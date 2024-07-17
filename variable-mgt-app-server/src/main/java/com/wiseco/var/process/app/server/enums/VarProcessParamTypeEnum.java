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
 * <p>
 * 审核参数枚举类
 * </p>
 *
 * @author guozhuoyi
 * @since 2023/8/8
 */
@AllArgsConstructor
@Getter
public enum VarProcessParamTypeEnum {
    /**
     * 审核参数枚举类型
     */
    VAR_REVIEW("变量审核", "var_review"),

    PREPROCESS_LOGIC_REVIEW("预处理逻辑审核", "process_logic_review"),

    PUBLIC_METHOD_REVIEW("公共方法审核", "public_method_review"),

    VAR_TEMPLATE_REVIEW("变量模板审核", "var_template_review"),

    VAR_LIST_REVIEW("变量清单审核", "var_list_review"),

    REAL_TIME_SERVICE_REVIEW("实时服务审核", "real_time_service_review"),

    BATCH_BACKTRACK_TASK_REVIEW("批量回溯任务审核", "batch_backtrack_task_review");

    /**
     * 参数名称
     */
    private String name;

    /**
     * 审核参数编码
     */
    private String code;

    /**
     * 根据枚举获取code
     *
     * @param o 枚举
     * @return 枚举code
     */
    public static String getVarProcessParamCode(VarProcessParamTypeEnum o) {
        return o.getCode();
    }

}
