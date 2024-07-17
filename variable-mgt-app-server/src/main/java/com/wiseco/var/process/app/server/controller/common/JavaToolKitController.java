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

import com.wiseco.var.process.app.server.service.JavaToolKitBiz;
import com.wiseco.var.process.app.server.service.dto.common.AddMethodDetailDTO;
import com.wiseco.var.process.app.server.service.dto.common.AttributeDTO;
import com.wiseco.var.process.app.server.service.dto.common.ClassDTO;
import com.wiseco.var.process.app.server.service.dto.common.JarDTO;
import com.wiseco.var.process.app.server.service.dto.common.JavaToolkitIdentifierDTO;
import com.wiseco.var.process.app.server.service.dto.common.MethodDetailDTO;
import com.wiseco.var.process.app.server.service.dto.output.JarInfoDTO;
import com.wisecoprod.starterweb.pojo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;

/**
 * Java工具类前端控制器
 *
 * @author fudengkui
 */
@Slf4j
@RestController
@RequestMapping("/javaToolKit")
@Tag(name = "Java工具类")
public class JavaToolKitController {

    @Autowired
    private JavaToolKitBiz javaToolKitBiz;

    /**
     * toolkitList
     *
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @GetMapping("/list")
    @Operation(summary = "工具类列表")
    public ApiResult<List<JarDTO>> toolkitList() {
        return ApiResult.success(javaToolKitBiz.toolkitList());
    }

    /**
     * parseJar
     *
     * @param file 文件对象
     * @return 解析jar文件的结果
     */
    @PostMapping("/parseJar")
    @Operation(summary = "解析jar文件")
    public ApiResult<List<JarDTO>> parseJar(@RequestParam(value = "file") MultipartFile file) {
        return ApiResult.success(javaToolKitBiz.parseJar(file));
    }

    /**
     * classNameDuplicateCheck
     *
     * @param content  content
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @PostMapping("/className/duplicate/check")
    @Operation(summary = "类名称/显示名重复校验")
    public ApiResult<Void> classNameDuplicateCheck(@RequestParam(value = "content") String content) {
        javaToolKitBiz.classNameDuplicateCheck(content);
        return ApiResult.success();
    }

    /**
     * 保存jar文件
     * @param content 内容
     * @param file 文件对象
     * @return 保存jar文件的结果
     * @throws Exception 异常
     */
    @PostMapping("/saveJar")
    @Operation(summary = "保存jar文件")
    public ApiResult<Void> saveJar(@RequestParam(value = "content") String content, @RequestParam(value = "file") MultipartFile file)
            throws Exception {
        javaToolKitBiz.saveJar(content, file);
        return ApiResult.success();
    }

