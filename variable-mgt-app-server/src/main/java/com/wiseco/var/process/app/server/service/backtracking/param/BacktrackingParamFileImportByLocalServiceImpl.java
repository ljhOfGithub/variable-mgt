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
package com.wiseco.var.process.app.server.service.backtracking.param;

import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.OutsideParamImportServiceTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.SysOss;
import com.wiseco.var.process.app.server.service.SysOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;

/**
 * 手动+文件方式
 * 从本地文件或者ftp选择的文件中导入参数
 *
 * @author xupei
 */
@Service
@Slf4j
public class BacktrackingParamFileImportByLocalServiceImpl implements BacktrackingParamImportService {

    @Autowired
    private SysOssService sysOssService;
    @Override
    public OutsideParamImportServiceTypeEnum getType() {
        return OutsideParamImportServiceTypeEnum.LOCAL_FILE;
    }


    @Override
    public InputStream importDataByLocalFile(S3Client s3Client,BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo) {
        final BacktrackingSaveInputVO.DataFile dataFile = dataGetTypeInfo.getDataFile();
        SysOss sysOss = sysOssService.getById(dataFile.getLocalFileId());
        try {
            //todo bucket写死，后续可能需要优化
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(sysOss.getOssPath())
                    .bucket("sys-fileview")
                    .build();
            return s3Client.getObject(objectRequest);
        } catch (Exception e) {
            log.error("取值文件获取失败，sysOss->{}",sysOss);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "取值文件获取失败");
        }
    }
}
