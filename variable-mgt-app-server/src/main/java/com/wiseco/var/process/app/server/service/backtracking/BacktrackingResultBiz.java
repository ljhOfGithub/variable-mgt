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
package com.wiseco.var.process.app.server.service.backtracking;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.boot.commons.io.SftpClient;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingTaskDataQueryVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingTaskOutsideDataQueryVO;
import com.wiseco.var.process.app.server.controller.vo.input.TaskInfoQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingOutsideVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingTaskListDetailVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingTaskQueryOutputDataVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingTaskQueryOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.OutsideTaskInfoOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.TaskInfoOutputVO;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessOutsideRef;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.VarProcessOutsideRefService;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskDetailDto;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskQueryDto;
import com.wiseco.var.process.app.server.service.impl.SftpClientService;
import com.wisecotech.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 批量回溯执行结果查询
 */
@RefreshScope
@Service
@Slf4j
public class BacktrackingResultBiz {

    public static final String FROM = "from";
    static final String SERVICE_ID = "serviceId";
    static final String BATCH_NO = "batchNo";
    static final String CALL_SUCCESS = "callSuccess";
    static final String BUSINESS_SUCCESS = "businessSuccess";
    static final String BUSINESS_SERIAL_NO = "businessSerialNo";
    static final String DECISION_SERIAL_NO = "decisionSerialNo";
    static final String REUQEST_START_DATE_FROM = "reuqestStartDateFrom";
    static final String REUQEST_START_DATE_TO = "reuqestStartDateTo";
    static final String COST_MILLISECOND_FROM = "costMillisecondFrom";
    static final String COST_MILLISECOND_TO = "costMillisecondTo";
    static final String TRY_TIMES_FROM = "tryTimesFrom";
    static final String TRY_TIMES_TO = "tryTimesTo";
    private static final String OUTSIDE_SERVER_LOG = "trace_report_external";
    private static final String VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT = "var_process_batch_backtracking_task_result";
    public static final String TASK_ID = "taskId";
    public static final String TASK_STATUS = "taskStatus";
    public static final String TO = "to";
    public static final String COST_MILLISECOND = "cost_millisecond";
    @Resource
    private BacktrackingTaskService backtrackingTaskService;
    @Resource
    private BacktrackingDataModelService backtrackingDataModelService;
    @Resource
    private VarProcessBatchBacktrackingMapper varBatchBacktrackingMapper;
    @Resource
    private VarProcessDataModelService varProcessDataModelService;
    @Resource
    private VarProcessOutsideRefService varProcessOutsideRefService;
    @Resource
    private DbOperateService dbOperateService;
    @Resource
    private BacktrackingResultDatabaseService backtrackingResultDatabaseService;
    @Resource
    private SftpClientService sftpClientService;

