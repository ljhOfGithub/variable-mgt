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
package com.wiseco.var.process.app.server.commons.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ycc
 * @since 2023/4/21 12:11
 */
@AllArgsConstructor
@Getter
public enum BizExceptionMessage {
    /**
     * 数据库相关 （4000~4500）
     */
    TABLE_DONT_EXIST_IN_CK(4000, "ClickHouse中表[{0}]不存在"), CREATE_HIVE_TABLE_ERROR(4001, "创建hive表异常"),
    /**
     * 管理空间(4500~5000)
     */
    MODEL_GRADING_SCHEME_BIN_CANNOT_EMPTY(4501, "请填写分箱信息"), MODEL_GRADING_SCHEME_RESULT_CANNOT_EMPTY(4502, "请填写分级结果"), MODEL_GRADING_SCHEME_DECISION_TREE_CANNOT_EMPTY(
            4503,
            "请绘制决策树"), INIT_SCORE_CANT_BE_NULL(
            4504,
            "初始分不能为空"),

    DATA_ITEM_BIN_OPR_DONT_SUPPORT(4505, "数据项[{0}]的分箱定义中，不支持的逻辑操作符[{1}]"), DATA_ITEM_SCORE_IS_NULL(4506, "数据项[{0}]的分箱分值定义不能为空"), DATA_ITEM_SCORE_DATA_TYPE_ERROR(
            4507,
            "数据项[{0}]的分箱分值数据类型错误，请填写数值型数据"), GRADING_RESULT_INCLOUDE_TYPE_NOT_NULL(
            4508,
            "分级结果中包含方式不能为空！"), GRADING_RESULT_DICT_ITEM_DONT_EXIST(
            4509,
            "分级结果内置字典项不存在"), GRADING_RESULT_GROUP_VALUE_CANNOT_NULL(
            4510,
            "分级结果的分组低值和分组高值均不能为空"), GRADING_RESULT_GROUP_VALUE_ERROR(
            4511,
            "分组高值应大于分组低值"), GRADING_RESULT_GROUP_VALUE_DONT_CONTINUOUS(
            4512,
            "分组值定义区间不连续"), CANT_OPER_BECAUSE_IN_DEIT(
            4513,
            "编辑中的数据无法进行该操作！"),

    ONLY_SCORE_CARD_SCHEME_HAS_DATA_ITEM(4514, "只有评分卡类型的方案才具有数据项信息！"), SCHEME_DATA_ITEM_DONT_EXIST(4515, "分级方案的数据项尚未定义！"), DATA_ELEMENT_IS_EMPTY(
            4516,
            "数据项定义查询结果为空"), DATA_ELEMENT_DONT_EXIST(
            4517,
            "数据项不存在或已停用[{0}]"), GRADING_DATA_ITEM_DONT_HAVE_VALUE(
            4518,
            "模型分级执行时数据项[{0}]值不存在！"), GRADING_DATA_ITEM_VALUE_ERROR(
            4519,
            "数据项[{0}]值为数值类型，当前填写错误无法解析！"),

    /**
     * 预测服务、模型部署相关(5000~5500)
     */
    FORECAST_SERVICE_DONT_EXIST(5000, "模型预测服务不存在！"), FORECAST_SERVICE_DONT_INCLUDE_MODEL(5001, "预测服务发布的模型中不包含当前模型版本！"), FORECAST_RESULT_DATA_IS_EMPTY(
            50002,
            "请检查所选预测服务是否存在预测结果数据"), TARGET_DATE_FORECAST_RESULT_IS_EMPTY(
            50003,
            "预测日期:{0},预测结果数据为空"), REAL_TIME_VERSION_DEPLOY_STATUS_NOT_SUCCESS_ERROR(
            50004,
            "当前实时预测服务版本的发布状态非发布成功状态"), BATCH_FORECAST_TASK_DONT_EXIST_ERROR(
            50005,
            "当前批量预测任务不存在或已删除"), MODEL_VERSION_CANT_PASS_DEPLOYE_POINT(
            50006,
            "选择的模型版本中模型文件不存在或生命周期未到可部署节点"),
    /**
     * 模型相关 5500~6000
     */
    MODEL_OR_VERSION_DONT_EXIST(5501, "模型或模型版本不存在"), MODEL_DONT_EXIST_OR_DEL(5502, "模型不存在或已删除"),

    /**
     * 变量相关 6001~6500
     */
    INVALID_VAR_NAME(6001, "变量名称{0}为系统内使用的默认变量名称，请使用其它名称！"),

    /**
     * 数据集相关 7001~7500
     */
    HIVE_TABLE_ARLEADY_EXIST(7001, "数据表名[{0}]已存在"), DATA_SET_ARLEADY_EXIST(7002, "数据表[{0}]已存在"), DATA_SET_DONT_HAVA_SUCCESS_RECORD(7003,
            "该数据集不存在导入成功的数据记录"), VAR_MAPPING_ORIGINAL_FEILD_IS_EMPTY(
            7004,
            "变量映射的数据源字段/数据列不能为空"), DATA_SET_DONT_EXIST(
            7005,
            "数据集不存在"), DATA_SET_HAS_IN_PROCESS_IMPORT_DATA_CANT_EDIT(
            7006,
            "该数据集存在正在导数的任务，暂不能编辑"), DELTE_FROM_HIVE_TABLE_ERROR(
            7007,
            "从hive数据库表{0}删除分区值为{1}的数据异常:{2}"), DATA_SET_IN_USE(
            7008,
            "该任务被使用中不可编辑"),

