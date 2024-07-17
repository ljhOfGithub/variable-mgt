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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资源配置：业务标识枚举
 *
 * @author wangxianli
 * @since 2022/1/12
 */
@AllArgsConstructor
@Getter
public enum PermissionResourceConfigCodeEnum {

    //系统日志
    SYS_LOG("sys_log", "系统日志"),

    //外部服务
    EXTERNAL_MAIN("external_main", "外部服务"),

    //领域
    DOMAIN_MAIN("domain_main", "领域"),

    DOMAIN_INDEX("domain_index", "领域首页"), DOMAIN_SUMMARY("domain_summary", "概述"), DOMAIN_TEAM("domain_team", "团队管理"), DOMAIN_RESOURCE(
            "domain_resource",
            "资源管理"),

    DOMAIN_SERVICE("domain_service", "决策服务"),

    DOMAIN_COMMON_SERVICE("domain_common_service", "公共决策模块"),

    DOMAIN_DATA_MODEL("domain_data_model", "数据模型"),

    DOMAIN_BLAZE_SERVICE("domain_blaze_service", "blaze决策模块"),

    DOMAIN_AB("domain_ab", "A/B测试"),

    DOMAIN_SERVICE_API("domain_service_api", "决策服务接口"), DOMAIN_DATA_QUERY("domain_data_query", "决策数据查询"),

    DOMAIN_OUTSIDE("domain_outside", "外部服务"),

    DOMAIN_STRATEGY_PARAM("domain_strategy_param", "策略参数"),

    DOMAIN_ROSTER("domain_roster", "名单管理"), DOMAIN_ROSTER_IN("domain_roster_in", "在库名单"), DOMAIN_ROSTER_OUT("domain_roster_out", "出库名单"), DOMAIN_ROSTER_TYPE(
            "domain_roster_type",
            "名单类型"),

    SERVICE_MAIN("service_main", "决策服务"),

    BUCKET_MAIN("bucket_main", "决策细分"),

    STRATEGY_MAIN("strategy_main", "策略"),

    STRATEGY_INDEX("strategy_index", "策略首页"), STRATEGY_SUMMARY("strategy_summary", "概述"), STRATEGY_DATA_BOARD("strategy_data_board", "数据看板"), STRATEGY_PROCESS(
            "strategy_process",
            "主流程"),

    STRATEGY_COMPONENT("strategy_component", "决策组件"), STRATEGY_DATA_VAR("strategy_data_var", "数据与变量"),

    //策略-外部服务
    STRATEGY_OUTSIDE("strategy_outside", "外部服务"), STRATEGY_VARIABLE("strategy_variable", "实时服务"), STRATEGY_COMMON("strategy_common", "公共决策模块"),

    DIRECTORY_MAIN("directory_main", "文件夹"),

    COMPONENT_MAIN("component_main", "组件"),

    //变量空间
    VARIABLE_MAIN("variable_main", "变量空间"),

    VARIABLE_INDEX("variable_index", "空间首页"), VARIABLE_SUMMARY("variable_summary", "概述"), VARIABLE_TEAM("variable_team", "团队管理"), VARIABLE_RESOURCE(
            "variable_resource",
            "资源管理"),

    VARIABLE_ADMIN("variable_admin", "变量管理"),

    VARIABLE_DATA_MODEL("variable_data_model", "数据模型"),

    VARIABLE_OUTSIDE("variable_outside", "外部服务引入"),

    VARIABLE_INTERNAL("variable_internal", "内部数据管理"),

    VARIABLE_PREP("variable_prep", "数据预处理"), VARIABLE_FUNCTION("variable_function", "公共函数"),

    VARIABLE_ROSTER("variable_roster", "名单管理"), VARIABLE_ROSTER_IN("variable_roster_in", "在库名单"), VARIABLE_ROSTER_OUT("variable_roster_out", "出库名单"), VARIABLE_ROSTER_TYPE(
            "variable_roster_type",
            "名单类型"),

    VARIABLE_SERVICE_API("variable_service_api", "变量发布"), VARIABLE_DATA_QUERY("variable_data_query", "结果查询"), BATCH_BACKTRACKING("batch_backtracking", "批量回溯"),;

    private String code;
    private String message;

}
