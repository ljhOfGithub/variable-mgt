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
package com.wiseco.var.process.app.server.service.impl;

import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.wiseco.auth.common.config.ConfigFileServerManageOutput;
import com.wiseco.boot.commons.io.SftpClient;
import com.wiseco.boot.config.BusinessConfigClient;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author mingao
 * @since 2023/8/28
 */
@Service
@Slf4j
public class SftpClientService {
    @Resource
    BusinessConfigClient businessConfigClient;

    /**
     * login
     *
     * @param dataSourceServerId 服务器id
     * @return SftpClient 文件服务器
     */
    public SftpClient login(Long dataSourceServerId) {
        ConfigFileServerManageOutput ftpInfo = businessConfigClient.queryConfigFileServerManageDetail(dataSourceServerId);
        String[] address = ftpInfo.getAddress().split(":");
        SftpClient sftpClient = new SftpClient(ftpInfo.getUsername(), ftpInfo.getPassword(), address[0], Integer.parseInt(address[1]), StandardCharsets.UTF_8);
        sftpClient.login();
        return sftpClient;
    }

    /**
     * logout
     *
     * @param sftpClient 服务器客户端
     */
    public void logout(SftpClient sftpClient) {
        sftpClient.logout();
    }


    /**
     * downloadStream
     *
     * @param directory    文件夹路径
     * @param downloadFile 文件名称
     * @param sftpClient   服务器客户端
     * @return InputStream
     */
    public InputStream downloadStream(String directory, String downloadFile, SftpClient sftpClient) {
        InputStream is = null;
        try {
            is = sftpClient.downloadStream(directory, downloadFile);
        } catch (Exception e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "服务器文件下载错误: " + directory);
        }
        return is;
    }

    /**
     * upload
     *
     * @param basePath           默认路径
     * @param directory          用户输入路径
     * @param sftpFileName       文件名称
     * @param input              文件输入流
     * @param sftpClient          服务器
     */
    public void upload(String basePath, String directory, String sftpFileName, InputStream input, SftpClient sftpClient) {
        try {
            sftpClient.upload(basePath, directory, sftpFileName, input);
        } catch (SftpException e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_WRITE_ERROR, "文件服务器上传时异常");
        } catch (IOException e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_WRITE_ERROR, "文件上传时IO异常");
        }
    }

    /**
     * delete
     *
     * @param directory  文件夹路径
     * @param deleteFile 文件名称
     * @param sftpClient 文件服务器
     */
    public void delete(String directory, String deleteFile, SftpClient sftpClient) {
        try {
            sftpClient.delete(directory, deleteFile);
        } catch (Exception e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "在文件服务器上删除文件发生错误");
        }
    }


    /**
     * isExist
     *
     * @param path       文件路径
     * @param sftpClient 服务器
     * @return boolean
     */
    public boolean isExist(String path, SftpClient sftpClient) {
        return sftpClient.isExist(path);
    }

    /**
     * 获取文件属性
     *
     * @param path       文件路径
     * @param sftpClient 服务器
     * @return 文件属性
     */
    public SftpATTRS getFileAttrs(String path, SftpClient sftpClient) {
        return sftpClient.getFileAttr(path);
    }
}