    /**
     * 模型监控相关 8001~8500
     */
    TAG_GROUP_NAME_REPEAT(8001, "分组名称重复"), ALREADY_EXIST_SAME_TAG_NAME(8002, "已存在该名称的标签！"), MONITOR_DATA_SET_IN_USE(8003, "数据集已被监控计划使用，无法清除数据并重建表"), DYNAMIC_REPORT_CONFIG_RELEASE_FAILED(
            8004,
            "发布失败:{0}"), MODEL_DONT_HAVE_REPORT_NEED_VAR(
            8005,
            "模型变量配置中不存在该报表所需的相关字段:[{0}]"), STATIC_REPORT_CONFIG_DONT_EXIST(
            8006,
            "该静态报表配置不存在"), NEED_SELECT_DATE_UNIT(
            8007,
            "请选择时间维度"), NEED_SELECT_BENCHMARK_DATA_TAG(
            8008,
            "请选择样本数据"), NEED_SELECT_SCORE_BIN_TYPE(
            8009,
            "请选择评分分箱方式"), DATE_UNIT_TYPE_DONT_SUPPORT(
            8010,
            "时间维度单位不支持"), NEED_SELECT_TAG_ID(
            8011,
            "请选择表现数据"), NEED_SELECT_TAG_BIN(
            8012,
            "请选择标签变量分箱方式"), PLAN_EXECUTE_JOB_IS_EXPIRED(
            8013,
            "该监控计划任务已失效，无法再启用"), MONI_MODEL_DONT_EXIST(
            8014,
            "监控模型不存在或已删除"), ALL_PLAN_REPORT_EXEC_FAILED(
            8015,
            "监控计划报表全部执行失败"), MUST_HAVE_ONE_OR_MORE_GOOD_GROUP(
            8016,
            "至少应该存在一个好客户分组"), MUST_HAVE_ONE_OR_MORE_BAD_GROUP(
            8017,
            "至少应该存在一个坏客户分组"), NOW_ONLY_SET_ONE_FILTER_CONDITION(
            8018,
            "目前过滤条件仅支持配置一个"), FILTER_CONDITION_GROUP_VAR_DONT_EXIST(
            8019,
            "过滤条件中的分组变量不存在"), FILTER_CONDITION_GROUP_TYPE_DONT_EXIST(
            8020,
            "过滤条件中的分组选择不存在"), DYNAMIC_REPORT_ROW_DATA_IS_NULL(
            8021,
            "行展示维度不能为空"), DYNAMIC_REPORT_COLUMN_DATA_IS_NULL(
            8022,
            "列展示维度不能为空"), DYNAMIC_REPORT_ROW_VAR_MUST_HAS_BIN(
            8023,
            "行展示维度中应至少选择一个存在分箱/分组定义的字段"), DYNAMIC_REPORT_COLUMN_VAR_MUST_HAS_BIN(
            8024,
            "列展示维度中应至少选择一个存在分箱/分组定义的字段"), DYNAMIC_REPORT_PREVIEW_ERROR(
            8025,
            "预览报表数据异常，请检查报表配置是否正确！"), REPORT_INDICATOR_CALCULATE_ERROR(
            8026,
            "报表指标计算异常"), FORECAST_SERVICE_CONFIG_PARAM_ERROR(
            8027,
            "预测服务配置缺少必填参数"), MODEL_CHARACTER_MUST_ATTR_IS_NULL(
            8023,
            "模型变量中非空变量不能为空"),

    /**
     * 通用类异常 9000~9999
     */
    QUERY_DATA_FAILED(9000, "查询数据失败"), UNSUPPORT_TYPE(9001, "不支持的类型"), MUST_NEED_PARAM_IS_NULL(9002, "必填参数不存在"), SELECT_MUST_NEED_OPTIONS(9003,
            "请选择必选查询条件:[{0}]"), DATA_NOT_FOUND(
            9004,
            "数据不存在"), DONT_COMPETE_COMMIT(
            9005,
            "正在处理，请勿重复提交！"), JOB_EXPRIED_TIME_INVALID(
            9006,
            "失效时间设置不合理，不存在下一次任务执行时间！"), DONT_SUPPORT_FREQUNCE_TYPE(
            9007,
            "不支持的执行频率类型"), FUNCTION_CANT_USE_NOW(
            9008,
            "功能暂未开放"), ALREADY_EXIST_SAME_NAME_DATA(
            9009,
            "已存在相同名称数据！"), DATE_FORMAT_ERROR(
            9010,
            "日期格式错误！"), READ_MUILTIPART_FILE_CONTENT_ERROR(
            9011,
            "读取上传文件内容异常"), UNSUPPORT_OPERATOR(
            9012,
            "不支持的操作符"), UNSUPPORT_OPER(
            9013,
            "不支持的操作"), NOT_EMPTY(
            9014,
            "不能为空"),

    UNKNOWN_ERROR(9999, "系统异常");;
    private Integer code;
    private String msg;

}
