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

import com.wiseco.boot.commons.io.SftpClient;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.DateFromType;
import com.wiseco.var.process.app.server.enums.OutsideParamImportServiceTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.service.impl.SftpClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 定时+文件方式
 * 从配置信息获取fpt文件中导入参数
 *
 * @author wuweikang
 */
@Service
@Slf4j
public class BacktrackingParamFileImportByFtpServiceImpl implements BacktrackingParamImportService {
    private static final String DATE_FORMAT = "${yyyyMMdd}";
    private static final String DELIMITER = "/";

    @Autowired
    private SftpClientService sftpClientService;

    /**
     * getType
     *
     * @return com.wiseco.var.process.app.server.enums.OutsideParamImportServiceTypeEnum
     */
    @Override
    public OutsideParamImportServiceTypeEnum getType() {
        return OutsideParamImportServiceTypeEnum.FTP_FILE;
    }

    @Override
    public InputStream importDataByFtpFile(BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo, VarProcessBatchBacktrackingTask varProcessBatchBacktrackingTask,
                                           BatchBacktrackingTriggerTypeEnum triggerType, SftpClient sftpClient) {
            if (triggerType == BatchBacktrackingTriggerTypeEnum.SCHEDULED) {
                final BacktrackingSaveInputVO.DataFileScheduled dataFileScheduled = dataGetTypeInfo.getDataFileScheduled();
                log.info("通过ftp文件获取外部传入参数，取数信息:{}", dataFileScheduled);
                return fromFileByScheduled(dataFileScheduled, varProcessBatchBacktrackingTask,sftpClient);
            } else {
                final BacktrackingSaveInputVO.DataFile dataFile = dataGetTypeInfo.getDataFile();
                log.info("通过ftp文件获取外部传入参数，取数信息:{}", dataFile);
                return fromFileByManual(dataFile.getFtpServerId(), dataFile.getDirectory(), dataFile.getFileName(),sftpClient);
            }
    }

    /**
     * fromFtp
     *
     * @param dataFileScheduled dataFileScheduled
     * @param taskInfo          taskInfo
     * @param sftpClient    服务器
     * @return java.io.InputStream
     */
    private InputStream fromFileByScheduled(BacktrackingSaveInputVO.DataFileScheduled dataFileScheduled, VarProcessBatchBacktrackingTask taskInfo,SftpClient sftpClient) {
        //判断是否存在ok文件，存在才能读取
        if (!StringUtils.isEmpty(dataFileScheduled.getOkFile())) {
            String okFilePath = analyzingFilePath(dataFileScheduled.getFilePath(), dataFileScheduled.getDateFrom());
            String okFileName = analyzingFilePath(dataFileScheduled.getOkFile(), dataFileScheduled.getDateFrom());
            if (!okFilePath.endsWith(DELIMITER)) {
                okFilePath = okFilePath + DELIMITER;
            }
            if (!sftpClientService.isExist(okFilePath + okFileName,sftpClient)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "OK文件不存在");
            }
        }

        //如果taskInfo.getDataGetFileFullPath()不为空，说明是重试，使用上一次的路径
        if (!StringUtils.isEmpty(taskInfo.getDataGetFileFullPath())) {
            String dataGetFileFullPath = taskInfo.getDataGetFileFullPath();
            String filePath = dataGetFileFullPath.substring(0, dataGetFileFullPath.lastIndexOf("/") + 1);
            String fileName = dataGetFileFullPath.substring(dataGetFileFullPath.lastIndexOf("/") + 1);
            return sftpClientService.downloadStream(filePath, fileName,sftpClient);
        }

        //使用当前路径
        String filePath = analyzingFilePath(dataFileScheduled.getFilePath(), dataFileScheduled.getDateFrom());
        String fileName = analyzingFilePath(dataFileScheduled.getFileName(), dataFileScheduled.getDateFrom());
        if (!filePath.endsWith(DELIMITER)) {
            filePath = filePath + DELIMITER;
        }
        taskInfo.setDataGetFileFullPath(filePath + fileName);
        return sftpClientService.downloadStream(filePath, fileName, sftpClient);
    }

    /**
     * 取值——手动
     *
     * @param ftpServerId 文件服务器Id
     * @param filePath    文件路径
     * @param fileName    文件名
     * @param sftpClient    服务器
     * @return java.io.InputStream
     */
    private InputStream fromFileByManual(Long ftpServerId, String filePath, String fileName,SftpClient sftpClient) {
        return sftpClientService.downloadStream(filePath, fileName, sftpClient);
    }


    /**
     * analyzingFilePath
     *
     * @param param param
     * @param time  time
     * @return java.lang.String
     */
    private static String analyzingFilePath(String param, DateFromType time) {
        //用户自定义路径
        String result = param;
        if (param.contains(DATE_FORMAT)) {
            String preFix = param.substring(0, param.indexOf(DATE_FORMAT));
            String lastFix = param.substring(preFix.length() + DATE_FORMAT.length());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            if (time == DateFromType.NOW_DATE) {
                result = preFix + dateFormat.format(date) + lastFix;
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DATE, MagicNumbers.MINUS_INT_1);
                result = preFix + dateFormat.format(calendar.getTime()) + lastFix;
            }
        }
        return result;
    }
}
