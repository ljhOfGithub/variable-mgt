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
package com.wiseco.var.process.app.server.commons.util;

import com.wiseco.boot.cache.CacheClient;
import com.wiseco.job.client.client.WisecoJobClient;
import com.wiseco.var.process.app.server.commons.BaseWisecoJobParam;
import com.wiseco.var.process.app.server.commons.constant.CacheKeyPrefixConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.cron.PageJobExecuteConfig;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.MonitoringConfTimeUnitEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.innerdata.TaskInfoDto;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import tech.powerjob.common.enums.DispatchStrategy;
import tech.powerjob.common.enums.ExecuteType;
import tech.powerjob.common.enums.ProcessorType;
import tech.powerjob.common.enums.TimeExpressionType;
import tech.powerjob.common.model.LifeCycle;
import tech.powerjob.common.request.http.SaveJobInfoRequest;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import java.util.List;

/**
 * @author ycc
 * @since 2023/1/12 13:44
 */
@Component
@Slf4j
public class WisecoJobOperateUtil {
    /**
     * 任务正常状态
     */
    private static final int    ENABLE_JOB_STATUS  = 1;
    private static final String JOB_ID_NAME        = "jobId";
    private static final String JOB_REQUEST_NAME   = "request";

    @Autowired(required = false)
    private WisecoJobClient     wisecoJobClient;

    @Autowired
    @Qualifier(value = "remoteCacheClient")
    private CacheClient         cacheClient;

