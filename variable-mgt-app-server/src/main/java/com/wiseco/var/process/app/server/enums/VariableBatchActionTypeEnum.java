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

import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变量操作类型
 *
 * @author wangxianli
 */
@AllArgsConstructor
@Getter
public enum VariableBatchActionTypeEnum {
    /**
     * 变量批量操作类型
     */
    BATCH_SUMMIT(2, "批量提交"),

    BATCH_DOWN(3, "批量停用"),

    BATCH_RE_ENABLE(4, "批量启用"),

    BATCH_APPROVED(6, "批量审核通过"),

    BATCH_REFUSE(7, "批量审核拒绝"),

    BATCH_RETURN_EDIT(8, "批量退回"),

    BATCH_DELETE(9, "批量删除");

    private Integer code;
    private String desc;

    /**
     * 根据code获取对应枚举
     *
     * @param code 枚举code
     * @return VariableActionTypeEnum
     */
    public static VariableBatchActionTypeEnum getStatus(Integer code) {
        for (VariableBatchActionTypeEnum statusEnum : VariableBatchActionTypeEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "非法操作类型");
    }

}
