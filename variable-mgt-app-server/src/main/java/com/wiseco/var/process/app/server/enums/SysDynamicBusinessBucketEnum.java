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

@AllArgsConstructor
@Getter
public enum SysDynamicBusinessBucketEnum {

    //变量空间
    VARIABLE_MAIN_DESC("variable_main_desc", "变量空间", PermissionResourceConfigCodeEnum.VARIABLE_MAIN,
            PermissionResourceConfigCodeEnum.VARIABLE_SUMMARY, "/process/index/?"), VARIABLE_MAIN_ATTR(
            "variable_main_attr",
            "变量空间属性",
            PermissionResourceConfigCodeEnum.VARIABLE_MAIN,
            PermissionResourceConfigCodeEnum.VARIABLE_SUMMARY,
            "/process/index/?"),

    //变量管理
    VARIABLE_ADMIN("variable_admin", "变量版本", PermissionResourceConfigCodeEnum.VARIABLE_ADMIN, PermissionResourceConfigCodeEnum.VARIABLE_ADMIN,
            "/process/manage/detail?"),

    //数据模型
    VARIABLE_DATA_MODEL("variable_data_model", "数据模型对象", PermissionResourceConfigCodeEnum.VARIABLE_DATA_MODEL,
            PermissionResourceConfigCodeEnum.VARIABLE_DATA_MODEL, "/process/model?"),

    //外部服务引入
    VARIABLE_OUTSIDE_IN_OBJECT("variable_outside_in_object", "外部服务", PermissionResourceConfigCodeEnum.VARIABLE_OUTSIDE,
            PermissionResourceConfigCodeEnum.VARIABLE_OUTSIDE, "/process/outServiceImport?"),

    //公共函数
    VARIABLE_FUNCTION("variable_function", "公共函数", PermissionResourceConfigCodeEnum.VARIABLE_FUNCTION,
            PermissionResourceConfigCodeEnum.VARIABLE_FUNCTION, "/process/publicFunction/detail?"),
    //内部数据
    VARIABLE_INTERNAL("variable_internal_data", "内部数据", PermissionResourceConfigCodeEnum.VARIABLE_INTERNAL,
            PermissionResourceConfigCodeEnum.VARIABLE_INTERNAL, "/process/publicFunction/detail?"),

    //变量发布接口
    VARIABLE_SERVICE("variable_service", "实时服务", PermissionResourceConfigCodeEnum.VARIABLE_SERVICE_API,
            PermissionResourceConfigCodeEnum.VARIABLE_SERVICE_API, "/process/publish?"), VARIABLE_MANIFEST(
            "variable_interface",
            "变量清单",
            PermissionResourceConfigCodeEnum.VARIABLE_SERVICE_API,
            PermissionResourceConfigCodeEnum.VARIABLE_SERVICE_API,
            "/process/publish?"),

    //批量回溯
    BATCH_BACKTRACKING("batch_backtracking", "变量清单", PermissionResourceConfigCodeEnum.BATCH_BACKTRACKING, PermissionResourceConfigCodeEnum.BATCH_BACKTRACKING, "/process/backtracking?"),;
    private String code;
    private String desc;
    private PermissionResourceConfigCodeEnum type;
    private PermissionResourceConfigCodeEnum permission;

    private String url;

    /**
     * 根据code获取枚举
     *
     * @param code 枚举code
     * @return SysDynamicBusinessBucketEnum
     */
    public static SysDynamicBusinessBucketEnum getCode(String code) {
        for (SysDynamicBusinessBucketEnum location : SysDynamicBusinessBucketEnum.values()) {
            if (location.getCode().equals(code)) {
                return location;
            }
        }
        return null;
    }

}
