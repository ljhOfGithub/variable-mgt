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

import com.wiseco.boot.commons.io.FileTree;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.auth.common.config.ConfigFileServerManageOutput;
import com.wiseco.var.process.app.server.service.impl.FtpClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xupei
 */
@RestController
@RequestMapping("/ftp")
@Slf4j
@Tag(name = "FTP共用接口")
public class FtpController {

    @Resource
    private FtpClientService ftpClientService;

    /**
     * ftp下拉选择项
     *
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/list")
    @Operation(summary = "ftp下拉选择项")
    public APIResult<Map<Long, String>> buildServerList() {
        return APIResult.success(ftpClientService.buildServerList());
    }

    /**
     * ftp目录文件信息
     * @param serverId 服务Id
     * @return ftp目录文件信息
     */
    @GetMapping("/info/{serverId}")
    @Operation(summary = "ftp目录文件信息")
    public APIResult<List<FileTree>> buildFileTree(@PathVariable("serverId") Long serverId) {
        return APIResult.success(ftpClientService.buildFileTree(serverId));
    }

    /**
     * ftp配置信息
     *
     * @param serverId serverId
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/config/{serverId}")
    @Operation(summary = "ftp配置信息")
    public APIResult<ConfigFileServerManageOutput> getConfig(@PathVariable("serverId") Long serverId) {
        return APIResult.success(ftpClientService.getConfig(serverId));
    }

}
