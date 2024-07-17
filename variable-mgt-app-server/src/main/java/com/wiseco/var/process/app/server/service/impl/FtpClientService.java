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

import com.jcraft.jsch.SftpException;
import com.wiseco.boot.commons.io.FileTree;
import com.wiseco.boot.commons.io.FileTypeEnum;
import com.wiseco.boot.commons.io.FtpClient;
import com.wiseco.boot.commons.io.SftpClient;
import com.wiseco.boot.config.BusinessConfigClient;
import com.wiseco.auth.common.config.ConfigFileServerManageOutput;
import com.wiseco.auth.common.config.ConfigFileServerManageQueryInputParam;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xupei
 */
@Service
@Slf4j
public class FtpClientService {
    private static final String SEPARATOR = ":";
    @Resource
    BusinessConfigClient businessConfigClient;

    /**
     *
     * @return 服务列表
     */
    public Map<Long, String> buildServerList() {
        final ConfigFileServerManageQueryInputParam param = ConfigFileServerManageQueryInputParam.builder().enabled("1").build();
        final List<ConfigFileServerManageOutput> configFileServerManageOutputs = businessConfigClient.queryConfigFileServerManageAllList(param);
        return configFileServerManageOutputs.stream().collect(Collectors.toMap(ConfigFileServerManageOutput::getId, ConfigFileServerManageOutput::getName));
    }

    /**
     * buildFileTree
     * @param serverId 服务Id
     * @return 文件树
     */
    public List<FileTree> buildFileTree(Long serverId) {
        final ConfigFileServerManageOutput ftpInfo = businessConfigClient.queryConfigFileServerManageDetail(serverId);
        final String[] address = ftpInfo.getAddress().split(SEPARATOR);
        // 协议类型，1:sftp，2:ftp
        switch (ftpInfo.getProtocolType()) {
            case "1":
                return sftpListFile(ftpInfo, address, ftpInfo.getDefaultPath());
            case "2":
                return ftpListFile(ftpInfo, address, ftpInfo.getDefaultPath());
            default:
                return new ArrayList<>();
        }
    }

    private List<FileTree> sftpListFile(ConfigFileServerManageOutput ftpInfo, String[] address, String directory) {
        SftpClient sftpClient = new SftpClient(ftpInfo.getUsername(), ftpInfo.getPassword(), address[0], Integer.parseInt(address[1]),
                StandardCharsets.UTF_8);
        sftpClient.login();
        final List<FileTree> fileTrees = sftpClient.iterateFile(directory, FileTypeEnum.values());
        sftpClient.logout();
        return fileTrees;
    }

    private List<FileTree> ftpListFile(ConfigFileServerManageOutput ftpInfo, String[] address, String directory) {
        FtpClient ftpClient = new FtpClient(ftpInfo.getUsername(), ftpInfo.getPassword(), address[0], Integer.parseInt(address[1]));
        return ftpClient.iterateFile(directory, null);
    }

    /**
     * getConfig
     * @param serverId 服务Id
     * @return ConfigFileServerManageOutput
     */
    public ConfigFileServerManageOutput getConfig(Long serverId) {
        final ConfigFileServerManageOutput configFileServerManageOutput = businessConfigClient.queryConfigFileServerManageDetail(serverId);
        Assert.notNull(configFileServerManageOutput, "未找到该配置文件服务器信息");
        configFileServerManageOutput.setUsername(null);
        configFileServerManageOutput.setPassword(null);
        return configFileServerManageOutput;
    }

    /**
     * getContent
     * @param serverId 服务Id
     * @param path 路径
     * @param fileName 文件名
     * @return byte[]
     */
    public InputStream getContent(Long serverId, String path, String fileName) {
        final ConfigFileServerManageOutput ftpInfo = businessConfigClient.queryConfigFileServerManageDetail(serverId);
        final String[] address = ftpInfo.getAddress().split(SEPARATOR);
        // 协议类型，1:sftp，2:ftp
        switch (ftpInfo.getProtocolType()) {
            case "1":
                return getSftpFile(ftpInfo, address, path, fileName);
            case "2":
                return getFtpFile(ftpInfo, address, path, fileName);
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "未知的文件服务器类型");
        }
    }

    private InputStream getFtpFile(ConfigFileServerManageOutput ftpInfo, String[] address, String path, String fileName) {
        SftpClient ftpClient = new SftpClient(ftpInfo.getUsername(), ftpInfo.getPassword(), address[0], Integer.parseInt(address[1]), StandardCharsets.UTF_8);
        try {
            return ftpClient.downloadStream(path, fileName);
        } catch (SftpException e) {
            log.error(e.toString(), e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "FTP文件读取失败");
        }
    }

    private InputStream getSftpFile(ConfigFileServerManageOutput ftpInfo, String[] address, String path, String fileName) {
        SftpClient sftpClient = new SftpClient(ftpInfo.getUsername(), ftpInfo.getPassword(), address[0], Integer.parseInt(address[1]), StandardCharsets.UTF_8);
        try {
            sftpClient.login();
            return sftpClient.downloadStream(path, fileName);
        } catch (SftpException e) {
            log.error(e.toString(), e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "SFTP文件读取失败");
        } finally {
            sftpClient.logout();
        }
    }
}
