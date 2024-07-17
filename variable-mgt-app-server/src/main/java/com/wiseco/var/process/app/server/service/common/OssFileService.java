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
package com.wiseco.var.process.app.server.service.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.wiseco.boot.cache.CacheClient;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.osg.sdk.AwsConfig;
import com.wiseco.decision.osg.sdk.OsgUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.PathUtil;
import com.wiseco.var.process.app.server.controller.vo.FileUploadRespVO;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.SysOssMapper;
import com.wiseco.var.process.app.server.repository.entity.SysOss;
import com.wiseco.var.process.app.server.service.SysOssService;
import com.wiseco.var.process.app.server.service.dto.FileFtpUploadDTO;
import com.wiseco.var.process.app.server.service.dto.FileUploadDTO;
import com.wisecotech.json.JSON;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.wiseco.decision.osg.sdk.OsgUtil.getClient;
import static com.wiseco.var.process.app.server.commons.enums.PathUtil.SLASH;
import static com.wiseco.var.process.app.server.commons.util.StringPool.DOT;


/**
 * @author Asker.J
 * @since 2022/11/16
 */
@Service
@Slf4j
@RefreshScope
public class OssFileService {

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private SysOssMapper sysOssMapper;

    @Autowired
    private SysOssService sysOssService;

    @Autowired
    private OssFileService ossFileService;


    @Value("${wiseco.boot.oss.endpoint}")
    private String ossServer;

    @Value("${wiseco.boot.oss.bucket}")
    private String ossBucket;

    @Value("${wiseco.boot.oss.secretKey}")
    private String ossSecretKey;

    @Value("${wiseco.boot.oss.appKey}")
    private String ossAccessKey;

