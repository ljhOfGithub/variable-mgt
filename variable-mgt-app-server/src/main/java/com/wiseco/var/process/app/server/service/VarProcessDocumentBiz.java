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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.net.HttpHeaders;
import com.wiseco.boot.commons.io.FileUtil;
import com.wiseco.boot.io.OSSClient;
import com.wiseco.boot.io.oss.OSSResponse;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.common.utils.IdentityGenerator;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessDocumentInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessDocumentQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDocumentOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDocumentUploadOutputDto;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDocument;
import com.wiseco.var.process.app.server.service.common.FilePreviewManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wiseco.var.process.app.server.commons.util.StringPool.DOT;
import static com.wiseco.var.process.app.server.commons.util.StringPool.EMPTY;
import static com.wiseco.var.process.app.server.commons.util.StringPool.SLASH;

/**
 * @author wangxianli
 */
@Slf4j
@Service
public class VarProcessDocumentBiz {

    /**
     * 策略文档文件在 OSS 的存储目录
     */
    public static final String DOCUMENT_DIR = "/variable";
    public static final String OSS_DOWNLOAN_FAIL_MESSAGE = "oss下载文件失败";
    @Autowired
    private VarProcessDocumentService varProcessDocumentService;
    @Autowired
    private FilePreviewManager        filePreviewManager;

    /**
     *  @Autowired
      */
    private OSSClient                 ossClient;


    /**
     * 分页查询文档
     * @param inputDto 输入实体类对象
     * @return 分页查询文档的结果
     */
    public IPage<VarProcessDocument> getListPage(VarProcessDocumentQueryInputDto inputDto) {
        Page<VarProcessDocument> page = new Page<>(inputDto.getCurrentNo(), inputDto.getSize());
        QueryWrapper<VarProcessDocument> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", DeleteFlagEnum.USABLE.getCode());

        if (inputDto.getSpaceId() != null) {
            queryWrapper.eq("var_process_space_id", inputDto.getSpaceId());
        }
        if (inputDto.getResourceId() != null) {
            queryWrapper.eq("resource_id", inputDto.getResourceId());
        }
        if (!StringUtils.isEmpty(inputDto.getFileType())) {
            queryWrapper.eq("file_type", inputDto.getFileType());
        }
        queryWrapper.orderByDesc("updated_time");
        queryWrapper.orderByDesc("id");
        Page<VarProcessDocument> pageList = varProcessDocumentService.page(page, queryWrapper);
        Map<String, String> userMap = new HashMap<>(MagicNumbers.EIGHT);

        if (!CollectionUtils.isEmpty(pageList.getRecords())) {
            for (VarProcessDocument varProcessDocument : pageList.getRecords()) {
                varProcessDocument.setCreatedUser(userMap.getOrDefault(varProcessDocument.getCreatedUser(), varProcessDocument.getCreatedUser()));
            }

        }
        return pageList;

    }

    /**
     * 获取文档list
     * @param inputDto 输入实体类对象
     * @return 文档list
     */
    public List<VarProcessDocument> getList(VarProcessDocumentQueryInputDto inputDto) {
        QueryWrapper<VarProcessDocument> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", DeleteFlagEnum.USABLE.getCode());
        if (inputDto.getSpaceId() != null) {
            queryWrapper.eq("var_process_space_id", inputDto.getSpaceId());
        }
        if (inputDto.getResourceId() != null) {
            queryWrapper.eq("resource_id", inputDto.getResourceId());
        }
        if (!StringUtils.isEmpty(inputDto.getFileType())) {
            queryWrapper.eq("file_type", inputDto.getFileType());
        }
        queryWrapper.orderByDesc("updated_time");
        queryWrapper.orderByDesc("id");
        List<VarProcessDocument> list = varProcessDocumentService.list(queryWrapper);
        Map<String, String> userMap = new HashMap<>(MagicNumbers.EIGHT);

        if (!CollectionUtils.isEmpty(list)) {
            for (VarProcessDocument varProcessDocument : list) {
                varProcessDocument.setCreatedUser(userMap.getOrDefault(varProcessDocument.getCreatedUser(), varProcessDocument.getCreatedUser()));
            }

        }
        return list;

    }

    /**
     * 详情查看
     * @param id 文档Id
     * @return 文档对象
     */
    public VarProcessDocument getDetail(Long id) {
        return varProcessDocumentService.getById(id);
    }

