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
package com.wiseco.var.process.app.server.exception;

import com.wiseco.boot.commons.exception.WisecoErrorCode;

/**
 * 睿信业务错误码
 *
 * @author Gmm
 * @date 2023/12/12
 */
public enum VariableMgtErrorCode implements WisecoErrorCode {

    /**
     * 通用
     */
    COMMON_SPACE_NOT_FOUND("001", "变量空间不存在或未配置"),
    COMMON_INVALID_OPERATION("002", "无效的操作"),
    COMMON_INVALID_INPUT("003", "无效的输入"),
    COMMON_CHECK_FAIL("004", "校验不通过"),
    COMMON_WARNING("005", "校验确认提示"),
    COMMON_PERMISSION_DENIED("006", "权限不足"),
    COMMON_FILE_ERROR("007", "文件操作错误"),
    COMMON_FILE_READE_ERROR("008", "文件读取错误"),
    COMMON_FILE_DATA_ERROR("009", "文件数据有误"),
    COMMON_FILE_WRITE_ERROR("010", "文件写入错误"),
    COMMON_FILE_UPLOAD_ERROR("011", "文件上传错误"),
    COMMON_DATABASE_ERROR("012", "数据库操作错误"),
    COMMON_ENUM_ERROR("013", "未知枚举类型"),
    COMMON_INTERFACE_UNREALIZED("014", "接口未实现"),

    /**
     * 公共配置
     */
    CONFIG_DEFAULT_VALUE_NOT_CONFIG("config_001", "缺失值定义未配置"),
    CONFIG_DICT_NOT_CONFIG("config_002", "字典或字典项未配置"),
    CONFIG_CATEGORY_NOT_CONFIG("config_003", "业务分类未配置"),
    CONFIG_TAG_NOT_CONFIG("config_004", "标签未配置"),
    CONFIG_PARAM_NOT_CONFIG("config_005", "通用参数未配置"),
    CONFIG_EXCEPTION_NOT_CONFIG("config_006", "异常值未配置"),
    CONFIG_TAG_GROUP_NOT_CONFIG("config_007", "标签组未配置"),
    CONFIG_DICT_ALREADY_USED("config_008", "试图删除已经使用的字典，导致异常"),

    /**
     * 数据模型
     */
    MODEL_NOT_FOUND("model_001", "数据模型不存在，或查询不到"),
    MODEL_EXISTS("model_002", "数据模型已存在，不允许重复"),
    MODEL_CHANGED("model_003", "数据模型被修改，导致当前操作失败或异常"),
    MODEL_STATUS_NO_MATCH("model_004", "数据模型的状态或引用关系不匹配，导致异常"),
    MODEL_NOT_EXTEND("model_005", "非拓展数据"),
    MODEL_RETURN_DATA_NO_MAPPING_TABLE("model_006", "返回数据中的对象不存在映射表"),
    MODEL_RETURN_DATA_NOT_DEFINE("model_007", "返回数据中的对象未定义或不完整"),
    MODEL_OUTSIDE_NOT_DEFINE("model_008", "外部服务请求数据结构未定义"),
    MODEL_OUTSIDE_NO_ROOT_OBJ("model_009", "外数没有根对象"),
    MODEL_NO_SQL("model_010", "数据模型的SQL语句不能为空"),
    MODEL_NO_SQL_PARAM("model_011", "数据模型的SQL参数信息未定义"),
    MODEL_NO_SQL_TABLE("model_012", "数据模型的SQL表信息找不到"),
    MODEL_ROOT_OBJ_NOT_MAT("model_013", "Json根对象不匹配"),
    MODEL_ROOT_OBJ_NOT_ONLY("model_014", "Json根对象不唯一"),
    MODEL_TYPE_NOT_SUPPORT("model_015", "数据模型类型错误"),


    /**
     * 公共函数(变量模版、预处理、公共方法)
     */
    FUNCTION_NOT_FOUND("function_001", "function信息不存在，或查询不到"),
    FUNCTION_EXISTS("function_002", "function信息(编码/名称/中文名/identifer)已存在，不允许重复"),
    FUNCTION_CHANGED("function_003", "function信息被修改，导致当前操作失败或异常"),
    FUNCTION_STATUS_NO_MATCH("function_004", "function的状态或引用关系不匹配，导致异常"),
    FUNCTION_INVALID_CONTENT("function_005", "function的content内容有误"),

    /**
     * 变量（变量）
     */
    VARIABLE_NOT_FOUND("variable_001", "变量信息不存在，或查询不到"),
    VARIABLE_EXISTS("variable_002", "变量信息(编码/名称/中文名/identifer)已存在，不允许重复"),
    VARIABLE_CHANGED("variable_003", "变量信息被修改，导致当前操作失败或异常"),
    VARIABLE_STATUS_NO_MATCH("variable_004", "变量的状态或引用关系不匹配，导致异常"),
    VARIABLE_INVALID_CONTENT("variable_005", "变量content内容有误"),
    VARIABLE_ADD_ERROR("variable_006", "批量生成变量时，添加变量异常"),

