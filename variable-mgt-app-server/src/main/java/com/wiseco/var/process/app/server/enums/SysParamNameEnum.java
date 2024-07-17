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
 * 系统参数名称枚举类
 *
 * @author Zhaoxiong Chen
 * @since 2022/4/7
 */
@Getter
@AllArgsConstructor
public enum SysParamNameEnum {

    // 外部服务创建
    DATA_CACHE("dataCache", "外部服务数据缓存天数"),
    RETRY_COUNT("retryCount", "外部服务调用失败重试次数"),
    SINGLE_TIMEOUT("singleTimeout", "外部服务单笔调用超时时间 (ms)"),
    TOTAL_TIMEOUT("totalTimeout", "外部服务调用总超时时间 (ms)"),

    /**
     * Rest 服务网关
     * 后端在保存 REST 地址时, 内容起始和结束不需要保存 "/"
     * 前端拼接逻辑: gatewayUrl + "/" + "ds_service_interface.input.url"
     */
    GATEWAY_URL("gatewayUrl", "rest服务网关Url"),

    DECISION_URI("decisionUri", "决策服务URI"),
    VAR_URI("varUri", "变量服务URI"),

    // 高性能集群部署控制参数
    K8S("k8s", "是否调用k8s接口部署服务，1是0否"),
    NACOS("nacos", "是否调用nacos接口动态更新策略组件，1是0否"),

    // trace开关
    TRACE_TEST("traceTest", "策略测试&组件测试trace开关，1开0关"),

    //rest调用外部服务是否使用mock数据开关
    ENVIRONMENT("environment", "当前环境1-配置环境,2-运行环境,3-分析环境"),

    // 生产发布方式
    RELEASE_TYPE("release_type", "发布方式设置, 详见Wiki"),

    // 处理中重试次数
    DEALING_RETRY_TIMES("dealing_retry_times", "处理中重试次数"),

    // 处理中重试间隔时间(ms)
    DEALING_RETRY_INTERVAL("dealing_retry_interval", "处理中重试间隔时间(ms)");

    private final String name;

    private final String description;

    /**
     * getTypeEnum
     * @param name String
     * @return SysParamNameEnum
     */
    public static SysParamNameEnum getTypeEnum(String name) {
        for (SysParamNameEnum sysParamNameEnum : SysParamNameEnum.values()) {
            if (sysParamNameEnum.getName().equals(name)) {
                return sysParamNameEnum;
            }
        }
        return null;
    }
}
