/*
 * Licensed to the Wiseco Software Corporation under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wiseco.var.process.app.server.service;

import com.wiseco.decision.engine.var.enums.RequestMsgFormatter;
import com.wiseco.decision.engine.var.runtime.VarContainer;
import com.wiseco.decision.engine.var.runtime.context.ServiceContext;
import com.wiseco.decision.engine.var.runtime.core.Engine;
import com.wiseco.decision.engine.var.runtime.core.Server;
import com.wiseco.decision.engine.var.utils.PackageGenerator;
import com.wiseco.var.process.app.server.commons.util.JarUtil;
import com.wiseco.var.process.app.server.controller.vo.input.VariableExecuteParam;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * @author zhangyang
 * @since 2024/03/28
 */
@Service
@Slf4j
public class TestVariableServiceBiz {

    private static final String BASIC_PATH = "/usr/local/src/vars";

    /**
     * 离线包执行实时服务
     *
     * @param param
     * @return 变量执行结果
     */
    public JSONObject offlineLibVarExecute(VariableExecuteParam param) {
        try {
            String serviceCode = param.getServiceCode();
            String msgFormat = param.getMsgFormat();
            String libPath = param.getLibPath();
            Engine engine = getEngine(serviceCode, libPath);
            if (engine == null) {
                throw new RuntimeException("engine构建失败");
            }
            RequestMsgFormatter msgFormatter = RequestMsgFormatter.JSON;
            if (RequestMsgFormatter.XML.name().equals(msgFormat)) {
                msgFormatter = RequestMsgFormatter.XML;
            }
            String requestContent = param.getRequestContent();
            ServiceContext serviceContext = engine.buildContext(requestContent, false, null, null,
                    null, null, null, null,
                    null, null, null, null, null, msgFormatter);
            engine.execute(serviceContext);
            return JSONObject.parseObject(JSONObject.toJSONString(serviceContext.getVars()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Engine getEngine(String serviceCode, String libPath) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Engine engine = null;
        File basePathFile = new File(BASIC_PATH);
        VarContainer varContainer = new VarContainer(basePathFile.toPath().toString(), false);
        Server server = varContainer.addServer(serviceCode);
        File file = findFileByServiceCode(serviceCode, libPath);
        if (file != null) {
            com.wiseco.decision.engine.var.runtime.core.Service service = server.addService(serviceCode);
            engine = service.addEngine(serviceCode);
            Map<String, byte[]> varClsMap = JarUtil.extractClassFromFile(file);
            Map<String, Class<?>> clsMap = engine.loadClassMap(varClsMap);
            engine.setRawDataClass(clsMap.get(MessageFormat.format("{0}.rawData", PackageGenerator.ENGINE_PACKAGE)));
            engine.setVarsClass(clsMap.get(MessageFormat.format("{0}.vars", PackageGenerator.ENGINE_PACKAGE)));
            for (Map.Entry<String, Class<?>> entry : clsMap.entrySet()) {
                String key = entry.getKey();
                String[] split = key.split("\\.");
                String className = split[split.length - 1];
                if (className.contains("Component_")) {
                    Class<?> componentClass = clsMap.get(MessageFormat.format("{0}.{1}.{1}", PackageGenerator.ENGINE_PACKAGE, className));
                    if (componentClass != null) {
                        engine.addVar(className, componentClass);
                        engine.addEntryVarName(className);
                    }
                }
            }
        }
        return engine;
    }

    private File findFileByServiceCode(String serviceCode, String libPath) {
        File file = new File(libPath);
        if (file.exists()) {
            File[] fileList = file.listFiles();
            if (fileList != null && fileList.length != 0) {
                for (File f : fileList) {
                    String name = f.getName();
                    if (name.contains("varService-" + serviceCode)) {
                        return f;
                    }
                }
            }
        }
        return null;
    }
}