    /**
     * 查询结果集文件
     *
     * @param taskId 任务id
     * @return 查询结果集文件
     */
    public List<String> getResultFile(Long taskId) {
        VarProcessBatchBacktrackingTask task = backtrackingTaskService.getById(taskId);
        Assert.notNull(task, "找不到任务");
        String resultFileInfoJson = task.getResultFileInfo();
        if (StringUtils.isEmpty(resultFileInfoJson)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_RESULT_EXPORT_ERROR, "结果文件暂未生成");
        }
        JSONObject resultFileInfo = JSONObject.parseObject(resultFileInfoJson);
        return JSONObject.parseArray(resultFileInfo.get("fileName").toString(), String.class);
    }


    /**
     * 导出结果集文件
     *
     * @param taskId   任务id
     * @param fileName 文件名称
     * @param response response
     */
    public void exportResultFile(Long taskId, String fileName, HttpServletResponse response) {
        log.info("fileName->{}", fileName);
        VarProcessBatchBacktrackingTask task = backtrackingTaskService.getById(taskId);
        Assert.notNull(task, "找不到任务");
        String resultFileInfoJson = task.getResultFileInfo();
        if (StringUtils.isEmpty(resultFileInfoJson)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_RESULT_EXPORT_ERROR, "该任务没有结果文件");
        }

        JSONObject resultFileInfo = JSONObject.parseObject(resultFileInfoJson);
        Long ftpServiceId = Long.valueOf(resultFileInfo.get("ftpServerId").toString());
        String directory = resultFileInfo.get("directory").toString();

        SftpClient sftpClient = sftpClientService.login(ftpServiceId);
        try (InputStream inputStream = sftpClientService.downloadStream(directory, fileName, sftpClient);
             OutputStream outputStream = response.getOutputStream()) {

            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");

            byte[] buffer = new byte[MagicNumbers.INT_10240];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != MagicNumbers.MINUS_INT_1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            log.info("fileName->{}", ftpServiceId);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_RESULT_EXPORT_ERROR, "结果文件导出失败");
        } finally {
            sftpClientService.logout(sftpClient);
        }
    }

    /**
     * 执行记录列表
     *
     * @param inputVO 输入
     * @return com.baomidou.mybatisplus.core.metadata.IPage
     */
    public IPage<TaskInfoOutputVO> getTaskPage(TaskInfoQueryInputVO inputVO) {
        Page<TaskInfoOutputVO> page = new Page<>(inputVO.getCurrentNo(), inputVO.getSize());

        BacktrackingTaskQueryDto taskQueryDto = new BacktrackingTaskQueryDto();
        BeanUtils.copyProperties(inputVO, taskQueryDto);

        IPage<BacktrackingTaskDetailDto> pageList = backtrackingTaskService.findBacktrackingTaskList(page, taskQueryDto);

        if (CollectionUtils.isEmpty(pageList.getRecords())) {
            return page;
        }

        List<TaskInfoOutputVO> taskInfosList = new ArrayList<>();
        for (int i = 0; i < pageList.getRecords().size(); i++) {
            BacktrackingTaskDetailDto task = pageList.getRecords().get(i);
            TaskInfoOutputVO taskInfoOutput = new TaskInfoOutputVO();
            BeanUtils.copyProperties(task, taskInfoOutput);
            taskInfoOutput.setIsExportResultFile(false);
            if (task.getStatus() == BacktrackingTaskStatusEnum.IN_PROGRESS) {
                taskInfoOutput.setEndTime(null);
                taskInfoOutput.setDuration(null);
            } else if (task.getStatus() == BacktrackingTaskStatusEnum.FAIL) {
                taskInfoOutput.setMaximumResponseTime(null);
                taskInfoOutput.setMinimumResponseTime(null);
                taskInfoOutput.setAverageResponseTime(null);
            } else if (task.getStatus() == BacktrackingTaskStatusEnum.SUCCESS || task.getStatus() == BacktrackingTaskStatusEnum.FILE_GENERATING) {
                taskInfoOutput.setAverageResponseTime(limitAverageResponseTime(task.getAverageResponseTime()));
                if (!StringUtils.isEmpty(task.getResultFileInfo())) {
                    taskInfoOutput.setIsExportResultFile(true);
                }
            } else if (task.getStatus() == BacktrackingTaskStatusEnum.PAUSED) {
                taskInfoOutput.setEndTime(null);
                taskInfoOutput.setDuration(null);
            }
            taskInfosList.add(taskInfoOutput);
        }

        page.setTotal(pageList.getTotal());
        page.setPages(pageList.getPages());
        page.setRecords(taskInfosList);

        return page;
    }

    private String limitAverageResponseTime(float averageResponseTime) {
        String averageResponseTimeLimit;
        if (averageResponseTime == 0.0) {
            averageResponseTimeLimit = String.valueOf(0);
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            averageResponseTimeLimit = df.format(averageResponseTime);
        }
        return averageResponseTimeLimit;
    }

    /**
     * 外数记录
     *
     * @param backtrackingId 批量回溯ID
     * @param batchNumber    批次号
     * @return 外数执行记录列表
     */
    public List<OutsideTaskInfoOutputVO> getOverallOutsidePage(Long backtrackingId, String batchNumber) {
        final List<VarProcessBatchBacktrackingDataModel> dataModelList = backtrackingDataModelService.findListByBacktrackingId(backtrackingId);
        // 外数服务list
        final List<VarProcessBatchBacktrackingDataModel> outsideList = dataModelList.stream().filter(dataModel -> dataModel.getSourceType() == VarProcessDataModelSourceType.OUTSIDE_SERVER).collect(Collectors.toList());

        List<OutsideTaskInfoOutputVO> retList = new ArrayList<>();
        for (VarProcessBatchBacktrackingDataModel backtrackingDataModel : outsideList) {
            final OutsideTaskInfoOutputVO outsideTaskInfo = OutsideTaskInfoOutputVO.builder().build();
            // 设置外数服务Id
            VarProcessDataModel dataModel = varProcessDataModelService.findByDataModelInfo(backtrackingDataModel.getObjectName(), backtrackingDataModel.getObjectVersion());
            VarProcessOutsideRef outsideRef = varProcessOutsideRefService.findByDataModelId(dataModel.getId());
            outsideTaskInfo.setOutsideServiceId(outsideRef.getOutsideServiceId());
            outsideTaskInfo.setOutsideServiceCode(outsideRef.getOutsideServiceCode());
            outsideTaskInfo.setOutsideServiceName(outsideRef.getOutsideServiceName());
            outsideTaskInfo.setOutsideServiceStrategy(backtrackingDataModel.getOutsideServiceStrategy());

            Map<String, Object> param = new HashMap<>(MagicNumbers.EIGHT);
            param.put("batchNo", batchNumber);
            param.put("serviceId", outsideRef.getOutsideServiceId());
            //调用量
            long count = backtrackingResultDatabaseService.count(OUTSIDE_SERVER_LOG, param);
            outsideTaskInfo.setCallSize(count);

            outsideTaskInfo.setSuccessRate("0%");
            outsideTaskInfo.setFindRate("0%");
            if (count != 0) {
                //成功调用量
                param.put("call_success", 1);
                long successCount = backtrackingResultDatabaseService.count(OUTSIDE_SERVER_LOG, param);
                String callSuccessRate = new DecimalFormat("0.00%").format((double) successCount / count);
                outsideTaskInfo.setSuccessRate(callSuccessRate);

                //查得率
                param.remove("call_success");
                param.put("business_success", 1);
                long findCount = backtrackingResultDatabaseService.count(OUTSIDE_SERVER_LOG, param);
                String findSuccessRate = new DecimalFormat("0.00%").format((double) findCount / count);
                outsideTaskInfo.setFindRate(findSuccessRate);
            }

            //最大响应时间
            long maxResponseTime = backtrackingResultDatabaseService.max(OUTSIDE_SERVER_LOG, COST_MILLISECOND, param);
            outsideTaskInfo.setMaximumResponseTime(maxResponseTime);

            //最小响应时间
            long minResponseTime = backtrackingResultDatabaseService.min(OUTSIDE_SERVER_LOG, COST_MILLISECOND, param);
            outsideTaskInfo.setMinimumResponseTime(minResponseTime);

            //平均响应时间
            float averageResponseTime = backtrackingResultDatabaseService.avg(OUTSIDE_SERVER_LOG, COST_MILLISECOND, param);
            outsideTaskInfo.setAverageResponseTime(limitAverageResponseTime(averageResponseTime));

            retList.add(outsideTaskInfo);
        }
        return retList;
    }

    /**
     * 选择任务编号下拉列表
     *
     * @param backtrackingId 批量回溯Id
     * @return 任务编号下拉列表
     */
    public List<String> getBacktrackingTaskCodes(Long backtrackingId) {
        return varBatchBacktrackingMapper.getBacktrackingTaskCodes(backtrackingId);
    }


    /**
     * 执行数据明细列表
     *
     * @param inputVO 入参
     * @return 执行数据明细列表
     */
    public IPage<BacktrackingTaskQueryOutputDataVO> getTaskDataPage(BacktrackingTaskDataQueryVO inputVO) {

        ConditionSqlAndPrams conditionSqlAndPrams = getConditionSqlAndPrams(inputVO, Arrays.asList("currentNo", "size", "serialVersionUID"));
        //查询数据
        List<String> selectColum = Arrays.asList("serial_no", "start_time", "end_time", "status", "code");
        Page<BacktrackingTaskQueryOutputVO> page = new Page<>(inputVO.getCurrentNo(), inputVO.getSize());
        List<BacktrackingTaskQueryOutputVO> pageList = dbOperateService.queryPage(VAR_PROCESS_BATCH_BACKTRACKING_TASK_RESULT, page, selectColum, conditionSqlAndPrams.getWhereSql(), conditionSqlAndPrams.getPrams(), BacktrackingTaskQueryOutputVO.class).getRecords();

        //组装输出
        List<BacktrackingTaskQueryOutputDataVO> resultList = new ArrayList<>();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (BacktrackingTaskQueryOutputVO backtracking : pageList) {
            BacktrackingTaskQueryOutputDataVO outputVO = new BacktrackingTaskQueryOutputDataVO();
            outputVO.setSerialNo(backtracking.getSerialNo());
            outputVO.setTaskStatus(backtracking.getStatus());
            outputVO.setResultCode(backtracking.getCode());
            resultList.add(outputVO);
            if (backtracking.getStartTime() != 0 && backtracking.getEndTime() != 0) {
                outputVO.setStartTime(outputFormat.format(new Date(backtracking.getStartTime())));
                outputVO.setResponseTime(backtracking.getEndTime() - backtracking.getStartTime());
            }
        }

        // 分页设置
        Page<BacktrackingTaskQueryOutputDataVO> resultPage = new Page<>(inputVO.getCurrentNo(), inputVO.getSize());
        resultPage.setRecords(resultList);
        resultPage.setTotal(page.getTotal());
        return resultPage;
    }

    /**
     * 执行记录详细内容查看
     *
     * @param resultCode 结果编码
     * @return 执行记录查看列表VO
     */
    public BacktrackingTaskListDetailVO getResultDetail(String resultCode) {
        return backtrackingResultDatabaseService.getResultDetail(resultCode);
    }

    /**
     * 分页查询外数服务调用查看列表
     *
     * @param inputVO 输入实体类对象
     * @return 分页查询外数服务调用查看列表
     */
    public IPage<BacktrackingOutsideVO> getOutsidePage(BacktrackingTaskOutsideDataQueryVO inputVO) {
        Page<BacktrackingOutsideVO> page = new Page<>(inputVO.getCurrentNo(), inputVO.getSize());
        List<String> exclusions = Arrays.asList("currentNo", "size", "serialVersionUID");
        ConditionSqlAndPrams conditionSqlAndPrams = getConditionSqlAndPrams(inputVO, exclusions);
        //查询数据
        List<BacktrackingOutsideVO> resultList = dbOperateService.queryPage(OUTSIDE_SERVER_LOG, page, null, conditionSqlAndPrams.getWhereSql(), conditionSqlAndPrams.getPrams(), BacktrackingOutsideVO.class).getRecords();
        resultList.sort((dto1, dto2) -> {
            // 倒序比较data字段
            return dto2.getReuqestStartDate().compareTo(dto1.getReuqestStartDate());
        });
        page.setRecords(resultList);
        return page;
    }

    /**
     * 获取条件sql和参数
     *
     * @param object     对象
     * @param exclusions 已经用过的集合
     * @return 获取SQL和参数VO
     */
    public ConditionSqlAndPrams getConditionSqlAndPrams(Object object, List<String> exclusions) {
        List<Object> fieldValues = new ArrayList<>();
        Class<?> objectClass = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();

        StringBuilder whereSql = new StringBuilder(" 1 = 1 ");
        try {
            for (Field field : fields) {
                // 如果字段不在排除列表中，获取字段的值并添加到结果列表
                if (!exclusions.contains(field.getName())) {
                    // 设置字段为可访问
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if (value != null && !value.toString().isEmpty()) {
                        fieldValues.add(value);
                        whereSql.append(getObjectFieldsWithoutExclusions(field.getName()));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ConditionSqlAndPrams conditionSqlAndPrams = new ConditionSqlAndPrams();
        conditionSqlAndPrams.setWhereSql(whereSql.toString());
        conditionSqlAndPrams.setPrams(fieldValues.toArray(new Object[0]));
        return conditionSqlAndPrams;
    }

    /**
     * getObjectFieldsWithoutExclusions
     *
     * @param name 名称
     * @return msg
     */
    private String getObjectFieldsWithoutExclusions(String name) {
        String msg = "";
        if (SERVICE_ID.equals(name)) {
            msg = " AND service_id = ?";
        }
        if (BATCH_NO.equals(name)) {
            msg = " AND batch_no = ?";
        }
        if (CALL_SUCCESS.equals(name)) {
            msg = " AND call_success = ?";
        }
        if (BUSINESS_SUCCESS.equals(name)) {
            msg = " AND business_success = ?";
        }
        if (BUSINESS_SERIAL_NO.equals(name)) {
            msg = " AND business_serial_no = ?";
        }
        if (DECISION_SERIAL_NO.equals(name)) {
            msg = " AND decision_serial_no = ?";
        }
        if (REUQEST_START_DATE_FROM.equals(name)) {
            msg = " AND reuqest_start_date >= ?";
        }
        if (REUQEST_START_DATE_TO.equals(name)) {
            msg = " AND reuqest_start_date <= ?";
        }
        if (COST_MILLISECOND_FROM.equals(name)) {
            msg = " AND cost_millisecond >= ?";
        }
        if (COST_MILLISECOND_TO.equals(name)) {
            msg = " AND cost_millisecond <= ?";
        }
        if (TRY_TIMES_FROM.equals(name)) {
            msg = " AND try_times >= ?";
        }
        if (TRY_TIMES_TO.equals(name)) {
            msg = " AND try_times <= ?";
        }
        if (TASK_ID.equals(name)) {
            msg = " AND task_id = ?";
        }
        if (TASK_STATUS.equals(name)) {
            msg = " AND status = ?";
        }
        if (FROM.equals(name)) {
            msg = " AND (end_time - start_time) >= ?";
        }
        if (TO.equals(name)) {
            msg = " AND (end_time - start_time) <= ?";
        }
        return msg;
    }


    /**
     * sql查询条件和参数
     *
     * @author wuweikang
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ConditionSqlAndPrams {
        /**
         * 条件ql
         */
        private String whereSql;
        /**
         * 参数
         */
        private Object[] prams;
    }
}
