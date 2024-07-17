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
package com.wiseco.var.process.app.server.controller.model;


import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelJsonImportInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelExcelImportOutputDto;
import com.wiseco.var.process.app.server.service.datamodel.DataModelExcelBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;


/**
 * 变量加工数据模型 控制器
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/13
 */
@RestController
@RequestMapping("/variableDataModel")
@Slf4j
@Tag(name = "数据模型excel")
public class DataModelExcelController {

    @Resource
    private DataModelExcelBiz dataModelExcelBiz;

    /**
     * 导入数据模型Excel
     *
     * @param file 文件对象
     * @param spaceId 变量空间Id
     * @param dataModelId 数据模型Id
     * @return 数据模型
     */
    @PostMapping("/importDataModelExcel")
    @Operation(summary = "导入数据模型Excel")
    public APIResult<VariableDataModelExcelImportOutputDto> importDataModelExcel(@RequestParam("file") MultipartFile file,
                                                                                 @RequestParam("spaceId") @NotNull(message = "变量空间 ID 不能为空。") @Parameter(description = "变量空间 ID") Long spaceId,
                                                                                 @RequestParam("dataModelId") @NotNull(message = "数据模型 ID 不能为空。") @Parameter(description = "数据模型ID") Long dataModelId) {
        return APIResult.success(dataModelExcelBiz.importDataModelExcel(spaceId, dataModelId, file));
    }

    /**
     * 导出数据模型Excel
     *
     * @param spaceId 变量空间Id
     * @param dataModelId 数据模型Id
     * @param response HttpServletResponse对象
     */
    @GetMapping("/exportDataModelExcel")
    @Operation(summary = "导出数据模型Excel")
    public void exportDataModelExcel(@RequestParam("spaceId") @NotNull(message = "变量空间 ID 不能为空。") @Parameter(description = "变量空间 ID") Long spaceId,
                                     @RequestParam("dataModelId") @NotNull(message = "数据模型 ID 不能为空。") @Parameter(description = "数据模型ID") Long dataModelId,
                                     HttpServletResponse response) {
        dataModelExcelBiz.exportDataModelExcel(spaceId, dataModelId, response);
    }

    /**
     * 导入数据模型Json
     *
     * @param inputVo 数据模型Json导入参数
     * @return 数据模型Json导入输出参数
     */
    @PostMapping("/importDataModelJson")
    @Operation(summary = "导入数据模型Json")
    public APIResult<VariableDataModelExcelImportOutputDto> importDataModelJson(@RequestBody @Validated VariableDataModelJsonImportInputVo inputVo) {
        return APIResult.success(dataModelExcelBiz.importDataModelJson(inputVo));
    }

}
