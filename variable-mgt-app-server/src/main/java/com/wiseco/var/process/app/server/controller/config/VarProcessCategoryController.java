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
package com.wiseco.var.process.app.server.controller.config;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.CategoryLabelOutPutVo;
import com.wiseco.var.process.app.server.controller.vo.input.CategoryMoveInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryCheckInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryUpdateInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VarProcessCategoryOutputDto;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.VarProcessCategoryBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 变量分类配置服务
 *
 * @author kangyk
 * @since 2022/08/30
 */

@RestController
@RequestMapping("/varProcessCategory")
@Slf4j
@Tag(name = "变量分类配置服务")
@Validated
@LoggableClass(param = "varProcessCategory")
public class VarProcessCategoryController {

    @Autowired
    private VarProcessCategoryBiz varProcessCategoryBiz;

    /**
     * 服务分类树的查询
     *
     * @return 服务分类树
     */
    @GetMapping("/getServiceCategoryTree")
    @Operation(summary = "服务分类树查询")
    public APIResult<List<VarProcessCategoryBiz.TreeNode>> getServiceCategoryTree() {
        // 1.获取业务逻辑层的结果
        List<VarProcessCategoryBiz.TreeNode> result = varProcessCategoryBiz.getServiceCategoryTree();

        // 2.返回结果为前端
        return APIResult.success(result);
    }

    /**
     * 变量清单分类树查询
     *
     * @return 变量清单分类树
     */
    @GetMapping("/getManifestCategoryTree")
    @Operation(summary = "变量清单分类树查询")
    public APIResult<List<VarProcessCategoryBiz.TreeNode>> getManifestTree() {
        // 1.获取业务逻辑层的结果
        List<VarProcessCategoryBiz.TreeNode> result = varProcessCategoryBiz.getManifestTree();

        // 2.返回结果为前端
        return APIResult.success(result);
    }

    /**
     * 启用的分类树查询
     * @param categoryType 分类对象
     * @param varProcessSpaceId 变量空间Id
     * @return 启用的分类树
     */
    @GetMapping("/getCategoryTree")
    @Operation(summary = "启用的分类树查询")
    public APIResult<List<VarProcessCategoryBiz.TreeNode>> getCategoryTree(@Parameter(description = "分类对象") @RequestParam CategoryTypeEnum categoryType,
                                                                           @Parameter(description = "变量空间id") @RequestParam("varProcessSpaceId") Long varProcessSpaceId) {
        return APIResult.success(varProcessCategoryBiz.getCategoryTree(categoryType, varProcessSpaceId, true));
    }

    /**
     * 分类树查询(添加分类时用，包含未启用)
     * @param categoryType 分类对象
     * @param varProcessSpaceId 变量空间id
     * @return 分类树
     */
    @GetMapping("/getAllCategoryTree")
    @Operation(summary = "分类树查询(添加分类时用，包含未启用)")
    public APIResult<List<VarProcessCategoryBiz.TreeNode>> getAllCategoryTree(@Parameter(description = "分类对象") @RequestParam CategoryTypeEnum categoryType,
                                                                              @Parameter(description = "变量空间id") @RequestParam("varProcessSpaceId") Long varProcessSpaceId) {
        return APIResult.success(varProcessCategoryBiz.getCategoryTree(categoryType, varProcessSpaceId, null));
    }

    /**
     * 获取所有业务分类枚举（左边栏使用
     * @return 所有业务分类枚举
     */
    @GetMapping("/getCategoryLabel")
    @Operation(summary = "获取所有业务分类枚举（左边栏使用")
    public APIResult<List<CategoryLabelOutPutVo>> getCategoryLabel() {
        return APIResult.success(varProcessCategoryBiz.getCategoryLabel());
    }

    /**
     * 分类树详情列表
     * @param inputDto 输入实体类对象
     * @return 分类树详情列表
     */
    @PostMapping("/list")
    @Operation(summary = "分类树详情列表")
    public APIResult<List<VarProcessCategoryOutputDto>> getCategoryList(@RequestBody VarProcessCategoryQueryInputDto inputDto) {
        return APIResult.success(varProcessCategoryBiz.getCategoryList(inputDto));
    }

