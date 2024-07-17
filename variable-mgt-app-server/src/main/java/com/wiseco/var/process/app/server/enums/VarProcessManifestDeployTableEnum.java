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
package com.wiseco.var.process.app.server.enums;

import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigExcept;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTag;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTagGroup;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDict;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDictDetails;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionClass;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionExcept;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessInternalData;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestClass;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestInternal;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestOutside;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.repository.entity.VarProcessService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableClass;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableExcept;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableReference;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableTag;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableVar;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变量清单发布包内容项目-数据库实体枚举类
 *
 * @author wangxianli
 * @since 2022/9/28
 */
@Getter
@AllArgsConstructor
public enum VarProcessManifestDeployTableEnum {

    //空间
    VAR_PROCESS_SPACE("var_process_space", VarProcessSpace.class),

    //空间配置
    VAR_PROCESS_CATEGORY("var_process_category", VarProcessCategory.class), VAR_PROCESS_CONFIG_DEFAULT_VALUE("var_process_config_default",
            VarProcessConfigDefault.class), VAR_PROCESS_CONFIG_EXCEPTION_VALUE(
            "var_process_config_exception",
            VarProcessConfigExcept.class), VAR_PROCESS_CONFIG_TAG_GROUP(
            "var_process_config_tag_group",
            VarProcessConfigTagGroup.class), VAR_PROCESS_CONFIG_TAG(
            "var_process_config_tag",
            VarProcessConfigTag.class),

    //数据模型
    VAR_PROCESS_DATA_MODEL("var_process_data_model", VarProcessDataModel.class), VAR_PROCESS_DICT("var_process_dict", VarProcessDict.class), VAR_PROCESS_DICT_DETAILS(
            "var_process_dict_details",
            VarProcessDictDetails.class),

    //外部服务引入
    VAR_PROCESS_OUTSIDE_SERVICE_REF("var_process_outside_ref", VarProcessOutsideRef.class),

    //变量
    VAR_PROCESS_VARIABLE("var_process_variable", VarProcessVariable.class), VAR_PROCESS_VARIABLE_CLASS("var_process_variable_class",
            VarProcessVariableClass.class), VAR_PROCESS_VARIABLE_EXCEPTION_VALUE(
            "var_process_variable_exception",
            VarProcessVariableExcept.class), VAR_PROCESS_VARIABLE_FUNCTION(
            "var_process_variable_function",
            VarProcessVariableFunction.class), VAR_PROCESS_VARIABLE_REFERENCE(
            "var_process_variable_reference",
            VarProcessVariableReference.class), VAR_PROCESS_VARIABLE_VAR(
            "var_process_variable_var",
            VarProcessVariableVar.class), VAR_PROCESS_VARIABLE_TAG(
            "var_process_variable_tag",
            VarProcessVariableTag.class),

    //公共函数
    VAR_PROCESS_FUNCTION("var_process_function", VarProcessFunction.class), VAR_PROCESS_FUNCTION_CLASS("var_process_function_class",
            VarProcessFunctionClass.class), VAR_PROCESS_FUNCTION_EXCEPTION_VALUE(
            "var_process_function_exception",
            VarProcessFunctionExcept.class), VAR_PROCESS_FUNCTION_REFERENCE(
            "var_process_function_reference",
            VarProcessFunctionReference.class), VAR_PROCESS_FUNCTION_VAR(
            "var_process_function_var",
            VarProcessFunctionVar.class),

    //内部数据
    VAR_PROCESS_INTERNAL_DATA("var_process_internal_data", VarProcessInternalData.class),

    //实时服务
    VAR_PROCESS_SERVICE("var_process_service", VarProcessService.class),
    //VAR_PROCESS_SERVICE_DOMAIN("var_process_service_domain", VarProcessServiceDomain.class),
    //VAR_PROCESS_SERVICE_REF_OBJECT("var_process_service_ref_object", VarProcessServiceRefObject.class),

    //变量清单
    VAR_PROCESS_MANIFEST("var_process_manifest", VarProcessManifest.class), VAR_PROCESS_MANIFEST_CLASS("var_process_manifest_class",
            VarProcessManifestClass.class), VAR_PROCESS_MANIFEST_FUNCTION(
            "var_process_manifest_function",
            VarProcessManifestFunction.class), VAR_PROCESS_MANIFEST_INTERNAL_DATA(
            "var_process_manifest_internal",
            VarProcessManifestInternal.class), VAR_PROCESS_MANIFEST_DATA_MODEL(
            "var_process_manifest_data_model",
            VarProcessManifestDataModel.class), VAR_PROCESS_MANIFEST_OUTSIDE_SERVICE(
            "var_process_manifest_outside",
            VarProcessManifestOutside.class), VAR_PROCESS_MANIFEST_VAR(
            "var_process_manifest_var",
            VarProcessManifestVar.class), VAR_PROCESS_MANIFEST_VARIABLE(
            "var_process_manifest_variable",
            VarProcessManifestVariable.class),

    //批量回溯
    BATCH_BACKTRACKING("var_process_batch_backtracking", VarProcessBatchBacktracking.class),;
    private String tableName;
    private Class clazz;

    /**
     * 根据枚举表名获取枚举
     *
     * @param tableName 枚举表名
     * @return VarProcessManifestDeployTableEnum
     */
    public static VarProcessManifestDeployTableEnum getEnum(String tableName) {
        for (VarProcessManifestDeployTableEnum tableEntum : VarProcessManifestDeployTableEnum.values()) {
            if (tableEntum.getTableName().equals(tableName)) {
                return tableEntum;
            }
        }
        return null;
    }
}
