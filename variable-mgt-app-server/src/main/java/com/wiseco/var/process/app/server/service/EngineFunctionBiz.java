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

import com.wiseco.var.process.app.server.controller.vo.input.EngineFunctionTemplateInputDto;
import com.wisecotech.json.JSONObject;

/**
 * @author wangxianli
 * @since  2022/6/7
 */
public interface EngineFunctionBiz {
    /**
     * fillFunctionProvider
     *
     * @param baseObj 入参
     */
    @Deprecated
    void fillFunctionProvider(JSONObject baseObj);

    /**
     * 组装内置函数
     *
     * @param baseObj      表达式模板
     * @param templateNeed 是否是页面模板需要，true前端组装，false编译
     */
    void fillFunctionProvider(JSONObject baseObj, boolean templateNeed);

    /**
     * 获取内置函数EngineFunction的模版内容
     * @param inputDto 入参
     * @return 内置函数模版内容
     */
    JSONObject getEngineFunctionTemplate(EngineFunctionTemplateInputDto inputDto);

}
