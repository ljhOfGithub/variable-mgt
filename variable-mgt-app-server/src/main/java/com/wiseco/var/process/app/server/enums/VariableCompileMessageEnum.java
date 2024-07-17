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
 * 组件编译提示
 *
 * @author wangxianli
 */
@AllArgsConstructor
@Getter
public enum VariableCompileMessageEnum {

    //组件使用的变量与定义的不匹配

    VAR_DEL("使用的{0}[{1}]已删除"),

    VAR_ATTR_DISACCORD("使用的{0}[{1}]的属性不一致"),

    VAR_REF_DEL("定义的{0}{1}引用的对象[{2}]已删除"),

    VAR_REF_ATTR_DISACCORD("定义的{0}{1}引用的对象[{2}]的属性不一致"),

    VAR_NOT_USE("定义的{0}[{1}]未被使用"), VAR_NOT_INIT("本地变量[{0}]没有初始化就使用"),

    VAR_NOT_REF("使用的{0}[{1}]未在清单编辑页引入"),

    //公共函数是否存在
    FUNCTION_REF_NO_FOUND("引用的{0}[{1}]已删除"), FUNCTION_REF_DEL("引用的{0}[{1}]已删除"),FUNCTION_REF_NOT_ENABLED("引用的{0}[{1}]不处于启用状态"),

    //变量
    VARIABLE_REF_DEL("引用的变量[{0}]已删除"), VARIABLE_REF_OFF("引用的变量[{0}]不处于启用状态"), VARIABLE_REF_CHECK("流程缺少加工变量[{0}]"), VARIABLE_REF_List(
            "变量[{0}]已从变量清单移除"),

    //预处理逻辑
    PRE_REF_DEL("引用的预处理逻辑[{0}]已删除"), PRE_REF_OFF("引用的预处理逻辑[{0}]已下架"),

    //内部数据
    INTERNAL_DATA_REF_DEL("引用的内部数据[{0}]已删除"),

    //组件引用的名单类型
    ROSTER_DEL("组件引用的名单类型[{0}]已删除"),

    //引用的外部服务
    OUTSIDE_REF_STRATEGY_DEL("外部服务已删除"), OUTSIDE_REF_DISACCORD("引入的外部服务[{0}]已删除"), OUTSIDE_REF_RECEIVING_OBJECT_MISSING("引入的外部服务接收对象[{0}]不存在"),
    OUTSIDE_REF_AUTH_CODE_NOT_FOUND("引用的外部服务[{0}]未输入授权码"), OUTSIDE_REF_VALIDATE_FAIL("外数授权码校验失败");

    private String message;
}
