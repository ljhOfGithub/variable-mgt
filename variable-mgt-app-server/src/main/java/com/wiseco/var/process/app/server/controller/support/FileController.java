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
package com.wiseco.var.process.app.server.controller.support;

import com.wiseco.boot.commons.io.SftpClient;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.commons.enums.PathUtil;
import com.wiseco.var.process.app.server.controller.vo.FileUploadRespVO;
import com.wiseco.var.process.app.server.service.dto.FileFtpUploadDTO;
import com.wiseco.var.process.app.server.service.dto.FileUploadDTO;
import com.wiseco.var.process.app.server.service.impl.SftpClientService;
import com.wiseco.var.process.app.server.service.common.OssFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author xupei
 */
@RestController
@RequestMapping("/file")
@Slf4j
@Tag(name = "文件共用接口")
public class FileController {


    @Autowired
    private OssFileService ossFileService;

    @Autowired
    private SftpClientService sftpClientService;


    /**
     * 获取列表
     * @param file 文件对象
     * @param fileName 文件名
     * @param uploadId oss唯一标识：分片上传时的同一文件标识
     * @param chunkTotal 文件总的分片数量
     * @param chunkCurrent 当前分片，从1开始
     * @param md5   文件md5值
     * @param type 文件类型
     * @return 返回给前端的实体
     */
    @PostMapping("/oss")
    @Operation(summary = "oss存储")
    public APIResult<FileUploadRespVO> upload(@RequestParam("file") MultipartFile file,

                                              @Parameter(description = "新增：原文件名称")
                                              @RequestParam("fileName") String fileName,

                                              @Parameter(description = "oss唯一标识：分片上传时的同一文件标识")
                                              @RequestParam("uploadId") String uploadId,

                                              @Parameter(description = "文件总的分片数量")
                                              @RequestParam("chunkTotal") Integer chunkTotal,

                                              @Parameter(description = "当前分片，从1开始")
                                              @RequestParam("chunkCurrent") Integer chunkCurrent,

                                              @Parameter(description =  "文件md5值")
                                              @RequestParam("md5") String md5,

                                              @Parameter(description = "文件大小")
                                              @RequestParam("type") String type


    ) {
        FileUploadDTO inputDto = FileUploadDTO.builder()
                .fileName(fileName)
                .uploadId(uploadId)
                .chunkCurrent(chunkCurrent)
                .chunkTotal(chunkTotal)
                .md5(md5)
                .type(type)
                .build();

        return APIResult.success(ossFileService.upload(file, inputDto));
    }

    /**
     *
     * @param ftpServerId   文件服务器Id
     * @param filePath      文件服务器目录
     * @param fileName      文件名
     * @param ossUploadPath   oss存储目录
     * @param isPreview     是否预览
     * @return  返回给前端的实体
     */
    @PostMapping("/ossftp")
    @Operation(summary = "ftp文件上传oss存储")
    public APIResult<FileUploadRespVO> uploadRemote(
                                                @Parameter(description = "文件服务器Id")
                                                @RequestParam("ftpServerId") long ftpServerId,

                                                @Parameter(description = "文件目录")
                                                @RequestParam("filePath") String filePath,

                                                @Parameter(description = "文件名称")
                                                @RequestParam("fileName") String fileName,

                                                @Parameter(description = "oss目录")
                                                @RequestParam("ossUploadPath") String ossUploadPath,

                                                @Parameter(description = "是否预览")
                                                @RequestParam("isPreview") Boolean isPreview) {
        SftpClient sftpClient = null;
        try {
            sftpClient = sftpClientService.login(ftpServerId);
            InputStream inputStream = sftpClientService.downloadStream(filePath, fileName, sftpClient);
            long fileSize = sftpClientService.getFileAttrs(PathUtil.formDirPath(filePath) + fileName, sftpClient).getSize();
            FileFtpUploadDTO inputDto = FileFtpUploadDTO.builder()
                    .ftpServerId(ftpServerId)
                    .filePath(filePath)
                    .fileName(fileName)
                    .fileSize(fileSize)
                    .ossUploadPath(ossUploadPath)
                    .build();
            if (isPreview) {
                return APIResult.success(ossFileService.uploadFtpFileChunkToOss(inputStream, inputDto));
            } else {
                return APIResult.success(ossFileService.uploadFtpFileToOss(inputStream, inputDto));
            }
        } finally {
            if (sftpClient != null) {
                sftpClientService.logout(sftpClient);
            }

        }
    }

    /**
     *  判断oss文件是否存在
     * @param path
     * @return 是否存在
     */
    @GetMapping("/judgingOssFiles")
    @Operation(summary = "判断oss文件是否存在")
    public APIResult<Boolean> upload(@RequestParam("path") String path) {

        return APIResult.success(ossFileService.ossIsExist(path));
    }

}