    /**
     * 新增操作
     * @param inputDto 输入实体类对象
     * @return id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long create(VarProcessDocumentInputDto inputDto) {
        VarProcessDocument varProcessDocument = new VarProcessDocument();
        varProcessDocument.setVarProcessSpaceId(inputDto.getSpaceId());
        varProcessDocument.setResourceId(inputDto.getResourceId());
        varProcessDocument.setFileType(inputDto.getFileType());
        varProcessDocument.setDescription(inputDto.getDescription());
        varProcessDocument.setCreatedUser(SessionContext.getSessionUser().getUsername());

        varProcessDocument.setName(inputDto.getName());
        varProcessDocument.setPreViewName(inputDto.getPreViewName());
        varProcessDocument.setFileSize(inputDto.getFileSize());
        varProcessDocument.setFilePath(inputDto.getFilePath());
        varProcessDocument.setSuffix(inputDto.getSuffix());

        varProcessDocumentService.save(varProcessDocument);

        return varProcessDocument.getId();
    }

    /**
     * 更新操作
     * @param inputDto 输入实体类对象
     * @return id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long update(VarProcessDocumentInputDto inputDto) {
        VarProcessDocument varProcessDocument = new VarProcessDocument();
        varProcessDocument.setId(inputDto.getId());
        varProcessDocument.setFileType(inputDto.getFileType());
        varProcessDocument.setDescription(inputDto.getDescription());

        varProcessDocument.setName(inputDto.getName());
        varProcessDocument.setPreViewName(inputDto.getPreViewName());
        varProcessDocument.setFileSize(inputDto.getFileSize());
        varProcessDocument.setFilePath(inputDto.getFilePath());
        varProcessDocument.setSuffix(inputDto.getSuffix());

        varProcessDocument.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        varProcessDocumentService.updateById(varProcessDocument);

        return varProcessDocument.getId();
    }

    /**
     * 逻辑删除操作
     * @param id 文档Id
     */
    public void delete(Long id) {
        varProcessDocumentService.update(
                new UpdateWrapper<VarProcessDocument>().lambda()
                        .set(VarProcessDocument::getDeleteFlag, DeleteFlagEnum.DELETED.getCode())
                        .eq(VarProcessDocument::getId, id)
        );
    }

    /**
     * 下载文件
     * @param id 文档Id
     * @param response HttpServletResponse对象
     */
    public void downloadFile(Long id, HttpServletResponse response) {
        VarProcessDocument document = varProcessDocumentService.getById(id);

        //OSS下载文件
        String remotePath = document.getFilePath();
        OSSResponse responseOss = null;
        try {
            responseOss = ossClient.getObject(remotePath);
        } catch (Exception e) {
            log.error("oss下载文件失败：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, OSS_DOWNLOAN_FAIL_MESSAGE);
        }

        if (responseOss == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, OSS_DOWNLOAN_FAIL_MESSAGE);
        }

        try (InputStream inputStream = responseOss.getInputStream(); OutputStream outputStream = response.getOutputStream()) {

            IOUtils.copy(inputStream, outputStream);
            // 设定 HTTP 响应头部
            String fileName = document.getName() + "." + document.getSuffix();
            response.setContentType("application/octet-stream");
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
            response.addHeader(HttpHeaders.PRAGMA, "no-cache");
            response.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");

            response.flushBuffer();
        } catch (IOException e) {
            log.error("下载文件失败：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "下载文件失败");
        }
    }

    /**
     * 预览文档
     * @param id 文档Id
     * @return 策略文档表 出参Dto
     */
    public VariableDocumentOutputDto preview(Long id) {
        VarProcessDocument document = varProcessDocumentService.getById(id);
        if (document == null) {
            return null;
        }

        //OSS下载文件
        String remotePath = document.getFilePath();
        OSSResponse responseOss = null;
        try {
            responseOss = ossClient.getObject(remotePath);
        } catch (Exception e) {
            log.error("oss下载文件失败：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, OSS_DOWNLOAN_FAIL_MESSAGE);
        }

        if (responseOss == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, OSS_DOWNLOAN_FAIL_MESSAGE);
        }

        Map<String, String> userMap = new HashMap<>(MagicNumbers.EIGHT);

        String previewUrl = EMPTY;
        try (InputStream inputStream = responseOss.getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            previewUrl = filePreviewManager.getFilePreviewUrl(bytes, document.getPreViewName() + DOT + document.getSuffix());
        } catch (Exception e) {
            log.error("文件预览失败：", e);
        }

        return VariableDocumentOutputDto.builder().previewUrl(previewUrl).spaceId(document.getVarProcessSpaceId())
                .resourceId(document.getResourceId()).name(document.getName()).size(document.getFileSize()).fileType(document.getFileType())
                .createdUser(userMap.getOrDefault(document.getCreatedUser(), document.getCreatedUser())).createdTime(document.getUpdatedTime())
                .description(document.getDescription()).build();
    }

    /**
     * 上传文件
     * @param spaceId 变量空间Id
     * @param resourceId 资源Id
     * @param file 文件对象
     * @return 变量空间文档上传输出 DTO
     */
    public VariableDocumentUploadOutputDto uploadFile(Long spaceId, Long resourceId, MultipartFile file) {
        //路径规则：/strategy/策略ID/组件ID/文件名
        String fileName = FileUtil.getTargetFileName(file.getOriginalFilename());
        String filePath = DOCUMENT_DIR + SLASH + spaceId + SLASH + resourceId + SLASH + fileName;

        // 2. 上传 文档至 文件到 OSS
        try {
            ossClient.putObject(filePath, file.getBytes());
        } catch (Exception e) {
            log.error("文件保存到 OSS 失败: {}", e.getMessage());
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "文件保存失败");
        }

        return VariableDocumentUploadOutputDto.builder().fileName(fileName).filePath(filePath).fileSize(FileUtil.getSize(file.getSize()))
            .fileSuffix(FileUtil.getExtensionName(file.getOriginalFilename()))
            .preViewName(String.valueOf(IdentityGenerator.nextId()) + System.currentTimeMillis()).build();
    }
}