    /**
     * 清单
     */
    MANIFEST_NOT_FOUND("manifest_001", "清单信息不存在，或查询不到"),
    MANIFEST_EXISTS("manifest_002", "清单信息(编码/名称/中文名/identifer)已存在，不允许重复"),
    MANIFEST_CHANGED("manifest_003", "清单信息被修改，导致当前操作失败或异常"),
    MANIFEST_STATUS_NO_MATCH("manifest_004", "清单的状态或引用关系不匹配，导致异常"),
    MANIFEST_INVALID_CONTENT("manifest_005", "清单的content内容有误"),
    MANIFEST_CREATE_TABLE_FAIL("manifest_006", "创建清单结果表失败"),
    MANIFEST_PUBLISH_FAIL("manifest_007", "变量清单发布到nacos失败"),
    MANIFEST_OUTSIDE_CHECK_FAIL("manifest_008", "外数授权码校验失败"),

    /**
     * 服务
     */
    SERVICE_NOT_FOUND("service_001", "服务信息不存在，或查询不到"),
    SERVICE_EXISTS("service_002", "服务信息(编码/名称/中文名/identifer)已存在，不允许重复"),
    SERVICE_CHANGED("service_003", "服务信息被修改，导致当前操作失败或异常"),
    SERVICE_STATUS_NO_MATCH("service_004", "服务的状态或引用关系不匹配，导致异常"),

    /**
     * 服务授权
     */
    SERVICE_AUTH_NOT_FOUND("service_auth_001", "未查询到服务授权相关信息"),

    /**
     * 批量回溯
     */
    BACK_TRACKING_SAVE_PARAM_FAIL("back_tracking_001", "批量回溯保存参数错误"),
    BACK_TRACKING_NOT_FOUND("back_tracking_001", "批量回溯信息不存在，或查询不到"),
    BACK_TRACKING_EXISTS("back_tracking_002", "批量回溯信息(编码/名称/中文名/identifer)已存在，不允许重复"),
    BACK_TRACKING_CHANGED("back_tracking_003", "批量回溯信息被修改，导致当前操作失败或异常"),
    BACK_TRACKING_STATUS_NO_MATCH("back_tracking_004", "批量回溯的状态或引用关系不匹配，导致异常"),
    BACK_TRACKING_ANALYSE("back_tracking_005", "批量回溯整体分析异常"),
    BACK_TRACKING_NO_RESULT("back_tracking_006", "批量回溯查询不到结果数据"),
    BACK_TRACKING_EXECUTE_ERROR("back_tracking_007", "批量回溯执行异常"),
    BACK_TRACKING_RESULT_SAVE_ERROR("back_tracking_008", "批量回溯结果保存异常"),
    BACK_TRACKING_PAUSE_EXECUTE_ERROR("back_tracking_009", "批量回溯暂停执行异常"),
    BACK_TRACKING_RESULT_EXPORT_ERROR("back_tracking_010", "批量回溯结果导出异常"),
    BACK_TRACKING_File_Preview_ERROR("back_tracking_011", "批量回溯文件预览异常"),
    /**
     * 统计分析
     */
    STAT_("stat_001", ""),

    /**
     * 监控预警
     */
    MONITOR_("monitor_001", ""),

    /**
     * 数据(内部数据)
     */
    DATA_TYPE_IS_EMPTY("data_001", "内部数据类型不能为空"),
    DATA_SOURCE_IS_EMPTY("data_002", "内部数据来源不能为空"),
    DATA_PREVIEW_ERROR("data_003", "数据预览异常"),

    /**
     * 编译测试
     */
    TEST_RESULT_NOT_FOUND("test_001", "测试数据集不存在，或查询不到"),
    TEST_COMPILE_VALIDATE_FAILED("test_999", "编译验证失败"),

    // 表达式双层数组循环 10003
    TEMPLATE_ARRAY_LOOP("10004", "表达式双层数组循环"),

    /**
     * 场景
     */
    SCENE_NAME_EXISTS("scene_001", "场景名称重复"),
    SCENE_CODE_EXISTS("scene_002", "场景编码重复"),
    SCENE_DATA_SOURCE_BOUND("scene_003", "数据源已被绑定，无法选择"),
    SCENE_DATA_MODEL_BOUND("scene_004", "数据模型已被绑定，无法选择"),
    SCENE_INPUT_MISS("scene_005", "变量角色定义不完全"),;

    private String code;
    private String message;

    VariableMgtErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