    /**
     * 接收文件上传oss
     *
     * @param file 文件对象
     * @param dto  前端发送的输入实体
     * @return key，value对
     */
    public Pair<String, String> receive(MultipartFile file, FileUploadDTO dto) {
        try {
            final byte[] data = file.getBytes();
            String uploadId = dto.getUploadId();
            String ossFileName = null;
            if (dto.getChunkCurrent() == 1) {
                ossFileName = generateFileName(dto.getFileName());
            } else {
                org.springframework.util.Assert.notNull(uploadId, "数据传输异常中断");
                ossFileName = (String) cacheClient.get(uploadId);
                org.springframework.util.Assert.notNull(ossFileName, "数据传输异常中断");
            }
            //上传oss
            if (dto.getChunkTotal() == 1) {
                uploadOssFile(null, data, "", ossFileName);
                dto.setOssFilePath(ossFileName);
            } else {
                uploadId = multiUploadOssFile((Integer) null, "", ossFileName,
                        dto.getUploadId(), dto.getChunkCurrent(),
                        dto.getChunkTotal(), data);
                cacheClient.put(uploadId, ossFileName);
                dto.setUploadId(uploadId);
            }
            return Pair.of(uploadId, ossFileName);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "文件上传异常");
        }
    }

    /**
     * @param dynamicDir  动态目录地址
     * @param ossFileName oss文件名
     * @param data        数据
     * @param totalSize   总片数
     * @param partsCount  当前片数
     * @return uploadId
     * @throws URISyntaxException 异常
     */
    public String multipartUpload(String dynamicDir, String ossFileName, byte[] data, int totalSize, int partsCount) throws URISyntaxException {
        String fileId = PathUtil.formDirPath(dynamicDir) + ossFileName;

        //设置oss配置
        setOssConfig(ossServer,ossBucket,ossAccessKey,ossSecretKey);

        log.info("multipartUpload,data size:{}, totalCount:{}, partsCount:{}", data.length, totalSize, partsCount);
        String uploadId = (String) cacheClient.get(fileId + "_uploadId");
        final String eTagsStr = (String) cacheClient.get(fileId + "_eTags");
        List<String> eTags = JSON.parseArray(eTagsStr, String.class);
        if (Objects.isNull(uploadId)) {
            uploadId = OsgUtil.createMultipartUpload(fileId);
            eTags = new ArrayList<>();
            cacheClient.put(fileId + "_uploadId", uploadId);
        }
        if (Objects.nonNull(eTags)) {
            eTags.add(OsgUtil.uploadPart(fileId, partsCount, uploadId, data));
            cacheClient.put(fileId + "_eTags", JSON.toJSONString(eTags));
            if (partsCount == totalSize) {
                OsgUtil.completeMultipartUpload(fileId, uploadId, eTags);
            }
        }
        return uploadId;
    }

    /**
     * 接收文件上传oss
     *
     * @param file 文件对象
     * @param dto  前端发送的输入实体
     * @return key，value对
     */
    @Transactional(rollbackFor = Exception.class)
    public FileUploadRespVO upload(MultipartFile file, FileUploadDTO dto) {
        try {
            Boolean ossIsExist;
            QueryWrapper<SysOss> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("md5", dto.getMd5());
            List<SysOss> sysOssList = sysOssMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(sysOssList)) {
                //oss文件不存在刪除则sys_oss中的相关数据
                ossIsExist = ossIsExist(sysOssList.get(MagicNumbers.ZERO).getOssPath());
                if (Boolean.FALSE.equals(ossIsExist)) {
                    sysOssMapper.delete(queryWrapper);
                }
              } else {
               ossIsExist = false;
            }

            FileUploadRespVO fileUploadRespVO = new FileUploadRespVO();
            Long fileSize = file.getSize();
            boolean isFinish = false;
            //文件已存在，直接返回给前端（秒传功能）并将相关参数存到mysql的sys_oss表中
            if (Boolean.TRUE.equals(ossIsExist)) {
                fileUploadRespVO.setIsFinish(true);
                fileUploadRespVO.setFileName(dto.getFileName());
                SysOss sysOss = new SysOss();
                sysOss.setMd5(dto.getMd5());
                sysOss.setFileName(dto.getFileName());
                sysOss.setCreatedUser(SessionContext.getSessionUser().getUsername());
                sysOss.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                sysOss.setOssPath(sysOssList.get(0).getOssPath());
                sysOss.setFileSize(sysOssList.get(0).getFileSize());
                sysOssService.save(sysOss);
                fileUploadRespVO.setFileId(sysOss.getId());
                fileUploadRespVO.setSysOss(sysOssService.getById(sysOss.getId()));
                return fileUploadRespVO;
            } else {
                //MD5不存在于mysql的sys_oss表中 分片上传
                final byte[] data = file.getBytes();
                String uploadId = dto.getUploadId();
                String ossFileName = null;
                if (dto.getChunkCurrent() == 1) {
                    ossFileName = generateFileName(dto.getFileName());
                    cacheClient.put(ossFileName + "_uploadId", fileSize);
                } else {
                    org.springframework.util.Assert.notNull(uploadId, "数据传输异常中断");
                    ossFileName = (String) cacheClient.get(uploadId);
                    Long oldFileSize = (Long) cacheClient.get(ossFileName + "_uploadId");
                    cacheClient.put(ossFileName + "_uploadId", fileSize + oldFileSize);
                    org.springframework.util.Assert.notNull(ossFileName, "数据传输异常中断");
                }
                //上传oss
                String ossPath = PathUtil.formDirPath(dto.getType()) + ossFileName;
                uploadId = multipartUpload(dto.getType(), ossFileName, data, dto.getChunkTotal(), dto.getChunkCurrent());
                cacheClient.put(uploadId, ossFileName);
                if (dto.getChunkCurrent().equals(dto.getChunkTotal())) {
                    Long totalFileSize = (Long) cacheClient.get(ossFileName + "_uploadId");
                    SysOss sysOss = new SysOss();
                    sysOss.setMd5(dto.getMd5());
                    sysOss.setFileName(dto.getFileName());
                    sysOss.setOssPath(ossPath);
                    sysOss.setFileSize(totalFileSize);
                    sysOss.setCreatedUser(SessionContext.getSessionUser().getUsername());
                    sysOss.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                    sysOssService.save(sysOss);
                    fileUploadRespVO.setFileId(sysOss.getId());
                    fileUploadRespVO.setSysOss(sysOssService.getById(sysOss.getId()));
                    isFinish = true;
                }
                fileUploadRespVO.setFileName(dto.getFileName());
                fileUploadRespVO.setUploadId(uploadId);
                fileUploadRespVO.setIsFinish(isFinish);
                return fileUploadRespVO;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "文件上传异常");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 上传小文件
     *
     * @param workspaceId 工作空间Id
     * @param datas       数据流
     * @param dynamicDir  动态目录地址
     * @param ossFileName oss文件名
     */
    public void uploadOssFile(Integer workspaceId, byte[] datas, String dynamicDir, String ossFileName) {
        String ossPath = PathUtil.formDirPath(dynamicDir) + ossFileName;
        //设置oss配置
        setOssConfig(ossServer,ossBucket,ossAccessKey,ossSecretKey);

        try {
            OsgUtil.putFile(ossPath, datas);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "文件上传oss失败");
        }
    }

    /**
     * 分片上传文件(1片)
     *
     * @param workspaceId 工作空间Id
     * @param dynamicDir  动态文件目录
     * @param ossFileName oss文件名
     * @param uploadId    上传Id
     * @param currentId   当前Id
     * @param total       总数
     * @param data        数据
     * @return 文件名
     */
    public String multiUploadOssFile(Integer workspaceId, String dynamicDir, String ossFileName, String uploadId, int currentId, int total, byte[] data) {
        String ossPath = PathUtil.formDirPath(dynamicDir) + ossFileName;
        //设置oss配置
        setOssConfig(ossServer,ossBucket,ossAccessKey,ossSecretKey);
        try {
            if (StringUtils.isEmpty(uploadId) && currentId != 1) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "文件分片上传失败，uploadId缺失");
            }
            if (StringUtils.isEmpty(uploadId) || currentId == 1) {
                uploadId = OsgUtil.createMultipartUpload(ossPath);
            }
            String eTag = OsgUtil.uploadPart(ossPath, currentId, uploadId, data);
            log.info("{}: set eTag to redis:{}", uploadId, eTag);
            rightPush(uploadId, eTag);
            if (currentId == total) {
                List range = cacheClient.getWithType("eTags_" + uploadId, List.class);
                log.info("{}: get the last eTags from redis:{}", uploadId, range);
                int i = 0;
                assert range != null;
                while (range.size() != total) {
                    try {
                        if (i++ > MagicNumbers.INT_300) {
                            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "文件上传失败，请重试");
                        }
                        Thread.sleep(MagicNumbers.THOUSAND);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "文件上传失败，请重试");
                    }
                }
                OsgUtil.completeMultipartUpload(ossPath, uploadId, range);
            }
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "文件分片上传oss，第[" + currentId + "]片上传失败");
        }
        return uploadId;
    }

    /**
     * 存放值
     *
     * @param uploadId String
     * @param eTag     String
     */
    private void rightPush(String uploadId, String eTag) {
        List<String> eTags = cacheClient.getWithType("eTags_" + uploadId, List.class);
        log.info("{}: get eTags from redis:{}", uploadId, eTags);
        if (null == eTags) {
            eTags = Lists.newArrayList();
        }
        eTags.add(eTag);
        log.info("{}: get eTags from redis:{}", uploadId, eTags);
        cacheClient.put("eTags_" + uploadId, eTags);
    }

    /**
     * 下载oss文件
     *
     * @param workspaceId 工作空间Id
     * @param dynamicDir  动态文件目录
     * @param ossFileName oss文件名
     * @param callback    回调函数
     */
    public void downloadOssFile(Integer workspaceId, String dynamicDir, String ossFileName, Consumer<Pair<InputStream, Long>> callback) {
        AwsConfig awsConfig = null;
        log.info("aws配置：{}", awsConfig);
        String ossPath = PathUtil.formDirPath(dynamicDir) + ossFileName;
        log.info("ossFilePath:{}", ossPath);
        //设置oss配置
        setOssConfig(ossServer,ossBucket,ossAccessKey,ossSecretKey);
        OsgUtil.Response res = null;
        try {
            res = OsgUtil.getFile(ossPath);
            URL fileUrl = OsgUtil.getFileUrl(ossPath);
            Map<String, Object> csvProps = new HashMap<String, Object>(MagicNumbers.EIGHT);
            csvProps.put("url", fileUrl);
            csvProps.put("quote", "\"");
            csvProps.put("seperator", ",");
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            throw new IllegalStateException("OSS获取文件失败，路径" + ossPath, e);
        }
        try (InputStream is = res.getInputStream()) {
            callback.accept(Pair.of(is, res.getContentLength()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "文件解析异常" + e.getMessage());
        }
    }

    /**
     * 生成oss文件名
     *
     * @param fileName 文件名
     * @return oss文件名
     */
    public String generateFileName(String fileName) {
        Assert.isTrue(fileName.lastIndexOf(DOT) > 1, "文件名称不符合标准");
        return fileName.substring(0, fileName.lastIndexOf(DOT)) + "_"
                + DateUtil.format(new Date(), "yyyy_MM_dd_HH_mm_ss_SSS")
                + RandomUtils.nextInt(MagicNumbers.INT_100, MagicNumbers.INT_999)
                + fileName.substring(fileName.lastIndexOf(DOT));
    }


    /**
     * 设置oss配置
     * @param ossServer 路径
     * @param ossBucket 文件夹路径
     * @param ossAccessKey 密钥
     * @param ossSecretKey 密钥
     */
    public void setOssConfig(String ossServer,String ossBucket,String ossAccessKey,String ossSecretKey) {

        OsgUtil.AwsConfig.setServer(ossServer);
        OsgUtil.AwsConfig.setBucket(ossBucket);
        OsgUtil.AwsConfig.setAccessKey(ossAccessKey);
        OsgUtil.AwsConfig.setSecretKey(ossSecretKey);
    }

    /**
     * 获取oss路径URL
     *
     * @param workspaceId Integer
     * @param dynamicDir  String
     * @param ossFileName String
     * @return String
     */
    public String getOssFileUrl(Integer workspaceId, String dynamicDir, String ossFileName) {
        AwsConfig awsConfig = null;
        log.info("aws配置：{}", awsConfig);
        if (ossFileName.startsWith(SLASH)) {
            ossFileName = ossFileName.substring(1);
        }
        String ossPath = PathUtil.formDirPath(dynamicDir).substring(1) + ossFileName;
        log.info("ossFilePath:{}", ossPath);
        //设置oss配置
        setOssConfig(ossServer,ossBucket,ossAccessKey,ossSecretKey);
        URL fileUrl = OsgUtil.getFileUrl(ossPath);
        return fileUrl.toString();
    }


    /**
     *  从 ftp 服务器上传大文件到OSS
     * @param inputStream  输入文件流
     * @param dto 输入实体类对象
     * @return 上传响应对象
     */
    public FileUploadRespVO uploadFtpFileChunkToOss(InputStream inputStream, FileFtpUploadDTO dto) {
        //设置oss配置
        setOssConfig(ossServer,ossBucket,ossAccessKey,ossSecretKey);
        try {
            int chunkSize = MagicNumbers.INT_50 * MagicNumbers.INT_1024 * MagicNumbers.INT_1024;
            byte[] buffer = new byte[chunkSize];
            int totalBytesRead = 0;
            int bytesRead = 0;

            while (totalBytesRead < chunkSize && (bytesRead = inputStream.read(buffer, totalBytesRead, chunkSize - totalBytesRead)) != MagicNumbers.MINUS_INT_1) {
                totalBytesRead += bytesRead;
            }
            inputStream = new ByteArrayInputStream(buffer, 0, totalBytesRead);
            String md5 = MD5.create().digestHex(inputStream);

            //判断MD5是否存在于mysql的sys_oss表中
            FileUploadRespVO fileUploadRespVO;
            QueryWrapper<SysOss> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("md5", md5).eq("file_size", dto.getFileSize());
            List<SysOss> sysOssList = sysOssMapper.selectList(queryWrapper);
            //MD5存在于mysql的sys_oss表中 相关字段设为true，返回给前端（秒传功能）将相关参数存到mysql的sys_oss表中
            if (!CollectionUtils.isEmpty(sysOssList)) {
                log.info("OSS预览文件已经存在,直接返回");
                SysOss sysOss = sysOssList.get(0);
                fileUploadRespVO = FileUploadRespVO.builder().fileId(sysOss.getId()).sysOss(sysOssService.getById(sysOss.getId()))
                        .fileName(dto.getFileName()).isFinish(true).build();
            } else {
                //MD5不存在于mysql的sys_oss表中 分片上传
                String ossFileName = generateFileName(dto.getFileName());
                String ossPath = PathUtil.formDirPath(dto.getOssUploadPath()) + ossFileName;

                Long totalSize = 0L;
                OsgUtil.putFile(ossPath, buffer);
                totalSize = (long)totalBytesRead;

                SysOss sysOss = SysOss.builder().md5(md5).fileName(dto.getFileName()).ossPath(ossPath).fileSize(totalSize)
                                .createdUser(SessionContext.getSessionUser().getUsername())
                                .updatedUser(SessionContext.getSessionUser().getUsername()).build();
                sysOssService.save(sysOss);
                fileUploadRespVO = FileUploadRespVO.builder().fileId(sysOss.getId()).sysOss(sysOssService.getById(sysOss.getId())).fileName(dto.getFileName()).isFinish(true).build();
                log.info("OSS预览文件上传成功. file={}", ossPath);
            }
            return fileUploadRespVO;
        } catch (IOException ex) {
            log.error("文件IO异常", ex.getMessage());
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "文件IO异常");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "OSS文件上传异常");
        }
    }

    /**
     *  从 ftp 服务器上传大文件到OSS
     * @param inputStream  输入文件流
     * @param dto 输入实体类对象
     * @return 上传响应对象
     */
    public FileUploadRespVO uploadFtpFileToOss(InputStream inputStream, FileFtpUploadDTO dto) {
        //设置oss配置
        setOssConfig(ossServer,ossBucket,ossAccessKey,ossSecretKey);
        try {
            String userName = null;
            try {
                userName = SessionContext.getSessionUser().getUsername();
            } catch (Exception e) {
                log.info("Session User not exist.");
            }
            String fileName = dto.getFileName();
            String ossPath = PathUtil.formDirPath(dto.getOssUploadPath()) + generateFileName(fileName);
            SysOss sysOss = uploadMultipartFile(ossPath, inputStream, dto.getFileSize());
            sysOss.setFileName(fileName);
            sysOss.setCreatedUser(userName);
            sysOss.setUpdatedUser(userName);
            sysOssService.saveOrUpdate(sysOss);
            log.info("OSS文件上传成功. file={}", ossPath);
            return FileUploadRespVO.builder().fileId(sysOss.getId()).sysOss(sysOssService.getById(sysOss.getId()))
                    .fileName(dto.getFileName()).isFinish(true).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "OSS文件上传异常:" + e.getMessage());
        }
    }

    private SysOss uploadMultipartFile(String ossPath, InputStream inputStream, long fileSize) {
        ThreadPoolExecutor executor = null;
        try {
            int chunkSize = MagicNumbers.INT_50 * MagicNumbers.INT_1024 * MagicNumbers.INT_1024;
            byte[] buffer = new byte[chunkSize];
            executor = new ThreadPoolExecutor(MagicNumbers.FIVE, MagicNumbers.TEN, MagicNumbers.INT_60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(MagicNumbers.ONE_HUNDRED), Executors.defaultThreadFactory());
            String uploadId = OsgUtil.createMultipartUpload(ossPath);
            List<Future<String>> futureList = new ArrayList<>();
            Long totalSize = 0L;
            int partNumber = 1;
            int totalBytesRead = 0;
            int bytesRead = 0;
            String md5 = null;
            while ((bytesRead = inputStream.read(buffer, 0, chunkSize)) != MagicNumbers.MINUS_INT_1) {
                totalBytesRead += bytesRead;
                while (totalBytesRead < chunkSize && (bytesRead = inputStream.read(buffer, totalBytesRead, chunkSize - totalBytesRead)) != MagicNumbers.MINUS_INT_1) {
                    totalBytesRead += bytesRead;
                }
                if (partNumber == MagicNumbers.ONE) {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer, 0, totalBytesRead);
                    md5 = MD5.create().digestHex(byteArrayInputStream);
                    QueryWrapper<SysOss> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("md5", md5).eq("file_size", fileSize);
                    List<SysOss> sysOssList = sysOssMapper.selectList(queryWrapper);
                    if (!CollectionUtils.isEmpty(sysOssList)) {
                        log.info("OSS文件已经存在,直接返回.");
                        return sysOssList.get(0);
                    }
                }
                Future<String> future = executor.submit(uploadPartCallable(ossPath, partNumber, uploadId, Arrays.copyOf(buffer, totalBytesRead)));
                futureList.add(future);
                totalSize += totalBytesRead;
                totalBytesRead = 0;
                partNumber++;
            }
            List<String> partEtags = new ArrayList<>();
            for (Future<String> future : futureList) {
                partEtags.add(future.get());
            }
            OsgUtil.completeMultipartUpload(ossPath, uploadId, partEtags);
            return SysOss.builder().md5(md5).ossPath(ossPath).fileSize(totalSize).build();
        } catch (URISyntaxException ex) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "OSS文件上传合并异常");
        } catch (IOException ex) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "文件IO异常");
        } catch (ExecutionException ex) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "分片上传异步执行异常");
        } catch (InterruptedException ex) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_UPLOAD_ERROR, "分片上传中断异常");
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
    }

    private static Callable<String> uploadPartCallable(String ossFilePath, int partNo, String uploadId, byte[] data) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                return  OsgUtil.uploadPart(ossFilePath, partNo, uploadId, data);
            }
        };
    }

    /**
     *  判断oss文件是否存在
     * @param path 文件路径
     * @return 是否存在
     */
    public Boolean ossIsExist(String path) {
        //设置oss配置
        setOssConfig(ossServer,ossBucket,ossAccessKey,ossSecretKey);
        try {
            S3Client s3Client = getS3Client();
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(path)
                    .bucket("sys-fileview")
                    .build();
            InputStream objectData =  s3Client.getObject(objectRequest);

            byte[] buffer = new byte[MagicNumbers.INT_1024];
            int bytesRead = objectData.read(buffer);
            objectData.close();
            return bytesRead > 0;
        } catch (URISyntaxException | IOException | S3Exception e) {
            return Boolean.FALSE;
        }
    }

    private S3Client getS3Client() throws URISyntaxException {
        //设置oss配置
        ossFileService.setOssConfig(ossServer, ossBucket, ossAccessKey, ossSecretKey);
        return getClient();
    }

}
