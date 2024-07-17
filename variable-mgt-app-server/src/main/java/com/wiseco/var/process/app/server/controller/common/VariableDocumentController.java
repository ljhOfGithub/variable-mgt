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
package com.wiseco.var.process.app.server.controller.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessDocumentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessDocumentQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDocumentOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDocumentUploadOutputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDocument;
import com.wiseco.var.process.app.server.service.VarProcessDocumentBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 变量空间文档
 * </p>
 *
 * @author wangxianli
 * @since 2022-03-13
 */
@RestController
@RequestMapping("/varProcessDocument")
@Slf4j
@Tag(name = "变量空间文档")
public class VariableDocumentController {

    @Autowired
    private VarProcessDocumentBiz varProcessDocumentBiz;

    /**
     * 分页查询列表
     *
     * @param inputDto 输入实体类对象
     * @return 分页查询列表
     */
    @GetMapping("/listPage")
    @Operation(summary = "分页查询列表")
    public APIResult<IPage<VarProcessDocument>> getListPage(VarProcessDocumentQueryInputDto inputDto) {
        return APIResult.success(varProcessDocumentBiz.getListPage(inputDto));
    }

    /**
     * 列表
     *
     * @param inputDto 输入实体类对象
     * @return 文档对象list
     */
    @GetMapping("/list")
    @Operation(summary = "列表")
    public APIResult<List<VarProcessDocument>> getList(VarProcessDocumentQueryInputDto inputDto) {
        return APIResult.success(varProcessDocumentBiz.getList(inputDto));
    }

    /**
     * 详情
     *
     * @param id id
     * @return 详情
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "详情")
    public APIResult<VarProcessDocument> getDetail(@Parameter(description = "id") @PathVariable("id") Long id) {
        return APIResult.success(varProcessDocumentBiz.getDetail(id));
    }

    /**
     * 添加
     *
     * @param inputDto 输入实体类对象
     * @return 添加后的结果
     */
    @PostMapping("/add")
    @Operation(summary = "添加")
    public APIResult<Long> create(@RequestBody VarProcessDocumentInputDto inputDto) {
        return APIResult.success(varProcessDocumentBiz.create(inputDto));
    }

    /**
     * 修改
     *
     * @param inputDto 输入实体类对象
     * @return 修改后的结果
     */
    @PostMapping("/update")
    @Operation(summary = "修改")
    public APIResult<Long> update(@RequestBody VarProcessDocumentInputDto inputDto) {
        return APIResult.success(varProcessDocumentBiz.update(inputDto));
    }

    /**
     * 删除
     *
     * @param id id
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/delete/{id}")
    @Operation(summary = "删除")
    public APIResult delete(@PathVariable("id") Long id) {
        varProcessDocumentBiz.delete(id);
        return APIResult.success();
    }

    /**
     * 下载
     *
     * @param id id
     * @param response response
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/downloadFile")
    @Operation(summary = "下载")
    public APIResult downloadFile(@Parameter(description = "id", required = true, example = "70100000") @RequestParam("id") Long id,
                                  HttpServletResponse response) {
        varProcessDocumentBiz.downloadFile(id, response);
        return APIResult.success();
    }

    /**
     * 文件预览
     *
     * @param id id
     * @return 文件预览后的结果
     */
    @GetMapping("/preview/{id}")
    @Operation(summary = "文件预览")
    public APIResult<VariableDocumentOutputDto> preview(@Parameter(description = "id", required = true, example = "70100000") @PathVariable("id") Long id) {
        return APIResult.success(varProcessDocumentBiz.preview(id));
    }

    /**
     * 上传文件
     *
     * @param spaceId  空间id
     * @param resourceId 组件id
     * @param file 文件
     * @return 上传文件后的结果
     */
    @PostMapping("/uploadFile")
    @Operation(summary = "上传文件")
    public APIResult<VariableDocumentUploadOutputDto> uploadFile(@Parameter(description = "空间id") @Valid @NotNull @RequestParam("spaceId") Long spaceId,
                                                                 @Parameter(description = "组件id") @RequestParam("resourceId") Long resourceId,
                                                                 @Parameter(description = "文件") @RequestParam("file") MultipartFile file) {
        return APIResult.success(varProcessDocumentBiz.uploadFile(spaceId, resourceId, file));
    }
}
