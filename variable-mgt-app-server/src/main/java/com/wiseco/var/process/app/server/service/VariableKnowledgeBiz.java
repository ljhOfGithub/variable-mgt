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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.var.process.engine.compiler.ServiceExporter;
import com.wiseco.decision.model.engine.VarDto;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestClass;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.VariableFlowQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDto;
import com.wiseco.var.process.app.server.service.dto.VariableServiceIndexDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestClassService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wisecotech.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 变量知识库 业务实现
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/14
 */
@Slf4j
@Service
public class VariableKnowledgeBiz {

    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    @Autowired
    private ServiceExporter serviceExporter;

    @Autowired
    private VarProcessManifestClassService varProcessManifestClassService;

    @Autowired
    private VarProcessManifestVariableService varProcessManifestVariableService;

    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;

    /**
     * 导出并存储变量的内容
     * @param manifestDto 变量清单Dto
     * @param varIdentifiers 变量的唯一标识符
     * @throws Throwable 异常
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void exportAndStoreVariableKnowledge(VariableManifestDto manifestDto, List<String> varIdentifiers) throws Throwable {
        try {
            // 0. 获取待导出变量清单 DTO
            VarProcessManifest manifestEntity = manifestDto.getManifestEntity();
            if (null == manifestEntity) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "待编译变量清单不存在。");
            }
            // 获取用户名
            String userName;
            try {
                userName = SessionContext.getSessionUser().getUsername();
            } catch (Exception e) {
                // 如果用户名获取失败, 使用 system 作为用户名
                userName = CommonConstant.SYSTEM;
            }
            Long manifestId = manifestDto.getManifestEntity().getId();
            Integer manifestVersion = manifestDto.getManifestEntity().getVersion();
            // 1. 构建数据库压缩包描述索引
            List<VariableServiceIndexDto> indexDtoList = buildKnowledgeArchiveIndex(manifestDto, varIdentifiers);
            // 2. 导出实时服务, 获取知识库类文件 zip 压缩包
            Set<VarDto> varDefSet = new HashSet<>();
            VariableFlowQueryDto dto = new VariableFlowQueryDto();
            dto.setSpaceId(manifestEntity.getVarProcessSpaceId());
            dto.setManifestId(manifestId);
            List<VarProcessVariable> variableFlow = varProcessManifestVariableService.getVariableFlow(dto);
            for (VarProcessVariable variable : variableFlow) {
                VarDto varDto = new VarDto();
                varDto.setName(variable.getName());
                varDto.setType(variable.getDataType());
                varDto.setVersion(variable.getVersion());
                varDefSet.add(varDto);
            }
            // TODO
            byte[] classFileArchiveBytes = serviceExporter.exportService(manifestId, varDefSet, new HashMap<>(MagicNumbers.EIGHT));
            log.info("Variable service {} manifest {} (ver.{}): class file archive exported.", 1, manifestId, manifestVersion);

            // 3. 新建并保存/更新实时服务 class 表实体类
            VarProcessManifestClass newEntity = VarProcessManifestClass.builder()
                    .manifestId(manifestId)
                    .variableIndex(JSON.toJSONString(indexDtoList))
                    .variableClass(classFileArchiveBytes)
                    .createdUser(userName)
                    .updatedUser(userName)
                    .build();
            varProcessManifestClassService.remove(
                    new QueryWrapper<VarProcessManifestClass>().lambda()
                            .eq(VarProcessManifestClass::getManifestId, manifestId)
            );
            varProcessManifestClassService.save(newEntity);
            log.info("Variable service {} manifest {} (ver.{}): class file archive persisted.", 1, manifestId, manifestVersion);
        } finally {
            log.info("导出并存储变量的内容流程结束!");
        }
    }

    /**
     * 构建知识库树型索引 DTO
     *
     * @param manifestDto 变量清单 DTO
     * @param varIdentifiers 变量变量标识列表
     * @return 知识库树形索引 DTO List
     */
    private List<VariableServiceIndexDto> buildKnowledgeArchiveIndex(VariableManifestDto manifestDto, List<String> varIdentifiers) {

        // 0. 准备
        // 变量清单实体类
        VarProcessManifest manifestEntity = manifestDto.getManifestEntity();
        // 创建空白 DTO
        VariableServiceIndexDto indexDto = new VariableServiceIndexDto();
        // 获取变量空间, 服务实体类
        VarProcessSpace spaceEntity = varProcessSpaceService.getById(manifestEntity.getVarProcessSpaceId());
        List<ServiceInfoDto> serviceEntity = varProcessServiceVersionService.findserviceListByVersionIds(Collections.singletonList(manifestEntity.getServiceId()));

        // 1. 变量空间
        // 变量空间编码
        indexDto.setSpaceCode(spaceEntity.getCode());
        // 变量空间数据模型是否存在输入数据 (默认为 true)
        indexDto.setInput(true);
        // 变量空间数据模型是否存在内部数据 (默认为 true)
        indexDto.setInternalData(true);

        //内存模式是map
        indexDto.setDataMode("map");

        // 变量空间服务列表
        List<VariableServiceIndexDto.Service> services = new ArrayList<>(1);
        indexDto.setServices(services);

        // 2. 实时服务
        VariableServiceIndexDto.Service service = new VariableServiceIndexDto.Service();
        services.add(service);
        // 服务编码
        service.setServiceCode(CollectionUtils.isEmpty(serviceEntity) ? "" : serviceEntity.get(0).getCode());
        // 服务使用的变量
        List<VariableServiceIndexDto.Variable> variables = new ArrayList<>(1);
        service.setVars(variables);

        // 3. 服务使用的变量
        // 变量命名避免与 Java 10 保留字 var 冲突
        VariableServiceIndexDto.Variable variable = new VariableServiceIndexDto.Variable();

        // 变量清单 ID
        variable.setManifestId(manifestEntity.getId());

        // 变量清单发布变量标识符
        varIdentifiers.add(String.valueOf(manifestEntity.getId()));
        variable.setVarIdentifiers(varIdentifiers);

        // 输出变量标识符
        List<String> entryVarIdentifiers = new ArrayList<>(1);
        entryVarIdentifiers.add(String.valueOf(manifestEntity.getId()));
        variable.setEntryVarIdentifiers(entryVarIdentifiers);

        variables.add(variable);
        // 用 List 保存构建的树形结构, 方便 REST 服务拼接
        return Collections.singletonList(indexDto);
    }

}
