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
 * 函数操作类型 枚举类
 *
 * @author wangxianli
 * @since 2022/6/10
 */
@AllArgsConstructor
@Getter
public enum FlowActionTypeEnum {
    /**
     * 操作类型
     */
    ADD("新增", FlowStatusEnum.EDIT),

    SUBMIT("提交", FlowStatusEnum.UNAPPROVED),

    DOWN("停用", FlowStatusEnum.DOWN),

    UP("启用", FlowStatusEnum.UP),

    APPROVED("审核通过", FlowStatusEnum.UP),

    REFUSE("审核拒绝", FlowStatusEnum.REFUSE),

    RETURN_EDIT("退回编辑", FlowStatusEnum.EDIT),

    DELETE("删除", FlowStatusEnum.DELETE),;

    private String desc;
    private FlowStatusEnum nextStatus;

}
