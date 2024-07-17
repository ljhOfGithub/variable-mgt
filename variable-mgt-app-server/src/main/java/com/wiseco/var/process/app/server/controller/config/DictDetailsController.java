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
import com.wiseco.var.process.app.server.controller.vo.input.DictDetailsInsertInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.DictDetailsParentListInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.DictDetailImportOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.DictDetailsParentListOutputDto;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.VarProcessDictDetailsBiz;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 领域模型字典详情
 * </p>
 *
 * @author liaody
 * @since 2021-12-06
 */
@RestController
@RequestMapping("/varProcessDictDetails")
@Slf4j
@Tag(name = "字典项")
@LoggableClass(param = "varProcessDictDetails")
public class DictDetailsController {

    @Autowired
    private VarProcessDictDetailsBiz varProcessDictDetailsBiz;

    /**
     * 字典项新增
     *
     * @param inputDto 入参
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/insertDictDetails")
    @Operation(summary = "字典项新增")
    @LoggableDynamicValue(params = {"var_process_dict","dictId"})
    @LoggableMethod(value = "在字典类型[%s]下添加字典项[%s]",params = {"dictId","name"},type = LoggableMethodTypeEnum.CREATE_DICT_DETAIL)
    public APIResult insertDictDetails(@Validated @RequestBody DictDetailsInsertInputDto inputDto) {
        varProcessDictDetailsBiz.insertDictDetails(inputDto);
        return APIResult.success();
    }

    /**
     * 字典项修改
     *
     * @param inputDto 入参
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/editDictDetails")
    @Operation(summary = "字典项修改")
    @LoggableDynamicValue(params = {"var_process_dict","dictId"})
    @LoggableMethod(value = "在字典类型[%s]下编辑字典项[%s]",params = {"dictId","name"},type = LoggableMethodTypeEnum.EDIT_DICT_DETAIL)
    public APIResult editDictDetails(@Validated @RequestBody DictDetailsInsertInputDto inputDto) {
        varProcessDictDetailsBiz.insertDictDetails(inputDto);
        return APIResult.success();
    }

    /**
     * 获取字典项的上级字典
     *
     * @param inputDto 入参
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/findDictDetailsByParent")
    @Operation(summary = "获取字典项的上级字典")
    public APIResult<List<DictDetailsParentListOutputDto>> findDictDetailsByParent(@Validated @RequestBody DictDetailsParentListInputDto inputDto) {
        List<DictDetailsParentListOutputDto> list = varProcessDictDetailsBiz.findDictDetailsByParent(inputDto);
        return APIResult.success(list);
    }

    /**
     * 字典项删除
     *
     * @param id id
     * @param spaceId 变量空间id
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/deleteDictDetailsById")
    @Operation(summary = "字典项删除")
    @LoggableDynamicValue(params = {"var_process_dict","var_process_dict_details","id"})
    @LoggableMethod(value = "在字典类型[%s]下删除字典项[%s]",params = {"id"}, type = LoggableMethodTypeEnum.DELETE_DICT_DETAIL)
    public APIResult deleteDictDetailsById(@Parameter(description = "字典项id") @RequestParam("id") Long id, @Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId) {
        varProcessDictDetailsBiz.deleteDictDetailsById(id);
        return APIResult.success();
    }

    /**
     * 导入字典项
     *
     * @param file file
     * @param spaceId 变量空间id
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @PostMapping("/importDictDetailsExcel")
    @Operation(summary = "导入字典项")
    public APIResult<DictDetailImportOutputDto> importDictDetailsExcel(@RequestParam("file") MultipartFile file,
                                                                       @Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId) {
        DictDetailImportOutputDto outputDto = varProcessDictDetailsBiz.importDictDetailsExcel(file, spaceId);
        return APIResult.success(outputDto);
    }

    /**
     * 导出字典项模板
     *
     * @param spaceId 变量空间id
     * @param response 响应
     */
    @GetMapping("/exportDictDetailsTemplate")
    @Operation(summary = "导出字典项模板")
    public void exportDictDetailsTemplate(@Parameter(description = "变量空间id") @RequestParam("spaceId") Long spaceId, HttpServletResponse response) {
        varProcessDictDetailsBiz.exportDictDetailsTemplate(response);
    }
}