    /**
     * 保存分类配置
     * @param inputDto 输入实体类对象
     * @return 保存或修改分类配置后的结果
     */
    @PostMapping("/saveCategory")
    @Operation(summary = "保存分类配置")
    @LoggableDynamicValue(params = {"var_process_category","categoryName","categoryType"})
    @LoggableMethod(value = "在[%s]下添加分类[%s]",params = {"categoryType","categoryName"}, type = LoggableMethodTypeEnum.CREATE_CAT)
    public APIResult<Long> saveCategory(@RequestBody VarProcessCategoryInputDto inputDto) {
        return APIResult.success(varProcessCategoryBiz.saveOrUpdateCategory(inputDto));
    }

    /**
     * 修改分类配置
     * @param inputDto 输入实体类对象
     * @return 保存或修改分类配置后的结果
     */
    @PostMapping("/editCategory")
    @Operation(summary = "修改分类配置")
    @LoggableDynamicValue(params = {"var_process_category","categoryName","categoryType"})
    @LoggableMethod(value = "在[%s]下编辑分类[%s]",params = {"categoryType","categoryName"}, type = LoggableMethodTypeEnum.EDIT_CAT)
    public APIResult<Long> editCategory(@RequestBody VarProcessCategoryInputDto inputDto) {
        return APIResult.success(varProcessCategoryBiz.saveOrUpdateCategory(inputDto));
    }

    /**
     * 上移/下移分类
     * @param categoryMoveInputVo 入参
     * @return true or false
     */
    @PostMapping("/move")
    @Operation(summary = "上移/下移分类")
    public APIResult<Boolean> moveCategory(@RequestBody @Validated CategoryMoveInputVo categoryMoveInputVo) {
        return APIResult.success(varProcessCategoryBiz.moveCategory(categoryMoveInputVo.getCategoryId(), categoryMoveInputVo.getOpeType()));
    }

    /**
     * 删除分类配置
     * @param inputDto 输入实体类对象
     * @return 删除分类配置后的结果
     */
    @PostMapping("/deleteCategory")
    @Operation(summary = "删除分类配置")
    @LoggableDynamicValue(params = {"var_process_category","categoryId"})
    @LoggableMethod(value = "在[%s]下删除分类[%s]",params = {"categoryId"}, type = LoggableMethodTypeEnum.DELETE_CAT)
    public APIResult<Boolean> deleteCategory(@RequestBody VarProcessCategoryUpdateInputDto inputDto) {
        return APIResult.success(varProcessCategoryBiz.deleteCategory(inputDto));
    }

    /**
     * 检查是否可以删除或者修改分类配置
     * @param inputDto 输入实体类对象
     * @return 是否可以删除或者修改分类配置的结果
     */
    @PostMapping("/checkDeleteOrUpdateCategory")
    @Operation(summary = "检查是否可以删除或者修改分类配置")
    public APIResult<String> checkDeleteOrUpdateCategory(@RequestBody VarProcessCategoryCheckInputDto inputDto) {
        return APIResult.success(varProcessCategoryBiz.checkDeleteCategory(inputDto));
    }

    /**
     * 获取分类及分类下启用的变量清单
     * @param varProcessSpaceId 变量空间Id
     * @param excludedList 已经用过的变量清单Id
     * @return 分类及分类下启用的变量清单
     */
    @GetMapping("/getCategoryTreeWithManifest")
    @Operation(summary = "获取分类及分类下启用的变量清单")
    public APIResult<List<VarProcessCategoryBiz.TreeNode>> getCategoryTreeWithManifest(@Parameter(description = "空间id") @RequestParam("varProcessSpaceId") Long varProcessSpaceId,
                                                                                       @Parameter(description = "已经选择的对象") @RequestParam("excludedList") List<Long> excludedList) {
        return APIResult.success(varProcessCategoryBiz.getCategoryTreeWithManifest(varProcessSpaceId, excludedList));
    }
}
