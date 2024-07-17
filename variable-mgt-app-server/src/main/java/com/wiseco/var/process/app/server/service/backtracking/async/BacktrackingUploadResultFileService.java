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
package com.wiseco.var.process.app.server.service.backtracking.async;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.sql.StringEscape;
import com.google.common.collect.Lists;
import com.wiseco.boot.commons.io.SftpClient;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingOutputFile;
import com.wiseco.var.process.app.server.enums.BacktrackingDataTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingTaskService;
import com.wiseco.var.process.app.server.service.impl.SftpClientService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 批量回溯上传结果文件
 */
@Slf4j
@Service
public class BacktrackingUploadResultFileService {

    @Resource
    private SftpClientService sftpClientService;
    @Resource
    private BacktrackingTaskService backtrackingTaskService;

    @Resource
    private DbOperateService dbOperateService;

    @Value("${backtracking.task.read_size:10}")
    private int readSize;

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT = "var_process_batch_backtracking_task_result";

    public static final String YYYY_MM_DD = "${yyyyMMdd}";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String SUFFIX = "/";
    public static final String STRING_OK = ".ok";


    /**
     * 上传结果文件
     *
     * @param backtracking     批量回溯实体
     * @param backtrackingTask 批量回溯任务
     */
    public void uploadResultFile(VarProcessBatchBacktracking backtracking, VarProcessBatchBacktrackingTask backtrackingTask) {
        List<String> codeList = dbOperateService.queryForList(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, Collections.singletonList("code"), " task_id = " + backtrackingTask.getId(), null, null, null)
                .stream().map(item -> item.get("code").toString()).collect(Collectors.toList());

        BacktrackingOutputFile backtrackingOutput = JSON.parseObject(backtracking.getOutputInfo(), BacktrackingOutputFile.class);
        //默认路径
        String defaultPath = backtrackingOutput.getDefaultPath();
        //解析自定义路径
        String customPath = analyzingCustomPath(backtrackingOutput.getFilePath(), backtrackingTask.getStartTime());
        String fileNameTemplate = getFileName(backtracking, backtrackingTask, backtrackingOutput);
        Long ftpServerId = backtrackingOutput.getFtpServerId();
        List<List<String>> groupCodeList = Lists.partition(codeList, backtrackingOutput.getFileSplitType() == null ? codeList.size() : backtrackingOutput.getFileSize());
        ArrayList<String> fileNames = new ArrayList<>();
        SftpClient sftpClient = null;
        try {
            sftpClient = sftpClientService.login(ftpServerId);
            //上传结果文件
            for (int i = 0; i < groupCodeList.size(); i++) {
                String fileName = backtrackingOutput.getFileSplitType() != null ? String.format(fileNameTemplate, "_" + (i + 1)) : String.format(fileNameTemplate, "");
                File tempFile = File.createTempFile(backtracking.getName() + "_" + backtrackingTask.getId() + "_result", backtrackingOutput.getFileType().getDesc());
                log.info("批量回溯日志：创建本地结果文件，文件路径->{}", tempFile.getAbsoluteFile());

                //执行结果写入文件
                importResultToFile(tempFile, backtracking, groupCodeList.get(i), backtrackingOutput.getDataType());

                //上传
                fileUpload(tempFile, backtracking, defaultPath, customPath, fileName, sftpClient);

                //记录上传的文件名称
                fileNames.add(fileName);
            }

            String okFileName = String.format(fileNameTemplate, "") + STRING_OK;
            //输出ok文件
            if (Boolean.TRUE.equals(backtrackingOutput.getOkFileFlag())) {
                //手动需要删除原文件
                if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.MANUAL) {
                    if (sftpClientService.isExist(defaultPath + customPath + okFileName, sftpClient)) {
                        sftpClientService.delete(defaultPath + customPath, okFileName, sftpClient);
                    }
                }

                InputStream okInputStream = creatOkFile(backtrackingOutput, codeList.size());
                sftpClientService.upload(defaultPath, customPath, okFileName, okInputStream, sftpClient);
                okInputStream.close();
            }

            //记录结果上传信息
            JSONObject resultFileInfo = new JSONObject();
            resultFileInfo.put("ftpServerId", ftpServerId);
            resultFileInfo.put("directory", defaultPath + customPath);
            resultFileInfo.put("fileName", fileNames);
            backtrackingTask.setStatus(BacktrackingTaskStatusEnum.SUCCESS);
            backtrackingTask.setResultFileInfo(resultFileInfo.toJSONString());
            backtrackingTaskService.updateById(backtrackingTask);
        } catch (Exception e) {
            backtrackingTask.setStatus(BacktrackingTaskStatusEnum.FAIL);
            backtrackingTask.setErrorMessage("上传结果文件失败");
            backtrackingTaskService.updateById(backtrackingTask);
            log.error("上传结果文件失败", e);
        } finally {
            if (sftpClient != null) {
                sftpClientService.logout(sftpClient);
            }
        }
    }


    /**
     * 文件上传
     *
     * @param tempFile     临时文件
     * @param backtracking 批量回溯实体
     * @param defaultPath  默认路径
     * @param customPath   自定义路径
     * @param fileName     文件名称
     * @param sftpClient   文件服务器
     */
    private void fileUpload(File tempFile, VarProcessBatchBacktracking backtracking, String defaultPath, String customPath, String fileName, SftpClient sftpClient) throws Exception {
        try {
            String fullPath = defaultPath + customPath + fileName;
            //如果手动，需要先删除原文件
            if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.MANUAL) {
                if (sftpClientService.isExist(fullPath, sftpClient)) {
                    sftpClientService.delete(defaultPath + customPath, fileName, sftpClient);
                }
                if (sftpClientService.isExist(fullPath + STRING_OK, sftpClient)) {
                    sftpClientService.delete(defaultPath + customPath, fileName + ".ok", sftpClient);
                }
            }

            InputStream resultInPutStream = Files.newInputStream(tempFile.toPath());
            //上传结果文件
            sftpClientService.upload(defaultPath, customPath, fileName, resultInPutStream, sftpClient);
            resultInPutStream.close();
        } finally {
            if (tempFile.exists()) {
                boolean flag = tempFile.delete();
                if (!flag) {
                    log.error("文件删除失败,文件-》{}", tempFile);
                }
            }
        }
    }


    /**
     * 结果文件数据写入
     *
     * @param tempFile                 临时文件
     * @param backtracking             批量回溯实体
     * @param resultCodeList           结果code集合
     * @param backtrackingDataTypeEnum 数据格式
     */
    private void importResultToFile(File tempFile, VarProcessBatchBacktracking backtracking, List<String> resultCodeList, BacktrackingDataTypeEnum backtrackingDataTypeEnum) {
        try {
            BacktrackingOutputFile backtrackingOutput = JSON.parseObject(backtracking.getOutputInfo(), BacktrackingOutputFile.class);
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile.getAbsoluteFile(), true), StandardCharsets.UTF_8))) {
                //分段执行
                List<List<String>> responseCodeGroup = Lists.partition(resultCodeList, readSize);
                boolean isFirst = true;
                for (List<String> responseCodeList : responseCodeGroup) {
                    //获取响应json
                    List<String> resultJsonList = getDataByResultCodes(responseCodeList, Collections.singletonList("response_info")).stream().map(item -> String.valueOf(item.get("response_info"))).collect(Collectors.toList());
                    //写结构化数据
                    if (backtrackingOutput.getDataType() == BacktrackingDataTypeEnum.STRUCTURED) {
                        ArrayList<String> varNames = new ArrayList<>(JSON.parseObject(resultJsonList.get(0)).keySet());
                        if (isFirst) {
                            for (int i = 0; i < varNames.size(); i++) {
                                writer.write(varNames.get(i));
                                if (i != varNames.size() - 1) {
                                    writer.write(",");
                                }
                            }
                            isFirst = false;
                        }

                        for (String resultData : resultJsonList) {
                            writer.write("\r\n");
                            JSONObject jsonObject = JSON.parseObject(resultData);
                            for (int i = 0; i < varNames.size(); i++) {
                                writer.write(jsonObject.get(varNames.get(i)).toString());
                                if (i != varNames.size() - 1) {
                                    writer.write(",");
                                }
                            }
                        }
                    } else {
                        if (backtrackingDataTypeEnum == BacktrackingDataTypeEnum.JSON_STRING) {
                            //加转义
                            resultJsonList = resultJsonList.stream().map(StringEscape::escapeRawString).collect(Collectors.toList());
                        } else if (backtrackingDataTypeEnum == BacktrackingDataTypeEnum.XML) {
                            resultJsonList = resultJsonList.stream().map(item -> JSONUtil.toXmlStr(new cn.hutool.json.JSONObject(item))).collect(Collectors.toList());
                        }

                        //写json
                        for (String result : resultJsonList) {
                            writer.write(result);
                            writer.write("\r\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "结果文件数据写入异常");
        }
    }

    /**
     * 解析动态参数
     *
     * @param param     param
     * @param startTime 开始时间
     * @return String
     */
    private static String analyzingCustomPath(String param, Date startTime) {
        if (StringUtils.isEmpty(param)) {
            return SUFFIX;
        }
        //用户自定义路径
        String result = param;
        if (result.contains(YYYY_MM_DD)) {
            String preFix = result.substring(0, result.indexOf(YYYY_MM_DD));
            SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDD);
            result = preFix + dateFormat.format(startTime);
        }
        if (!result.endsWith(SUFFIX)) {
            result += SUFFIX;
        }
        return result;
    }


    /**
     * 获取文件名称
     *
     * @param backtracking       批量回溯实体
     * @param taskInfo           任务信息
     * @param backtrackingOutput 批量回溯输出信息
     * @return java.lang.String
     */
    private String getFileName(VarProcessBatchBacktracking backtracking, VarProcessBatchBacktrackingTask taskInfo, BacktrackingOutputFile backtrackingOutput) {
        //文件名
        StringBuilder fileName = new StringBuilder(backtrackingOutput.getFileName());
        if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.SCHEDULED) {
            String startTime = LocalDate.now().format(dateFormatter) + " 00:00:00";
            String endTime = LocalDate.now().format(dateFormatter) + " 23:59:59";
            List<VarProcessBatchBacktrackingTask> taskList = backtrackingTaskService.list(new LambdaQueryWrapper<VarProcessBatchBacktrackingTask>()
                    .select(VarProcessBatchBacktrackingTask::getId)
                    .eq(VarProcessBatchBacktrackingTask::getBacktrackingId, backtracking.getId())
                    .ge(VarProcessBatchBacktrackingTask::getStartTime, startTime)
                    .le(VarProcessBatchBacktrackingTask::getStartTime, endTime)
                    .orderByAsc(VarProcessBatchBacktrackingTask::getCreatedTime));

            for (int i = 0; i < taskList.size(); i++) {
                if (Objects.equals(taskList.get(i).getId(), taskInfo.getId())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(YYYYMMDD);
                    fileName.append(dateFormat.format(taskInfo.getStartTime())).append("_").append(i + 1);
                    break;
                }
            }
        }
        fileName.append("%s").append(backtrackingOutput.getFileType().getDesc());
        return fileName.toString();
    }


    /**
     * 创建成功文件
     *
     * @param backtrackingOutput backtrackingOutput
     * @param taskTotal          taskTotal
     * @return java.io.InputStream
     */
    private InputStream creatOkFile(BacktrackingOutputFile backtrackingOutput, int taskTotal) throws IOException {
        File tempFile = File.createTempFile("temp", backtrackingOutput.getFileType().getDesc());
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(tempFile.getAbsoluteFile().toPath()), StandardCharsets.UTF_8)) {
            writer.write(String.valueOf(taskTotal));
        } catch (IOException e) {
            throw new IOException(e);
        }
        return Files.newInputStream(tempFile.toPath());
    }

    /**
     * 查询数据
     *
     * @param resultCodeList 结果表唯一标识list
     * @param colum          查询的列
     * @return 数据
     */
    private List<Map<String, Object>> getDataByResultCodes(List<String> resultCodeList, List<String> colum) {
        StringBuilder condition = new StringBuilder("code in(");
        for (String resultCode : resultCodeList) {
            condition.append("'").append(resultCode).append("',");
        }
        condition.deleteCharAt(condition.length() - 1);
        condition.append(")");
        return dbOperateService.queryForList(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, colum, condition.toString(), null, null, null);
    }

}
