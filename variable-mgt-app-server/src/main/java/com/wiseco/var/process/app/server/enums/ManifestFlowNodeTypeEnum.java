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
 * 决策流节点类型枚举
 *
 * @author liaody
 * @author Zhaoxiong Chen
 * @since 2021/12/13
 */
@Getter
@AllArgsConstructor
public enum ManifestFlowNodeTypeEnum {
    // 任务节点
    FLOW("flow", "流程信息"),
    // 条件分支节点
    SPLIT("split", "条件分支"),
    // 并行分支节点
    PARALLEL("parallel", "并行分支"),

    // 外部服务节点
    SERVICE("service", "外数调用"),

    // 变量加工节点
    VAR("var", "变量加工"),

    // 数据预处理节点
    PRE_PROCESS("pre_process", "数据预处理"),

    INTERNAL_DATA("internal_data", "内部数据获取"),

    INTERNAL_LOGIC("internal_logic", "内部逻辑计算");

    private final String code;
    private final String description;

    /**
     * 从枚举类名称取得对应枚举类
     *
     * @param name 枚举类名称 (不区分大小写)
     * @return 名称对应的枚举类
     */
    public static ManifestFlowNodeTypeEnum fromName(String name) {
        for (ManifestFlowNodeTypeEnum nodeType : values()) {
            if (nodeType.name().equalsIgnoreCase(name)) {
                return nodeType;
            }
        }
        return null;
    }
}
