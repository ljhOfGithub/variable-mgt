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
package com.wiseco.var.process.app.server.exception;

import com.wiseco.boot.commons.exception.ErrorCode;
import com.wiseco.boot.commons.exception.ErrorCodeTypeEnum;
import com.wiseco.boot.module.core.WisecoBootModulesEnum;
import com.wiseco.decision.common.enums.GlobalEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VarModuleErrorCode implements ErrorCode {

    /**
     * 数据模型
     */
    DOMAIN_DATA_MODEL_DEFAULT(WisecoBootModulesEnum.VAR_PROCESS_MODEL, ErrorCodeTypeEnum.DEFAULT_PROCESS_ERROR, "999", "已知的业务处理"), DOMAIN_DATA_MODEL_ADD(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "001",
            "数据模型-添加"), DOMAIN_DATA_MODEL_UPDATE(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "002",
            "数据模型-修改"), DOMAIN_DATA_MODEL_UPDATE_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "003",
            "数据模型-修改校验"), DOMAIN_DATA_MODEL_DETELE(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "004",
            "数据模型-删除"), DOMAIN_DATA_MODEL_DETELE_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "005",
            "数据模型-删除校验"), DOMAIN_DATA_MODEL_COPY(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "006",
            "数据模型-复制"), DOMAIN_DATA_MODEL_IMPORT(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "007",
            "数据模型-导入"), DOMAIN_DATA_MODEL_EXPORT_TMP(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "008",
            "数据模型-导出模板"), DOMAIN_DATA_MODEL_EXPORT(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "009",
            "数据模型-导出"), DOMAIN_DATA_MODEL_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "010",
            "数据模型-列表"), DOMAIN_DATA_MODEL_DETAIL(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "011",
            "数据模型-根据数据模型ID查询"), DOMAIN_DATA_MODEL_LIST_BY_DOMAIN_ID(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "012",
            "数据模型-根据领域ID查询"), DOMAIN_DATA_MODEL_LIST_EXCLUDE(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "013",
            "数据模型-根据领域ID查询不包含当前数据模型ID"), DOMAIN_DATA_MODEL_VARIABLE(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "014",
            "数据模型-获取数据变量树"), DOMAIN_DATA_MODEL_INPUT(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "015",
            "数据模型-获取input变量树"), DOMAIN_DATA_MODEL_VERISION(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "016",
            "数据模型-获取新版本号"), DOMAIN_DATA_MODEL_DIFF(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "017",
            "数据模型-版本对比"), DOMAIN_DATA_MODEL_REF(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "018",
            "数据模型-策略引用"),

    /**
     * 字典
     */
    DOMAIN_DICT_DEL_USED_BY_MODEL_REJECT(WisecoBootModulesEnum.VAR_PROCESS_MODEL, ErrorCodeTypeEnum.DEFAULT_PROCESS_ERROR, GlobalEnum.BIZ_EXCEPTION
            .getCode(), "该字典类型已被使用，不允许删除。"), DOMAIN_DICT_DEL_DEV_EXIST_CONFIRM(WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.DEFAULT_PROCESS_ERROR, GlobalEnum.CONFIRM.getCode(),
            "该字典类型下已有字典项，确认删除？"), DOMAIN_DICT_NOT_DETAIL_DEL_EXIST_CONFIRM(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.DEFAULT_PROCESS_ERROR,
            GlobalEnum.CONFIRM
                    .getCode(),
            "确认删除该字典类型？"), DOMAIN_DICT_DISABLE_DEV_EXIST_REJECT(
            WisecoBootModulesEnum.VAR_PROCESS_MODEL,
            ErrorCodeTypeEnum.DEFAULT_PROCESS_ERROR,
            GlobalEnum.BIZ_EXCEPTION
                    .getCode(),
            "该字典类型已被使用，不允许停用。"),

    /**
     * 变量管理
     */
    VARIABLE_SPACE_CONFIG_DEFAULT(WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT, ErrorCodeTypeEnum.DEFAULT_PROCESS_ERROR, "999", "已知的业务处理"), VARIABLE_SPACE_CONFIG_CATE_TREE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "001",
            "变量分类-分类数"), VARIABLE_SPACE_CONFIG_CATE_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "002",
            "变量分类-列表"), VARIABLE_SPACE_CONFIG_CATE_ADD(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "003",
            "变量分类-添加"), VARIABLE_SPACE_CONFIG_CATE_DEL_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "004",
            "变量分类-删除校验"), VARIABLE_SPACE_CONFIG_DEL(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "005",
            "变量分类-删除"), VARIABLE_SPACE_CONFIG_TAG_TREE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "010",
            "变量标签-标签树"), VARIABLE_SPACE_CONFIG_TAG_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "011",
            "变量标签-列表"), VARIABLE_SPACE_CONFIG_TAG_GROUP_SAVE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "012",
            "变量标签-添加或修改标签组"), VARIABLE_SPACE_CONFIG_TAG_GROUP_DEL(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "013",
            "变量标签-删除标签组"), VARIABLE_SPACE_CONFIG_TAG_ADD(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "014",
            "变量标签-添加标签"), VARIABLE_SPACE_CONFIG_TAG_DEL(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "015",
            "变量标签-删除标签"), VARIABLE_SPACE_CONFIG_DEFAULT_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "016",
            "缺失值-列表"), VARIABLE_SPACE_CONFIG_DEFAULT_EDIT(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "017",
            "缺失值-编辑"), VARIABLE_SPACE_CONFIG_EX_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "018",
            "异常值-列表"), VARIABLE_SPACE_CONFIG_EX_SAVE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "019",
            "异常值-保存"), VARIABLE_SPACE_CONFIG_EX_DEL_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "020",
            "异常值-删除校验"), VARIABLE_SPACE_CONFIG_EX_DEL(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "021",
            "异常值-删除"), VARIABLE_COMMON_DATA_MODEL_TREE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "001",
            "查询变量空间数据模型树形结构统一接口"), VARIABLE_COMMON_VAR_TREE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "002",
            "变量匹配树接口"), VARIABLE_COMMON_TEMPLATE_STATIC(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "010",
            "变量加工模板配置+所有dataProvider数据"), VARIABLE_COMMON_TEMPLATE_VAR_BY_PATH(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "011",
            "dataProvider追加数据接口"), VARIABLE_COMMON_TEMPLATE_PROVIDEER_ADD(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "012",
            "只有this_dataProvider追加数据接口"), VARIABLE_COMMON_TEMPLATE_PROVIDER_THIS(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "013",
            "根据变量路径（对象）获取一级基本属性"), VARIABLE_COMMON_TEMPLATE_PROVIDER_BY_PATH(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "014",
            "根据变量路径（对象）动态获取相对象"), VARIABLE_COMMON_TEMPLATE_BUCKET(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "015",
            "根据变量路径（对象）获取相对象数组"), VARIABLE_COMMON_TEMPLATE_OUTSIDE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "016",
            "根据变量路径（对象）一级属性对比"), VARIABLE_COMMON_TEMPLATE_PARENT_ATT(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "017",
            "获取数据变量类型集合"), VARIABLE_COMMON_TEMPLATE_SAME_TYPE_OBJ(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "018",
            "获取数据provider集合"), VARIABLE_COMMON_TEMPLATE_DICT_TYPE_OBJ(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "019",
            "获取字典数据provider集合"), VARIABLE_ADMIN_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "001",
            "变量列表"), VARIABLE_ADMIN_DETAILS(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "002",
            "变量详情"), VARIABLE_ADMIN_PROPERTIES(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "003",
            "变量属性"), VARIABLE_ADMIN_SAVE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "004",
            "保存变量"), VARIABLE_ADMIN_COPY_UP_DOWN(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "005",
            "复制上架或下架的变量"), VARIABLE_ADMIN_COPY(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "006",
            "复制变量"), VARIABLE_ADMIN_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "007",
            "验证变量"), VARIABLE_ADMIN_UP(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "008",
            "上架"), VARIABLE_ADMIN_UPDATE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "009",
            "修改状态"), VARIABLE_ADMIN_COMPARE_CONTENT(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "010",
            "内容比较"), VARIABLE_ADMIN_CACHE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "011",
            "临时缓存内容"), VARIABLE_ADMIN_RESTORE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "012",
            "恢复版本"), VARIABLE_ADMIN_DELETE_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "013",
            "删除变量校验"), VARIABLE_ADMIN_DELETE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "014",
            "删除变量"), VARIABLE_ADMIN_CREATE_CATEGARY(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "015",
            "创建变量分类"), VARIABLE_ADMIN_QUERY_CATEGARY(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "016",
            "查询变量分类"), VARIABLE_ADMIN_APPROVE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "017",
            "审核通过或拒绝"), VARIABLE_ADMIN_COMPARE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "018",
            "变量对比"), VARIABLE_ADMIN_COMPARE_VERSION(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "019",
            "变量对比信息"), VARIABLE_ADMIN_VERSION_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_MANAGEMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "020",
            "变量版本信息列表"),

    /**
     * 内部数据管理
     */
    VARIABLE_INTERNAL_DATA_DEFAULT(WisecoBootModulesEnum.VAR_PROCESS_DATA, ErrorCodeTypeEnum.DEFAULT_PROCESS_ERROR, "999", "已知的业务处理"), VARIABLE_INTERNAL_DATA_REF_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "001",
            "内部数据引入-列表"), VARIABLE_INTERNAL_DATA_REF_DETAIL(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "002",
            "内部数据引入-详情"), VARIABLE_INTERNAL_DATA_REF_CREATE(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "003",
            "内部数据引入-创建"), VARIABLE_INTERNAL_DATA_REF_CREATE_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "004",
            "内部数据引入-创建校验"), VARIABLE_INTERNAL_DATA_REF_SAVE(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "005",
            "内部数据引入-保存"), VARIABLE_INTERNAL_DATA_REF_SAVE_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "006",
            "内部数据引入-保存校验"), VARIABLE_INTERNAL_DATA_REF_TABLES(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "007",
            "内部数据引入-获取所有表结构"), VARIABLE_INTERNAL_DATA_REF_VIEW(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "008",
            "内部数据引入-数据预览"), VARIABLE_INTERNAL_DATA_REF_DELETE_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "009",
            "内部数据引入-验证删除"), VARIABLE_INTERNAL_DATA_REF_DELETE(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "010",
            "内部数据引入-删除"),

    VARIABLE_INTERNAL_DATA_TABLE_DELETE(WisecoBootModulesEnum.VAR_PROCESS_DATA, ErrorCodeTypeEnum.UNKNOWN_ERROR, "011", "内部数据表管理-删除数据表"), VARIABLE_INTERNAL_DATA_TABLE_CREATE(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "012",
            "内部数据表管理-创建数据表"), VARIABLE_INTERNAL_DATA_TABLE_QUERY(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "013",
            "内部数据表管理-查询数据表"), VARIABLE_INTERNAL_DATA_TABLE_DATA(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "014",
            "内部数据表管理-查询数据表结构"), VARIABLE_INTERNAL_DATA_TABLE_UPDATE(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "015",
            "内部数据表管理-更新数据表信息"),

    VARIABLE_INTERNAL_DATA_LIST(WisecoBootModulesEnum.VAR_PROCESS_DATA, ErrorCodeTypeEnum.UNKNOWN_ERROR, "016", "内部数据管理-查询表数据"), VARIABLE_INTERNAL_DATA_ADD(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "017",
            "内部数据管理-添加数据"), VARIABLE_INTERNAL_DATA_EDIT(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "018",
            "内部数据管理-修改数据"), VARIABLE_INTERNAL_DATA_EXPORT(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "019",
            "内部数据管理-导出数据"), VARIABLE_INTERNAL_DATA_EXPORT_ALL(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "020",
            "内部数据管理-导出所有数据"), VARIABLE_INTERNAL_DATA_IMPORT(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "021",
            "内部数据管理-导入数据"), VARIABLE_INTERNAL_DATA_BATCH_DELETE(
            WisecoBootModulesEnum.VAR_PROCESS_DATA,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "022",
            "内部数据管理-批量删除数据"),

    /**
     * 变量发布
     */
    VARIABLE_SEERVICE_DEFAULT(WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT, ErrorCodeTypeEnum.DEFAULT_PROCESS_ERROR, "999", "已知的业务处理"), VARIABLE_SEERVICE_TREE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "001",
            "实时服务和变量清单查询"), VARIABLE_SEERVICE_DETAIL(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "002",
            "实时服务详情查询"), VARIABLE_SEERVICE_ADD(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "003",
            "添加实时服务"), VARIABLE_SEERVICE_UPDATE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "004",
            "编辑实时服务"), VARIABLE_SEERVICE_OAUTH(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "005",
            "服务授权查询"), VARIABLE_SEERVICE_OAUTH_DOMAIN(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "006",
            "查询可以被授权的领域"), VARIABLE_SEERVICE_SAVE_OAUTH(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "007",
            "保存服务授权"), VARIABLE_SEERVICE_REMOVE_OAUTH(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "008",
            "移除服务授权"), VARIABLE_SEERVICE_DELETE_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "009",
            "删除实时服务校验"), VARIABLE_SEERVICE_DELETE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "010",
            "删除实时服务"), VARIABLE_SEERVICE_ATT(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "011",
            "获取实时服务属性信息"), VARIABLE_SEERVICE_API(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "012",
            "获取接口文档"), VARIABLE_SEERVICE_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "013",
            "获取空间下简单格式服务列表"),

    VARIABLE_SERVICE_MANIFEST_LIST(WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT, ErrorCodeTypeEnum.UNKNOWN_ERROR, "014", "查询服务下所有未删除变量清单"), VARIABLE_MANIFEST_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "029",
            "查询所有未删除的变量清单列表"), VARIABLE_SERVICE_MANIFEST_ADD(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "015",
            "添加变量清单"), VARIABLE_SERVICE_MANIFEST_DELETE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "016",
            "删除变量清单"), VARIABLE_SERVICE_MANIFEST_CONFIG(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "017",
            "获取变量清单配置"), VARIABLE_SERVICE_MANIFEST_SAVE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "018",
            "保存变量清单配置"), VARIABLE_SERVICE_MANIFEST_MAX_VARS(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "019",
            "查询所有变量最大已上架版本记录"), VARIABLE_SERVICE_MANIFEST_VARS(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "020",
            "查询指定变量所有已上架版本"), VARIABLE_SERVICE_MANIFEST_REF_OUTSIDE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "021",
            "查询变量空间引入的外部服务接收对象"), VARIABLE_SERVICE_MANIFEST_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "022",
            "校验内部/外部服务响应参数"), VARIABLE_SERVICE_MANIFEST_MAPPING(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "023",
            "获取外部服务入参映射对象"), VARIABLE_SERVICE_MANIFEST_BUIDING(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "024",
            "获取可用于变量清单调用流水号绑定的数据结构"), VARIABLE_SERVICE_REF_MANIFEST_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "025",
            "校验清单状态更新"), VARIABLE_SERVICE_MANIFEST_UPDATE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "026",
            "更新变量清单状态"), VARIABLE_SERVICE_MANIFEST_VERIFY(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "027",
            "提交审核"), VARIABLE_SERVICE_MANIFEST_ATT(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "028",
            "获取变量清单属性信息"),

    VARIABLE_SEERVICE_DEPLOY_APPLY_DATA_PREPARE(WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT, ErrorCodeTypeEnum.UNKNOWN_ERROR, "032",
            "上线申请-上线数据准备"), VARIABLE_SEERVICE_DEPLOY_APPLY_DATA_PREPARE_STATUS(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "033", "上线申请-查询数据准备进度"), VARIABLE_SEERVICE_DEPLOY_APPLY_CANCLE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "034",
            "上线申请-取消发布"), VARIABLE_SEERVICE_DEPLOY_APPLY_DOWN(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "035",
            "上线申请-下载上线包"), VARIABLE_SEERVICE_DEPLOY_APPLY_ONLINE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "036",
            "上线申请-申请上线"), VARIABLE_SEERVICE_DEPLOY_APPLY_ONLINE_STATUS(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "037",
            "上线申请-查询上线申请进度"), VARIABLE_SEERVICE_DEPLOY_APPLY_PUBLISH_STATUS(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "038",
            "上线申请-获取发布状态"), VARIABLE_SEERVICE_DEPLOY_APPLY_AGAIN(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "039",
            "上线申请-再次发布"), VARIABLE_SEERVICE_DEPLOY_PARSE_DOMAIN_IMPORT(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "040",
            "生产发布-导入领域上线包"), VARIABLE_SEERVICE_DEPLOY_PARSE_VARIABLE_SEERVICE_IMPORT(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "041",
            "生产发布-导入策略上线包"), VARIABLE_SEERVICE_DEPLOY_PARSE_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "042",
            "生产发布-上线申请校验(生产)"), VARIABLE_SEERVICE_DEPLOY_PARSE_APPLY(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "043",
            "生产发布-上线申请(生产)"), VARIABLE_SEERVICE_DEPLOY_PARSE_APPLY_STATUS(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "044",
            "生产发布-查询上线申请进度(生产)"), VARIABLE_SEERVICE_DEPLOY_PARSE_APPLY_RESULT(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "045",
            "生产发布-查询上线申请结果(生产)"), VARIABLE_SEERVICE_DOC_PAGE_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "046",
            "策略文档-分页查询列表"), VARIABLE_SEERVICE_DOC_LIST(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "047",
            "策略文档-列表"), VARIABLE_SEERVICE_DOC_DETAIL(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "048",
            "策略文档-详情"), VARIABLE_SEERVICE_DOC_ADD(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "049",
            "策略文档-添加"), VARIABLE_SEERVICE_DOC_UPDATE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "050",
            "策略文档-修改"), VARIABLE_SEERVICE_DOC_DELETE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "051",
            "策略文档-删除"), VARIABLE_SEERVICE_DOC_DOWN(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "052",
            "策略文档-下载"), VARIABLE_SEERVICE_DOC_FILE_VIEW(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "053",
            "策略文档-文件预览"), VARIABLE_SEERVICE_DOC_UPLOADFILE(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "054",
            "策略文档-上传文件"), VARIABLE_SEERVICE_DEPLOY_APPLY_DATA_PREPARE_CHECK(
            WisecoBootModulesEnum.VAR_PROCESS_METRIC_DEPLOYMENT,
            ErrorCodeTypeEnum.UNKNOWN_ERROR,
            "055",
            "上线申请-校验上线申请"),;

    WisecoBootModulesEnum module;
    ErrorCodeTypeEnum type;
    String code;
    String message;

    @Override
    public String getModuleCode() {
        return this.module.getModuleCode();
    }

    @Override
    public ErrorCodeTypeEnum getType() {
        return this.type;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
