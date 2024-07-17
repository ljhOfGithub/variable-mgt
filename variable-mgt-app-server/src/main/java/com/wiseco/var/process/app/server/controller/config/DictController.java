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
import com.wiseco.var.process.app.server.controller.vo.input.DictInsertInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.DictItemQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.DictListInputDto;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.VarProcessDictBiz;
import com.wiseco.var.process.app.server.service.dto.DictTreeDto;
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
 * <p>
 * 领域模型字典类型
 * </p>
 *
 * @author xiewu
 * @since 2022-02-16
 */
@RestController
@RequestMapping("/varProcessDict")
@Slf4j
@Tag(name = "字典类别")
@LoggableClass(param = "varProcessDict")
public class DictController {

    @Autowired
    private VarProcessDictBiz varProcessDictBiz;

    /**
     * 获取字典项的树，给变量模板在生成变量的时候调用
     * @param dictCode 字典的code
     * @return 字典项的树形结构
     */
    @GetMapping("/getDictTree")
    @Operation(summary = "获取字典项的树，给变量模板在生成变量的时候调用")
    public APIResult<List<VarProcessDictBiz.DictTreeNode>> getDictTree(@RequestParam("dictCode") @Parameter(description = "字典的code") String dictCode) {
        // 1.调用业务逻辑层的接口
        List<VarProcessDictBiz.DictTreeNode> result = varProcessDictBiz.getDictTree(dictCode);
        // 2.返回结果
        return APIResult.success(result);
    }

    /**
     * 新增字典类别
     *
     * @param inputDto 输入实体类对象
     * @return 新增字典类别的结果
     */
    @PostMapping("/insertDict")
    @Operation(summary = "新增字典类别")
    @LoggableMethod(value = "添加字典类型[%s]",params = "name", type = LoggableMethodTypeEnum.CREATE_DICT)
    public APIResult insertDict(@RequestBody DictInsertInputDto inputDto) {
        varProcessDictBiz.insertDict(inputDto);
        return APIResult.success();
    }

    /**
     * 编辑字典类别
     *
     * @param inputDto 输入实体类对象
     * @return 编辑字典类别的结果
     */
    @PostMapping("/editDict")
    @Operation(summary = "编辑字典类别")
    @LoggableMethod(value = "编辑字典类型[%s]", params = "name", type = LoggableMethodTypeEnum.EDIT_DICT)
    public APIResult editDict(@RequestBody DictInsertInputDto inputDto) {
        varProcessDictBiz.insertDict(inputDto);
        return APIResult.success();
    }

    /**
     * 字典类别列表
     *
     * @param inputDto 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/findDictList")
    @Operation(summary = "字典类别列表")
    public APIResult<List<DictTreeDto>> findDictList(@Validated @RequestBody DictListInputDto inputDto) {
        List<DictTreeDto> list = varProcessDictBiz.findDictList(inputDto);
        return APIResult.success(list);
    }

    /**
     * 根据字典类型编码获取字典项详情
     *
     * @param inputDto 输入
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/getDictItemByCode")
    @Operation(summary = "根据字典类型编码获取字典项详情")
    public APIResult<List<DictTreeDto>> getDictItemByCode(@RequestBody DictItemQueryInputDto inputDto) {
        List<DictTreeDto> list = varProcessDictBiz.getDictItemByCode(inputDto);
        return APIResult.success(list);
    }

    /**
     * 字典类别删除
     *
     * @param id id
     * @param spaceId 变量空间Id
     * @return 字典类别删除的结果
     */
    @GetMapping("/deleteDictById")
    @Operation(summary = "字典类别删除")
    @LoggableDynamicValue(params = {"var_process_dict","id"})
    @LoggableMethod(value = "删除字典类型[%s]",params = {"id"},type = LoggableMethodTypeEnum.DELETE_DICT)
    public APIResult deleteDictById(@Parameter(description = "字典类别id") @RequestParam("id") Long id, @Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId) {
        varProcessDictBiz.deleteDictById(id);
        return APIResult.success();
    }

    /**
     * 字典类型删除确认和校验是否被数据模型使用
     *
     * @param spaceId 空间id
     * @param domainDictId 字典Id
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/verifyDictDeletion")
    @Operation(summary = "字典类型删除确认和校验是否被数据模型使用")
    public APIResult verifyDictDeletion(@Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId,
                                                 @Parameter(description = "字典类型ID") @RequestParam("domainDictId") Long domainDictId) {
        return APIResult.success(varProcessDictBiz.verifyDictDeletion(spaceId, domainDictId));
    }
}
