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

import com.wiseco.decision.common.enums.GlobalEnum;
import com.wiseco.decision.common.enums.IResultCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务提示信息
 * <p>命名方式: ${业务}_${操作}_${备注}_${CONFIRM/REJECT}</p>
 *
 * @author wangxianli
 * @since 2022/6/9
 */
@Getter
@AllArgsConstructor
public enum BizCodeMessageEnum implements IResultCode {

    //数据模型字段 (领域, 公共决策模块)
    DOMAIN_DICT_DEL_USED_BY_MODEL_REJECT(GlobalEnum.BIZ_EXCEPTION.getCode(), "该字典类型已被使用，不允许删除。"), DOMAIN_DICT_DEL_DEV_EXIST_CONFIRM(
            GlobalEnum.CONFIRM
                    .getCode(),
            "该字典类型下已有字典项，确认删除？"), DOMAIN_DICT_NOT_DETAIL_DEL_EXIST_CONFIRM(
            GlobalEnum.CONFIRM
                    .getCode(),
            "确认删除该字典类型？"),

    // 变量空间
    VAR_SPACE_REMOVE_CONFIRM(GlobalEnum.CONFIRM.getCode(), "确认删除该变量空间？"),

    //变量分类
    VAR_CATEGORY_REMOVE_CONFIRM(GlobalEnum.CONFIRM.getCode(), "确认删除?"),

    VAR_CATEGORY_EDIT_CONFIRM(GlobalEnum.CONFIRM.getCode(), "该分类已被使用，确认编辑?"),

    // 变量加工-外部服务引入
    VAR_PROCESS_OUTSIDE_SERVICE_CANCEL_REFERENCE_USING_BY_VARIABLES_REJECT(GlobalEnum.BIZ_EXCEPTION.getCode(), "外部服务正在被实时服务使用，无法取消引入。"),

    // 变量清单 - 提交测试校验
    VAR_MANIFEST_SUBMIT_FOR_TESTING_VARIABLE_NOT_LISTED_DELETE(GlobalEnum.BIZ_EXCEPTION.getCode(), "该服务下的变量%s处于已删除状态，不允许提交。"), VAR_MANIFEST_SUBMIT_FOR_TESTING_VARIABLE_NOT_LISTED_REJECT(
            GlobalEnum.BIZ_EXCEPTION
                    .getCode(),
            "该服务下的变量%s未启用，不允许提交。"), VAR_MANIFEST_SUBMIT_FOR_TESTING_DATA_MODEL_BINDING_MISSING_REJECT(
            GlobalEnum.BIZ_EXCEPTION
                    .getCode(),
            "变量清单下所有的数据模型绑定的数据来源配置不完善。"), VAR_MANIFEST_SUBMIT_FOR_TESTING_EXTENDED_DATA_NOT_PRE_PROCESSED_REJECT(
            GlobalEnum.BIZ_EXCEPTION
                    .getCode(),
            "该服务依赖的扩展数据%s未定义预处理逻辑，不可提交。"), VAR_MANIFEST_SUBMIT_FOR_TESTING_PRE_PROCESS_LOGIC_NOT_LISTED_REJECT(
            GlobalEnum.BIZ_EXCEPTION
                    .getCode(),
            "该清单依赖的预处理逻辑%s不处于启用状态，不允许提交。"), VAR_MANIFEST_SUBMIT_FOR_TESTING_VARIABLE_TEMPLATE_NOT_LISTED_REJECT(
            GlobalEnum.BIZ_EXCEPTION
                    .getCode(),
            "该清单依赖的变量模板%s不处于启用状态，不允许提交。"), VAR_MANIFEST_SUBMIT_FOR_TESTING_COMMON_METHOD_NOT_LISTED_REJECT(
            GlobalEnum.BIZ_EXCEPTION
                    .getCode(),
            "该清单依赖的公共方法%s不处于启用状态，不允许提交。"),

    // 变量清单 - 状态变更确认提示
    VAR_MANIFEST_SUBMIT_FOR_TESTING_CONFIRM(GlobalEnum.CONFIRM.getCode(), "提交审核后该服务将不可修改，确认提交？"), VAR_MANIFEST_APPLY_FOR_VERIFY_CONFIRM(
            GlobalEnum.CONFIRM
                    .getCode(),
            "确认提交审核？"), VAR_MANIFEST_APPLY_FOR_PUBLISH_CONFIRM(
            GlobalEnum.CONFIRM
                    .getCode(),
            "确认申请上线？"), VAR_MANIFEST_STEP_BACK_CONFIRM(
            GlobalEnum.CONFIRM
                    .getCode(),
            "确认将该变量清单退回编辑状态？"), VAR_MANIFEST_APPROVE_CONFIRM(
            GlobalEnum.CONFIRM
                    .getCode(),
            "审核通过后将自动启用该变量清单，确认审核通过？"), VAR_MANIFEST_DISABLE_CONFIRM(
            GlobalEnum.CONFIRM
                    .getCode(),
            "确认停用该变量清单？"), VAR_MANIFEST_RE_ENABLE_CONFIRM(
            GlobalEnum.CONFIRM
                    .getCode(),
            "确认重新启用该变量清单？");

    private final String code;
    private final String message;
}
