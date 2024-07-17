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
 * @author wangxianli
 */
@AllArgsConstructor
@Getter
public enum SysDynamicOperateTypeEnum {

    /**
     * 创建
     */
    CREATE("创建"),
    ADD("添加"),

    EDIT("编辑"),
    DELETE("删除"),
    REMOVE("移除"),
    COPY("复制"),
    SET("设置"),
    REFRESH("刷新"),

    VERSION_ASSIGN("版本分配"),

    INTRODUCE("引入"),
    CANCEL_INTRODUCE("取消引入"),
    START("启动"),
    STOP("停用"),
    RELEASE("发布"),
    CANCEL_RELEASE("取消发布"),
    REPUBLISH("重新发布"),
    IN_WAREHOUSE("入库"),
    EX_WAREHOUSE("出库"),
    AUTO_EX_WAREHOUSE("自动出库"),


    //组件
    CHECK_IN("检入"),
    CHECK_OUT("检出"),
    FORCE_CHECK_OUT("强制检出"),
    CANCEL_CHECK_OUT("取消检出"),
    RESTORE_VERSION("恢复版本"),

    //变量
    VAR_APPLY_UP("申请上架"),
    VAR_UP("上架"),
    VAR_DOWN("下架"),
    ENABLE("启用"),
    IMPORT("导入"),

    PROGRESS("进行"),

    LOGIN("登录"),
    LOGIN_OUT("退出"),
    USER_REGISTER("注册"),
    APPROVED("审核通过"),
    REFUSE("审核拒绝"),
    RETURN_EDIT("退回编辑");

    private String name;

    /**
     * getCodeEnum
     * @param name String
     * @return SysDynamicOperateTypeEnum
     */
    public static SysDynamicOperateTypeEnum getCodeEnum(String name) {
        for (SysDynamicOperateTypeEnum sysDynamicOperateTypeEnum : SysDynamicOperateTypeEnum.values()) {
            if (sysDynamicOperateTypeEnum.getName().equals(name)) {
                return sysDynamicOperateTypeEnum;
            }
        }
        return null;
    }
}