    /**
     * listJar
     *
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @GetMapping("/jar/list")
    @Operation(summary = "jar文件列表")
    public ApiResult<List<JarInfoDTO>> listJar() {
        return ApiResult.success(javaToolKitBiz.listJar());
    }

    /**
     * jarDetail
     *
     * @param jarIdentifier jarIdentifier
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @GetMapping("/jar/detail")
    @Operation(summary = "jar包详情")
    public ApiResult<List<JarDTO>> jarDetail(@RequestParam(value = "jarIdentifier") String jarIdentifier) {
        return ApiResult.success(javaToolKitBiz.jarDetail(jarIdentifier));
    }

    /**
     * addClass
     *
     * @param dtoList dtoList
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @PostMapping("/addClass")
    @Operation(summary = "通过jar包添加class")
    public ApiResult<Void> addClass(@RequestBody List<JarDTO> dtoList) {
        javaToolKitBiz.addClass(dtoList);
        return ApiResult.success();
    }

    /**
     * classDetail
     *
     * @param identifier identifier
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @GetMapping("/class/detail")
    @Operation(summary = "class详情")
    public ApiResult<ClassDTO> classDetail(@RequestParam(value = "identifier") String identifier) {
        return ApiResult.success(javaToolKitBiz.classDetail(identifier));
    }

    /**
     * updateClass
     *
     * @param classDTO classDTO
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @PostMapping("/class/update")
    @Operation(summary = "更新class")
    public ApiResult<Void> updateClass(@RequestBody ClassDTO classDTO) {
        javaToolKitBiz.updateClass(classDTO);
        return ApiResult.success();
    }

    /**
     * deleteClassCheck
     *
     * @param dto dto
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @PostMapping("/class/delete/check")
    @Operation(summary = "删除class校验")
    public ApiResult<Void> deleteClassCheck(@Validated @RequestBody JavaToolkitIdentifierDTO dto) {
        javaToolKitBiz.deleteClassCheck(dto);
        return ApiResult.success();
    }

    /**
     * deleteClass
     *
     * @param dto dto
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @PostMapping("/class/delete")
    @Operation(summary = "删除class")
    public ApiResult<Void> deleteClass(@RequestBody JavaToolkitIdentifierDTO dto) {
        javaToolKitBiz.deleteClass(dto);
        return ApiResult.success();
    }

    /**
     * addMethodDetail
     *
     * @param identifier identifier
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @GetMapping("/addMethod/detail")
    @Operation(summary = "添加方法详情")
    public ApiResult<AddMethodDetailDTO> addMethodDetail(@RequestParam(value = "identifier") String identifier) {
        return ApiResult.success(javaToolKitBiz.addMethodDetail(identifier));
    }

    /**
     * addMethodSave
     *
     * @param dto dto
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @PostMapping("/addMethod/save")
    @Operation(summary = "添加方法保存")
    public ApiResult<Void> addMethodSave(@RequestBody AddMethodDetailDTO dto) {
        javaToolKitBiz.addMethodSave(dto);
        return ApiResult.success();
    }

    /**
     * methodDetail
     *
     * @param identifier identifier
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @GetMapping("/method/detail")
    @Operation(summary = "method详情")
    public ApiResult<MethodDetailDTO> methodDetail(@RequestParam(value = "identifier") String identifier) {
        return ApiResult.success(javaToolKitBiz.methodDetail(identifier));
    }

    /**
     * updateMethod
     *
     * @param dto dto
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @PostMapping("/method/update")
    @Operation(summary = "更新method")
    public ApiResult<Void> updateMethod(@RequestBody MethodDetailDTO dto) {
        javaToolKitBiz.updateMethod(dto);
        return ApiResult.success();
    }

    /**
     * deleteMethodCheck
     *
     * @param dto dto
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @PostMapping("/method/delete/check")
    @Operation(summary = "删除method校验")
    public ApiResult<Void> deleteMethodCheck(@Validated @RequestBody JavaToolkitIdentifierDTO dto) {
        javaToolKitBiz.deleteMethodCheck(dto);
        return ApiResult.success();
    }

    /**
     * 删除method
     * @param dto 输入实体类对象
     * @return 删除method的结果
     */
    @PostMapping("/method/delete")
    @Operation(summary = "删除method")
    public ApiResult<Void> deleteMethod(@RequestBody JavaToolkitIdentifierDTO dto) {
        javaToolKitBiz.deleteMethod(dto);
        return ApiResult.success();
    }

    /**
     * attributeDetail
     *
     * @param identifier identifier
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @GetMapping("/attribute/detail")
    @Operation(summary = "attribute详情")
    public ApiResult<AttributeDTO> attributeDetail(@RequestParam(value = "identifier") String identifier) {
        return ApiResult.success(javaToolKitBiz.attributeDetail(identifier));
    }

    /**
     * updateAttribute
     *
     * @param dto dto
     * @return com.wisecoprod.starterweb.pojo.ApiResult
     */
    @PostMapping("/attribute/update")
    @Operation(summary = "更新attribute")
    public ApiResult<Void> updateAttribute(@RequestBody AttributeDTO dto) {
        javaToolKitBiz.updateAttribute(dto);
        return ApiResult.success();
    }
}