    /**
     * 创建按cron表达式单实例执行（集群排他执行）的定时任务
     *
     * @param jobName        任务名称(确保唯一)
     * @param jobProcessor   处理该定时任务的处理器的class对象
     * @param cron           任务执行的cron表达式
     * @param jobDescription 任务描述
     * @param jobParam       任务参数，任务处理器可以获取到的业务参数
     * @param startTime      任务生效时间，毫秒值，为空时表示立即开始
     * @param endTime        任务失效时间，毫秒值，为空时表示不失效
     */
    public void addBasicCronJob(String jobName, Class<? extends BasicProcessor> jobProcessor, String cron, String jobDescription,
                                BaseWisecoJobParam jobParam, Long startTime, Long endTime) {
        log.info("创建定时,jobName:{}", jobName);
        JobInfoDTO jobInfoDTO = findJob(jobName);
        if (jobInfoDTO != null) {
            //任务已存在
            if (jobInfoDTO.getStatus() == ENABLE_JOB_STATUS) {
                log.info("定时任务已存在,状态正常,jobName:{}", jobName);
            } else {
                //该定时任务状态为停止，重新启用
                log.info("定时任务已存在,状态为已禁用，启用该任务,jobName:{}", jobName);
                enableJob(jobName);
            }
            return;
        }

        SaveJobInfoRequest request = buildBasicRequest(jobName, jobProcessor, cron, jobDescription, jobParam, startTime, endTime);
        ResultDTO<Long> result = wisecoJobClient.saveJob(request);
        log.info("创建定时任务[{}]返回:{}", jobName, JSON.toJSONString(result));
        if (!result.isSuccess()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_DATABASE_ERROR, "创建定时任务失败[" + jobName + "]");
        }
        Long jobId = result.getData();
        //这里把任务配置信息缓存一下，后续的删除、更新、查找操作会用到
        JSONObject jobValue = new JSONObject();
        jobValue.put(JOB_ID_NAME, jobId);
        jobValue.put(JOB_REQUEST_NAME, request);
        cacheClient.put(getJobCacheKey(jobName), jobValue.toJSONString());
    }

    /**
     * 修改按cron表达式单实例执行（集群排他执行）的定时任务
     * @param jobName 任务名
     * @param jobProcessor 任务处理器
     * @param cron cron表达式
     * @param jobDescription 任务描述
     * @param jobParam 任务参数
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public void updateBasicCronJob(String jobName, Class<? extends BasicProcessor> jobProcessor, String cron, String jobDescription,
                                   BaseWisecoJobParam jobParam, Long startTime, Long endTime) {
        log.info("更新定时任务,jobName:{}", jobName);
        Long jobId = getJobId(jobName);
        if (jobId == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "定时任务不存在，无法修改[" + jobName + "]");
        }
        SaveJobInfoRequest request = buildBasicRequest(jobName, jobProcessor, cron, jobDescription, jobParam, startTime, endTime);
        request.setId(jobId);
        ResultDTO<Long> result = wisecoJobClient.saveJob(request);
        log.info("创建定时任务返回:{}", JSON.toJSONString(result));
        if (!result.isSuccess()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_DATABASE_ERROR, "创建定时任务失败[" + jobName + "]");
        }
        JSONObject jobValue = new JSONObject();
        jobValue.put(JOB_ID_NAME, jobId);
        jobValue.put(JOB_REQUEST_NAME, request);

       cacheClient.put(getJobCacheKey(jobName), jobValue.toJSONString());
    }

    /**
     * 修改按cron表达式单实例执行（集群排他执行）的定时任务，仅更新其corn表达式
     * @param jobName 任务名
     * @param cron cron表达式
     */
    public void updateBasicCronJob(String jobName, String cron) {
        log.info("更新定时任务,jobName:{}", jobName);
        Object value = cacheClient.get(getJobCacheKey(jobName));
        if (value == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "定时任务不存在，无法修改[" + jobName + "]");
        }
        JSONObject jobValue = JSON.parseObject(String.valueOf(value));
        long jobId = Long.parseLong(String.valueOf(jobValue.get(JOB_ID_NAME)));
        SaveJobInfoRequest req = JSON.parseObject(String.valueOf(jobValue.get(JOB_REQUEST_NAME)), SaveJobInfoRequest.class);
        req.setId(jobId);
        req.setTimeExpression(cron);
        ResultDTO<Long> result = wisecoJobClient.saveJob(req);
        log.info("更新定时任务返回:{}", JSON.toJSONString(result));
        if (!result.isSuccess()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_DATABASE_ERROR, "更新定时任务失败[" + jobName + "]");
        }
        jobValue.put(JOB_ID_NAME, jobId);
        jobValue.put(JOB_REQUEST_NAME, req);
       cacheClient.put(getJobCacheKey(jobName), jobValue.toJSONString());
    }

    /**
     * 查找某个任务
     *
     * @param jobName  任务名称
     * @return JobInfoDTO
     */
    public JobInfoDTO findJob(String jobName) {
        log.info("查询定时任务,jobName:{}", jobName);
        Long jobId = getJobId(jobName);
        if (jobId == null) {
            return null;
        }
        ResultDTO<JobInfoDTO> jobInfoDtoResultDto = wisecoJobClient.fetchJob(jobId);
        log.info("获取定时任务返回:{}", JSON.toJSONString(jobInfoDtoResultDto));
        if (!jobInfoDtoResultDto.isSuccess()) {
            log.info("获取定时任务失败[" + jobName + "]");
            return null;
        }
        return jobInfoDtoResultDto.getData();
    }

    /**
     * cron表达式生成
     * @param dataTask 任务都西昂
     * @return PageJobExecuteConfig
     */
    public static PageJobExecuteConfig formCron(TaskInfoDto dataTask) {
        String startTime = dataTask.getStartTime();
        String[] time = startTime.split(":");
        PageJobExecuteConfig build = PageJobExecuteConfig.builder()
                .executeFrequency(dataTask.getExecutionFrequency())
                .hourNum(Integer.parseInt(time[0]))
                .minuteNum(Integer.parseInt(time[1]))
                .build();
        switch (dataTask.getExecutionFrequency()) {
            case EVERY_DAY:
                break;
            case EVERY_MONTH:
                build.setDayInMonth(Integer.parseInt(dataTask.getDayInMonth()));
                break;
            case TARGET:
                List<String> months = dataTask.getTargetMonths();
                List<String> days = dataTask.getTargetDays();
                build.setTargetMonths(months.stream().mapToInt(Integer::parseInt).toArray());
                build.setTargetDays(days.stream().mapToInt(Integer::parseInt).toArray());
                break;
            default:
        }
        return build;
    }

    private Long getJobId(String jobName) {
        Object value = null;
        value = cacheClient.get(getJobCacheKey(jobName));
        if (value == null) {
            return null;
        }
        JSONObject jobValue = JSON.parseObject(String.valueOf(value));
        return Long.parseLong(String.valueOf(jobValue.get(JOB_ID_NAME)));
    }

    /**
     * 启用某个任务
     * @param jobName 任务名
     */
    public void enableJob(String jobName) {
        log.info("定时任务启用,jobName:{}", jobName);
        Long jobId = getJobId(jobName);
        if (jobId == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "定时任务不存在，无法启用[" + jobName + "]");
        }
        ResultDTO<Void> resultDTO = wisecoJobClient.enableJob(jobId);
        log.info("启用定时任务返回:{}", JSON.toJSONString(resultDTO));
        if (!resultDTO.isSuccess()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "启用定时任务失败[" + jobName + "]");
        }
    }

    /**
     * 删除某个任务（任务逻辑删除）
     * @param jobName 任务名
     */
    public void deleteJob(String jobName) {
        Long jobId = getJobId(jobName);
        log.info("定时任务删除,jobName:{},jobId:{}", jobName, jobId);
        if (jobId != null) {
            ResultDTO<Void> resultDTO = wisecoJobClient.deleteJob(jobId);
            log.info("删除定时任务返回:{}", JSON.toJSONString(resultDTO));
            if (!resultDTO.isSuccess()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "删除定时任务失败[" + jobName + "]");
            }
        }
    }

    /**
     * 校验cron表达式
     *
     * @param cron cron
     * @return 最近执行时间
     */
    public List<String> getNextExecuteTimeByCron(String cron) {
        return wisecoJobClient.checkTimeExpression(MagicNumbers.TWO, cron).getData();
    }

    private SaveJobInfoRequest buildBasicRequest(String jobName, Class<? extends BasicProcessor> jobProcessor, String cron, String jobDescription,
                                                 BaseWisecoJobParam jobParam, Long startTime, Long endTime) {
        SaveJobInfoRequest request = new SaveJobInfoRequest();
        request.setJobName(jobName);
        request.setJobDescription(jobDescription);
        if (jobParam != null) {
            request.setJobParams(JSON.toJSONString(jobParam));
        }
        request.setTimeExpressionType(TimeExpressionType.CRON);
        request.setTimeExpression(cron);
        request.setExecuteType(ExecuteType.STANDALONE);
        request.setProcessorType(ProcessorType.BUILT_IN);
        request.setProcessorInfo(jobProcessor.getName());
        request.setDispatchStrategy(DispatchStrategy.HEALTH_FIRST);
        LifeCycle lifeCycle = new LifeCycle();
        lifeCycle.setStart(startTime);
        lifeCycle.setEnd(endTime);
        if (null != endTime && endTime <= System.currentTimeMillis()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "任务设置已过期");
        }
        request.setLifeCycle(lifeCycle);
        return request;
    }
    private String getJobCacheKey(String jobName) {
        return CacheKeyPrefixConstant.CACHE_KEY_PRI_FEX + jobName;
    }


    /**
     * getCronStr
     *
     * @param timeUnit 时间单位
     * @param monitorFrequencyTime 时间
     * @return java.lang.String
     */
    public String getCronStr(MonitoringConfTimeUnitEnum timeUnit, Integer monitorFrequencyTime) {
        String cronStr = "* * 0/1 * * ? ";
        switch (timeUnit) {
            case MINUTE:
                cronStr = "0 */" + monitorFrequencyTime + " * * * ?";
                break;
            case HOUR:
                cronStr = "0 0 */" + monitorFrequencyTime + " * * ?";
                break;
            case DAY:
                cronStr = "0 0 0 */" + monitorFrequencyTime + " * ?";
                break;
            case WEEK:
                cronStr = "0 0 0 */" + (monitorFrequencyTime * MagicNumbers.SEVEN) + " * ?";
                break;
            case MONTH:
                cronStr = "0 0 0 1 */" + monitorFrequencyTime + " * *";
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "不支持的时间格式");
        }
        return cronStr;
    }

    /**
     * 获取cron
     *
     * @param taskInfo 任务信息
     * @return java.lang.String
     */
    public String getCronStr(BacktrackingSaveInputVO.TaskInfo taskInfo) {
        String cron;
        String[] executeTimeArray;
        // cron表达式生成
        switch (taskInfo.getExecutionFrequency()) {
            case CRON:
                cron = taskInfo.getCron();
                break;
            case EVERY_DAY:
                executeTimeArray = taskInfo.getExecuteTime().split(":");
                cron = executeTimeArray[MagicNumbers.TWO] + " " + executeTimeArray[MagicNumbers.ONE] + " " + executeTimeArray[MagicNumbers.ZERO] + " * * ?";
                break;
            case EVERY_MONTH:
                executeTimeArray = taskInfo.getExecuteTime().split(":");
                cron = executeTimeArray[MagicNumbers.TWO] + " " + executeTimeArray[MagicNumbers.ONE] + " " + executeTimeArray[MagicNumbers.ZERO] + " " + taskInfo.getDayInMonth() + " * ?";
                break;
            case TARGET:
                executeTimeArray = taskInfo.getExecuteTime().split(":");
                String[] executeTimeDataArray = taskInfo.getExecuteData().split("-");
                cron = executeTimeArray[MagicNumbers.TWO] + " " + executeTimeArray[MagicNumbers.ONE] + " " + executeTimeArray[MagicNumbers.ZERO] + " "
                        + executeTimeDataArray[MagicNumbers.TWO] + " " + executeTimeDataArray[MagicNumbers.ONE] + " ? " + executeTimeDataArray[MagicNumbers.ZERO];
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_SAVE_PARAM_FAIL, "批量回溯任务信息参数错误");
        }
        return cron;
    }
}
