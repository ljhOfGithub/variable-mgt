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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeEntityUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.DataModelKeywordEnum;
import com.google.common.collect.Lists;
import com.wiseco.auth.common.UserDTO;
import com.wiseco.boot.commons.io.SftpClient;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.security.SessionUser;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.WisecoJobOperateUtil;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingCopyInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingFilePreviewInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingOutputFile;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingPreviewInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingViewInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.MultipartPreviewRespVO;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestDetailOutputVo;
import com.wiseco.var.process.app.server.enums.BacktrackingFileImportTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingFileSpiltCharEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.LocalDataTypeEnum;
import com.wiseco.var.process.app.server.enums.OutputType;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.job.backtracking.BacktrackingProcessor;
import com.wiseco.var.process.app.server.job.param.BacktrackingTaskParam;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingDataModelMapper;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingLifecycleMapper;
import com.wiseco.var.process.app.server.repository.VarProcessDataModelMapper;
import com.wiseco.var.process.app.server.repository.entity.SysOss;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingLifecycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.service.SysOssService;
import com.wiseco.var.process.app.server.service.VarProcessServiceManifestService;
import com.wiseco.var.process.app.server.service.common.OssFileService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.datamodel.DataModelViewBiz;
import com.wiseco.var.process.app.server.service.dto.PanelDto;
import com.wiseco.var.process.app.server.service.dto.TabDto;
import com.wiseco.var.process.app.server.service.dto.TableContent;
import com.wiseco.var.process.app.server.service.dto.VariableManifestDataModelMappingVo;
import com.wiseco.var.process.app.server.service.impl.SftpClientService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VariableManifestBiz;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import tech.powerjob.common.response.JobInfoDTO;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wiseco.decision.osg.sdk.OsgUtil.getClient;

/**
 * @author wuweikang
 */
@RefreshScope
@Service
@Slf4j
public class BacktrackingBiz {
    public static final int INT_500 = 500;
    public static final String STRING_0 = "0";
    public static final String NULL = "null";
    private static final String JOB_NAME = "backtracking_job_";
    @Resource
    private BacktrackingService backtrackingService;
    @Resource
    private BacktrackingDataModelService backtrackingDataModelService;
    @Resource
    private VarProcessBatchBacktrackingDataModelMapper varProcessBatchBacktrackingDataModelMapper;
    @Resource
    private VariableManifestBiz variableManifestBiz;
    @Resource
    private VarProcessDataModelMapper varProcessDataModelMapper;
    @Resource
    private DataModelViewBiz dataModelViewBiz;
    @Resource
    private VarProcessManifestService varProcessManifestService;

    @Autowired
    private VarProcessServiceManifestService varProcessServiceManifestService;
    @Resource
    private WisecoJobOperateUtil jobOperateUtil;
    @Resource
    private VarProcessBatchBacktrackingLifecycleMapper varProcessBatchBacktrackingLifecycleMapper;

    @Autowired
    private SysOssService sysOssService;

    @Autowired
    private OssFileService ossFileService;

    @Autowired
    private SftpClientService sftpClientService;

    @Autowired
    private UserService userService;

    @Value("${wiseco.boot.oss.endpoint}")
    private String ossServer;

    @Value("${wiseco.boot.oss.bucket}")
    private String ossBucket;

    @Value("${wiseco.boot.oss.secretKey}")
    private String ossSecretKey;

    @Value("${wiseco.boot.oss.appKey}")
    private String ossAccessKey;

    /**
     * 保存
     *
     * @param inputVO 输入实体类对象
     * @return 新的一行记录的Id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long save(BacktrackingSaveInputVO inputVO) {
        long count = backtrackingService.count(new LambdaQueryWrapper<VarProcessBatchBacktracking>()
                .ne(inputVO.getId() != null, VarProcessBatchBacktracking::getId, inputVO.getId())
                .eq(VarProcessBatchBacktracking::getName, inputVO.getName())
                .eq(VarProcessBatchBacktracking::getDeleteFlag, 1));
        if (count > 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXISTS, "批量回溯名称已存在！");
        }
        //关键字校验
        DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputVO.getName().toLowerCase());
        if (dataModelKeywordEnum != null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "批量回溯名称不允许使用关键字" + inputVO.getName());
        }
        if (inputVO.getDescription() != null && inputVO.getDescription().length() > INT_500) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "描述长度不能超过500");
        }

        Long backtrackingId;
        if (inputVO.getId() == null) {
            // 新增
            backtrackingId = addBacktracking(inputVO);
            // 生命周期
            varProcessBatchBacktrackingLifecycleMapper.insert(VarProcessBatchBacktrackingLifecycle.builder().backtrackingId(backtrackingId)
                    .actionType(FlowActionTypeEnum.ADD).status(FlowStatusEnum.EDIT).description(null)
                    .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build());
        } else {
            // 修改
            backtrackingId = updateBacktracking(inputVO);
        }
        return backtrackingId;
    }

    /**
     * addBacktracking
     *
     * @param inputVO 输入实体类对象
     * @return 新的一行记录的Id
     */
    public Long addBacktracking(BacktrackingSaveInputVO inputVO) {
        VarProcessBatchBacktracking batchBacktracking = new VarProcessBatchBacktracking();
        batchBacktracking.setId(null);
        batchBacktracking.setName(inputVO.getName());
        batchBacktracking.setManifestId(inputVO.getManifestId());
        batchBacktracking.setTriggerType(inputVO.getTriggerType());
        batchBacktracking.setDescription(inputVO.getDescription());
        batchBacktracking.setSerialNo(inputVO.getSerialNo());
        batchBacktracking.setEnableTrace(inputVO.getEnableTrace());
        batchBacktracking.setStatus(FlowStatusEnum.EDIT);
        batchBacktracking.setDeleteFlag(1);
        batchBacktracking.setDataGetTypeInfo(JSONObject.toJSONString(inputVO.getDataGetTypeInfo()));
        batchBacktracking.setTaskInfo(JSONObject.toJSONString(inputVO.getTaskInfo()));
        batchBacktracking.setOutputType(inputVO.getOutputType().toString());
        if (inputVO.getOutputType() == OutputType.FILE) {
            batchBacktracking.setOutputInfo(JSONObject.toJSONString(inputVO.getOutputInfoFile()));
        }
        if (inputVO.getTriggerType() == BatchBacktrackingTriggerTypeEnum.MANUAL) {
            String tableName = "var_process_manifest_backtracking_manual_" + inputVO.getManifestId();
            batchBacktracking.setResultTable(tableName);
        } else {
            String tableName = "var_process_manifest_backtracking_scheduled_" + inputVO.getManifestId();
            batchBacktracking.setResultTable(tableName);
        }

        List<ServiceManifestDetailOutputVo> serviceManifestDetailOutputVoList =
                varProcessServiceManifestService.getManifestDetail(Lists.newArrayList(inputVO.getManifestId()));
        batchBacktracking.setVariableSize(serviceManifestDetailOutputVoList.get(0).getCountVariable());

        SessionUser sessionUser = SessionContext.getSessionUser();
        batchBacktracking.setCreatedUser(sessionUser.getUsername());
        batchBacktracking.setUpdatedUser(sessionUser.getUsername());
        final UserDTO user = sessionUser.getUser();
        if (user != null && user.getDepartment() != null) {
            batchBacktracking.setDeptCode(user.getDepartment().getCode());
            batchBacktracking.setDeptName(user.getDepartment().getName());
        }

        backtrackingService.save(batchBacktracking);

        for (BacktrackingSaveInputVO.DataModelInfo dataModelInfo : inputVO.getDataModelInfoList()) {
            VarProcessBatchBacktrackingDataModel batchBacktrackingDataModel = new VarProcessBatchBacktrackingDataModel();
            batchBacktrackingDataModel.setVarProcessSpaceId(CommonConstant.DEFAULT_SPACE_ID);
            batchBacktrackingDataModel.setBacktrackingId(batchBacktracking.getId());
            batchBacktrackingDataModel.setObjectName(dataModelInfo.getName());
            batchBacktrackingDataModel.setNameCn(dataModelInfo.getNameCn());
            batchBacktrackingDataModel.setObjectVersion(dataModelInfo.getVersionId());
            batchBacktrackingDataModel.setSourceType(dataModelInfo.getSourceType());
            batchBacktrackingDataModel.setOutsideServiceStrategy(dataModelInfo.getOutsideServiceStrategy());
            batchBacktrackingDataModel.setId(null);
            backtrackingDataModelService.save(batchBacktrackingDataModel);
        }
        return batchBacktracking.getId();
    }

    /**
     * updateVarProcessBatchBacktrackingDataModel
     *
     * @param inputVO           输入实体类对象
     * @param batchBacktracking 批量回溯对象
     */
    public void updateVarProcessBatchBacktrackingDataModel(BacktrackingSaveInputVO inputVO, VarProcessBatchBacktracking batchBacktracking) {
        //对于批量回溯-数据模型映射表中的数据，我们应该先判断是不是应该删除，应该删除哪些，删除后，再考虑是新建还是更新
        //以下删除
        //获取输入的BatchBacktrackingDataModelId,不为空添加到list中
        List<Long> inputBatchBacktrackingDataModelId = new ArrayList<>();
        for (BacktrackingSaveInputVO.DataModelInfo dataModelInfo : inputVO.getDataModelInfoList()) {
            if (dataModelInfo.getId() != null) {
                inputBatchBacktrackingDataModelId.add(dataModelInfo.getId());
            }
        }
        //如果输入的BatchBacktrackingDataModelId全部为null则表示变量清单发生了改变导致依赖的所有数据模型发生了改变，
        // 清除批量回溯-数据模型映射表该批量回溯id下的所有数据
        if (CollectionUtils.isEmpty(inputBatchBacktrackingDataModelId)) {
            QueryWrapper<VarProcessBatchBacktrackingDataModel> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("backtracking_id", inputVO.getId());
            varProcessBatchBacktrackingDataModelMapper.delete(queryWrapper);

            //判断数据库中的BatchBacktrackingDataModelId是否在输入的BatchBacktrackingDataModelId中不在的话删除数据库中的该条数据
        } else {
            //获取数据库中的BatchBacktrackingDataModelId
            QueryWrapper<VarProcessBatchBacktrackingDataModel> queryWrapper = new QueryWrapper<>();
            //构造查询条件，useid 等于传入的 userId
            queryWrapper.eq("backtracking_id", inputVO.getId());
            List<VarProcessBatchBacktrackingDataModel> varProcessBatchBacktrackingDataModelList = varProcessBatchBacktrackingDataModelMapper
                    .selectList(queryWrapper);
            List<Long> databaseBatchBacktrackingDataModelId = new ArrayList<>();
            for (VarProcessBatchBacktrackingDataModel dataModelInfo : varProcessBatchBacktrackingDataModelList) {
                databaseBatchBacktrackingDataModelId.add(dataModelInfo.getId());
            }
            //判断
            for (Long databaseBatchBacktrackingDataModelId1 : databaseBatchBacktrackingDataModelId) {
                if (!inputBatchBacktrackingDataModelId.contains(databaseBatchBacktrackingDataModelId1)) {
                    varProcessBatchBacktrackingDataModelMapper.deleteById(databaseBatchBacktrackingDataModelId1);
                }
            }
        }

        //以下更新或者新建
        for (BacktrackingSaveInputVO.DataModelInfo dataModelInfo : inputVO.getDataModelInfoList()) {

            if (dataModelInfo.getId() == null) {
                VarProcessBatchBacktrackingDataModel batchBacktrackingDataModel = new VarProcessBatchBacktrackingDataModel();
                batchBacktrackingDataModel.setVarProcessSpaceId(CommonConstant.DEFAULT_SPACE_ID);
                batchBacktrackingDataModel.setBacktrackingId(batchBacktracking.getId());
                batchBacktrackingDataModel.setObjectName(dataModelInfo.getName());
                batchBacktrackingDataModel.setNameCn(dataModelInfo.getNameCn());
                batchBacktrackingDataModel.setObjectVersion(dataModelInfo.getVersionId());
                batchBacktrackingDataModel.setSourceType(dataModelInfo.getSourceType());
                batchBacktrackingDataModel.setOutsideServiceStrategy(dataModelInfo.getOutsideServiceStrategy());
                batchBacktrackingDataModel.setId(null);
                backtrackingDataModelService.save(batchBacktrackingDataModel);

            } else {
                QueryWrapper<VarProcessBatchBacktrackingDataModel> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", dataModelInfo.getId());
                VarProcessBatchBacktrackingDataModel batchBacktrackingDataModel = varProcessBatchBacktrackingDataModelMapper.selectOne(queryWrapper);
                batchBacktrackingDataModel.setBacktrackingId(batchBacktracking.getId());
                batchBacktrackingDataModel.setObjectName(dataModelInfo.getName());
                batchBacktrackingDataModel.setNameCn(dataModelInfo.getNameCn());
                batchBacktrackingDataModel.setObjectVersion(dataModelInfo.getVersionId());
                batchBacktrackingDataModel.setSourceType(dataModelInfo.getSourceType());
                batchBacktrackingDataModel.setOutsideServiceStrategy(dataModelInfo.getOutsideServiceStrategy());
                batchBacktrackingDataModel.setUpdatedTime(new Date());
                backtrackingDataModelService.updateById(batchBacktrackingDataModel);
            }

        }
    }

    /**
     * updateBacktracking
     *
     * @param inputVO 输入实体类对象
     * @return 更新的那一行记录的id
     */
    public Long updateBacktracking(BacktrackingSaveInputVO inputVO) {
        VarProcessBatchBacktracking batchBacktracking = backtrackingService.getById(inputVO.getId());
        if (batchBacktracking == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXISTS, "批量回溯任务不存在");
        }
        if (inputVO.getStatus() == FlowStatusEnum.UNAPPROVED) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_STATUS_NO_MATCH, "该任务为待审核状态，无法修改");
        }

        if (inputVO.getStatus() != FlowStatusEnum.UP) {
            batchBacktracking.setManifestId(inputVO.getManifestId());
            batchBacktracking.setTriggerType(inputVO.getTriggerType());
        }
        batchBacktracking.setName(inputVO.getName());
        batchBacktracking.setDescription(inputVO.getDescription());
        batchBacktracking.setEnableTrace(inputVO.getEnableTrace());
        batchBacktracking.setSerialNo(inputVO.getSerialNo());
        batchBacktracking.setDataGetTypeInfo(JSONObject.toJSONString(inputVO.getDataGetTypeInfo()));
        batchBacktracking.setTaskInfo(JSONObject.toJSONString(inputVO.getTaskInfo()));
        batchBacktracking.setUpdatedUser(SessionContext.getSessionUser().getUsername());

        batchBacktracking.setOutputType(inputVO.getOutputType().toString());
        if (inputVO.getOutputType() == OutputType.FILE) {
            batchBacktracking.setOutputInfo(JSONObject.toJSONString(inputVO.getOutputInfoFile()));
        }

        if (inputVO.getTriggerType() == BatchBacktrackingTriggerTypeEnum.MANUAL) {
            String tableName = "var_process_manifest_backtracking_manual_" + inputVO.getManifestId();
            batchBacktracking.setResultTable(tableName);

        } else {
            String tableName = "var_process_manifest_backtracking_scheduled_" + inputVO.getManifestId();
            batchBacktracking.setResultTable(tableName);
        }
        List<ServiceManifestDetailOutputVo> serviceManifestDetailOutputVoList = varProcessServiceManifestService.getManifestDetail(Lists.newArrayList(inputVO.getManifestId()));
        batchBacktracking.setVariableSize(serviceManifestDetailOutputVoList.get(0).getCountVariable());

        batchBacktracking.setUpdatedTime(new Date());
        backtrackingService.updateById(batchBacktracking);

        //更新批量回溯-数据模型映射表中的数据
        try {
            updateVarProcessBatchBacktrackingDataModel(inputVO, batchBacktracking);
        } catch (Exception e) {
            log.error("更新批量回溯-数据模型映射表中的数据失败", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_DATABASE_ERROR, "更新批量回溯-数据模型映射表中的数据失败");
        }


        //如果启用状态的定时任务被编辑了，则更新定时器
        if (batchBacktracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.SCHEDULED
                && batchBacktracking.getStatus() == FlowStatusEnum.UP) {
            saveJobTask(batchBacktracking);
        }
        return batchBacktracking.getId();
    }

    /**
     * 保存定时任务
     *
     * @param batchBacktracking 批量回溯
     */
    public void saveJobTask(VarProcessBatchBacktracking batchBacktracking) {
        String taskInfoJson = batchBacktracking.getTaskInfo();
        Assert.notEmpty(Collections.singleton(taskInfoJson), "任务信息为空");
        BacktrackingSaveInputVO.TaskInfo taskInfo = JSON.parseObject(taskInfoJson, BacktrackingSaveInputVO.TaskInfo.class);
        String cron = jobOperateUtil.getCronStr(taskInfo);
        //任务过期了，删除原来的任务
        if (!isExpire(cron)) {
            String jobName = JOB_NAME + batchBacktracking.getId();
            jobOperateUtil.deleteJob(jobName);
            return;
        }
        BacktrackingTaskParam backtrackingJobParam = BacktrackingTaskParam.builder().backtrackingId(batchBacktracking.getId()).build();
        String jobName = JOB_NAME + batchBacktracking.getId();
        final JobInfoDTO job = jobOperateUtil.findJob(jobName);
        if (job == null) {
            jobOperateUtil.addBasicCronJob(jobName, BacktrackingProcessor.class, cron, jobName, backtrackingJobParam, null, null);
        } else {
            jobOperateUtil.updateBasicCronJob(jobName, BacktrackingProcessor.class, cron, jobName, backtrackingJobParam, null, null);
        }
    }

    /**
     * 获取最近执行时间
     *
     * @param cron cron
     * @return 最近执行时间
     */
    public List<String> getNextExecuteTimeByCron(String cron) {
        return jobOperateUtil.getNextExecuteTimeByCron(cron);
    }


    /**
     * 校验cron表达式是否过期
     *
     * @param taskInfo 任务信息
     * @return 是否过期
     */
    public Boolean checkCronIsExpire(BacktrackingSaveInputVO.TaskInfo taskInfo) {
        String cron = jobOperateUtil.getCronStr(taskInfo);
        return isExpire(cron);
    }

    /**
     * 是否过期
     *
     * @param cron 表达式
     * @return false : 过期了 true: 没过期
     */
    public boolean isExpire(String cron) {
        boolean isExpire = false;
        //校验是否过期
        List<String> result = jobOperateUtil.getNextExecuteTimeByCron(cron);
        if (!CollectionUtils.isEmpty(result)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                format.parse(result.get(0));
                isExpire = true;
            } catch (ParseException ignored) {
            }
        }
        return isExpire;
    }


    /**
     * 复制
     *
     * @param inputVO 输入实体类对象
     * @return 复制后的新的实体Id
     */
    public Long copy(BacktrackingCopyInputVO inputVO) {
        //判断复制来源是否存在，不存在的话，直接报错
        VarProcessBatchBacktracking batchBacktracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getId)
                .eq(VarProcessBatchBacktracking::getId, inputVO.getCopyId()));
        if (batchBacktracking == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_NOT_FOUND, "批量回溯任务不存在");
        }
        //判断复制任务的中文名是否已经存在，存在的话直接报错
        List<VarProcessBatchBacktracking> list = backtrackingService.list(
                new QueryWrapper<VarProcessBatchBacktracking>().lambda()
                        .select(VarProcessBatchBacktracking::getId)
                        .eq(VarProcessBatchBacktracking::getName, inputVO.getName())
                        .eq(VarProcessBatchBacktracking::getDeleteFlag, 1)
        );
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_EXISTS, "批量回溯名称已存在！");
        }
        //关键字校验
        DataModelKeywordEnum dataModelKeywordEnum = DataModelKeywordEnum.fromName(inputVO.getName().toLowerCase());
        if (dataModelKeywordEnum != null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "批量回溯名称不允许使用关键字" + inputVO.getName());
        }

        //复制要操作两张表  批量回溯表和批零回溯-数据模型关系映射表

        //批量回溯表的复制(任务描述”、“结果输出信息”和“任务信息”清空)
        //从数据库中获取相关信息，新建一个实例，然后将获取信息填入新建的实例中，然后保存
        VarProcessBatchBacktracking sourceBatchBacktracking = backtrackingService.getById(inputVO.getCopyId());
        VarProcessBatchBacktracking newBatchBacktracking = new VarProcessBatchBacktracking();

        BeanUtils.copyProperties(sourceBatchBacktracking, newBatchBacktracking);
        newBatchBacktracking.setId(null);
        newBatchBacktracking.setName(inputVO.getName());
        newBatchBacktracking.setDescription(null);
        newBatchBacktracking.setTaskInfo(null);
        newBatchBacktracking.setOutputInfo(null);
        newBatchBacktracking.setCreatedTime(null);
        newBatchBacktracking.setUpdatedTime(null);
        newBatchBacktracking.setStatus(FlowStatusEnum.EDIT);
        newBatchBacktracking.setCreatedUser(SessionContext.getSessionUser().getUsername());
        newBatchBacktracking.setUpdatedUser(SessionContext.getSessionUser().getUsername());

        final UserDTO user = SessionContext.getSessionUser().getUser();
        if (user != null && user.getDepartment() != null) {
            newBatchBacktracking.setDeptCode(user.getDepartment().getCode());
            newBatchBacktracking.setDeptName(user.getDepartment().getName());
        }
        backtrackingService.save(newBatchBacktracking);

        //批量回溯表和批零回溯-数据模型关系映射表 的复制
        //获取批量回溯相关的数据模型，新建实例，然后改变批量回溯id然后保存
        QueryWrapper<VarProcessBatchBacktrackingDataModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("backtracking_id", inputVO.getCopyId());
        List<VarProcessBatchBacktrackingDataModel> batchBacktrackingDataModelList =
                varProcessBatchBacktrackingDataModelMapper.selectList(queryWrapper);
        for (VarProcessBatchBacktrackingDataModel backtrackingDataModel : batchBacktrackingDataModelList) {
            VarProcessBatchBacktrackingDataModel batchBacktrackingDataModel = new VarProcessBatchBacktrackingDataModel();
            BeanUtils.copyProperties(backtrackingDataModel, batchBacktrackingDataModel);

            batchBacktrackingDataModel.setId(null);
            batchBacktrackingDataModel.setBacktrackingId(newBatchBacktracking.getId());
            backtrackingDataModelService.save(batchBacktrackingDataModel);
        }
        // 生命周期
        varProcessBatchBacktrackingLifecycleMapper.insert(VarProcessBatchBacktrackingLifecycle.builder().backtrackingId(newBatchBacktracking.getId())
                .actionType(FlowActionTypeEnum.ADD).status(FlowStatusEnum.EDIT).description(null)
                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build());
        return newBatchBacktracking.getId();
    }

    /**
     * view
     *
     * @param inputVO 输入实体类对象
     * @return com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO
     */
    public BacktrackingSaveInputVO view(BacktrackingViewInputVO inputVO) {

        VarProcessBatchBacktracking batchBacktracking = backtrackingService.getById(inputVO.getBacktrackingId());
        if (batchBacktracking == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_NOT_FOUND, "批量回溯任务不存在");
        }

        //批量回溯表的组装
        BacktrackingSaveInputVO backtrackingViewOuputVO = new BacktrackingSaveInputVO();
        backtrackingViewOuputVO.setId(batchBacktracking.getId());
        backtrackingViewOuputVO.setName(batchBacktracking.getName());
        backtrackingViewOuputVO.setManifestId(batchBacktracking.getManifestId());
        backtrackingViewOuputVO.setTriggerType(batchBacktracking.getTriggerType());
        backtrackingViewOuputVO.setDescription(batchBacktracking.getDescription());
        backtrackingViewOuputVO.setSerialNo(batchBacktracking.getSerialNo());
        backtrackingViewOuputVO.setEnableTrace(batchBacktracking.getEnableTrace());

        backtrackingViewOuputVO.setDataGetTypeInfo(JSON.parseObject(batchBacktracking.getDataGetTypeInfo(),
                BacktrackingSaveInputVO.DataGetTypeInfo.class));

        backtrackingViewOuputVO.setOutputType(OutputType.valueOf(batchBacktracking.getOutputType()));
        if (OutputType.valueOf(batchBacktracking.getOutputType()) == OutputType.FILE) {
            backtrackingViewOuputVO.setOutputInfoFile(JSON.parseObject(batchBacktracking.getOutputInfo(), BacktrackingOutputFile.class));

        } else if (OutputType.valueOf(batchBacktracking.getOutputType()) == OutputType.DB) {
            BacktrackingSaveInputVO.BacktrackingOutputDb outputDb = new BacktrackingSaveInputVO.BacktrackingOutputDb();
            outputDb.setTableName(batchBacktracking.getResultTable());
            outputDb.setTableNameDesc(batchBacktracking.getResultTableDesc());
            backtrackingViewOuputVO.setOutputInfoDb(outputDb);
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "不支持的输出类型");
        }

        backtrackingViewOuputVO.setTaskInfo(JSON.parseObject(batchBacktracking.getTaskInfo(), BacktrackingSaveInputVO.TaskInfo.class));
        backtrackingViewOuputVO.setStatus(batchBacktracking.getStatus());

        //批量回溯-数据模型映射表的组装
        QueryWrapper<VarProcessBatchBacktrackingDataModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("backtracking_id", inputVO.getBacktrackingId());
        List<VarProcessBatchBacktrackingDataModel> varProcessBatchBacktrackingDataModelList = varProcessBatchBacktrackingDataModelMapper
                .selectList(queryWrapper);

        List<BacktrackingSaveInputVO.DataModelInfo> backtrackingViewOuputVoDataModelInfoList = new ArrayList<>();
        for (VarProcessBatchBacktrackingDataModel dataModelInfo : varProcessBatchBacktrackingDataModelList) {
            BacktrackingSaveInputVO.DataModelInfo backtrackingViewOuputVoDataModelInfo = new BacktrackingSaveInputVO.DataModelInfo();
            backtrackingViewOuputVoDataModelInfo.setId(dataModelInfo.getId());
            backtrackingViewOuputVoDataModelInfo.setName(dataModelInfo.getObjectName());
            backtrackingViewOuputVoDataModelInfo.setNameCn(dataModelInfo.getNameCn());
            backtrackingViewOuputVoDataModelInfo.setSourceType(dataModelInfo.getSourceType());
            backtrackingViewOuputVoDataModelInfo.setVersionId(dataModelInfo.getObjectVersion());
            backtrackingViewOuputVoDataModelInfo.setOutsideServiceStrategy(dataModelInfo.getOutsideServiceStrategy());

            backtrackingViewOuputVoDataModelInfoList.add(backtrackingViewOuputVoDataModelInfo);

        }

        backtrackingViewOuputVO.setDataModelInfoList(backtrackingViewOuputVoDataModelInfoList);

        //返回组装数据
        return backtrackingViewOuputVO;

    }

    /**
     * previewData
     *
     * @param reqVO 输入实体类对象
     * @return 分片数据预览
     */
    public MultipartPreviewRespVO previewData(BacktrackingPreviewInputVO reqVO) {
        return backtrackingService.previewData(reqVO);
    }

    /**
     * getTableName
     *
     * @param manifestId  变量清单ID
     * @param triggerType 批量回溯触发类型枚举类
     * @return 结果输出信息（数据库）
     */
    public BacktrackingSaveInputVO.BacktrackingOutputDb getTableName(Long manifestId, BatchBacktrackingTriggerTypeEnum triggerType) {
        VarProcessManifest manifest = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                .eq(VarProcessManifest::getId, manifestId)
                .select(VarProcessManifest::getVarManifestName));
        String tableName = "var_process_manifest_backtracking_" + (triggerType == BatchBacktrackingTriggerTypeEnum.MANUAL ? "manual" : "scheduled")
                + "_" + manifestId;
        String tableNameDesc = "批量回溯结果表_" + (triggerType == BatchBacktrackingTriggerTypeEnum.MANUAL ? "手动" : "定时") + "_"
                + manifest.getVarManifestName();
        return BacktrackingSaveInputVO.BacktrackingOutputDb.builder().tableName(tableName).tableNameDesc(tableNameDesc).build();
    }

    /**
     * getDataModelTree
     *
     * @param manifestId 变量清单Id
     * @return java.util.List
     */
    public List<DomainDataModelTreeDto> getDataModelTree(Long manifestId) {
        List<VariableManifestDataModelMappingVo> dataModels = variableManifestBiz.getDataModels(manifestId);
        List<DomainDataModelTreeDto> treeList = new ArrayList<>();
        for (VariableManifestDataModelMappingVo dataModel : dataModels) {
            if (dataModel.getSourceType() == VarProcessDataModelSourceType.OUTSIDE_PARAM) {

                QueryWrapper<VarProcessDataModel> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("object_name", dataModel.getName());
                queryWrapper.eq("version", dataModel.getVersion());
                VarProcessDataModel data = varProcessDataModelMapper.selectOne(queryWrapper);
                //获取空间下使用的全部变量
                Set<String> varPathMap = dataModelViewBiz.getUseVarList(1L);
                DomainDataModelTreeDto inputData = DomainModelTreeEntityUtils.transferDataModelTreeDto(data.getContent(), varPathMap);
                //首先判断有没有子节点 没有子节点的话就判断父节点有没有扩展数据 如果没有直接删除这个树形结构
                //如果有子节点 判断子结点中有没有扩展数据
                DomainDataModelTreeDto domainDataModelTreeDto = getExtendDate(inputData);
                if (domainDataModelTreeDto != null) {
                    treeList.add(inputData);
                }

            }

        }

        return treeList;
    }

    /**
     * 获取扩展数据
     *
     * @param domainDataModelTreeDto 决策领域树形结构实体对象
     * @return com.decision.jsonschema.util.dto.DomainDataModelTreeDto
     */
    public DomainDataModelTreeDto getExtendDate(DomainDataModelTreeDto domainDataModelTreeDto) {
        List<DomainDataModelTreeDto> childrenList = domainDataModelTreeDto.getChildren();
        if (!CollectionUtils.isEmpty(childrenList)) {
            List<DomainDataModelTreeDto> newChildrenList = new ArrayList<>();
            for (DomainDataModelTreeDto domainDataModelTreeDto1 : childrenList) {
                if (STRING_0.equals(domainDataModelTreeDto1.getIsExtend())) {
                    newChildrenList.add(domainDataModelTreeDto1);
                }
                if (!CollectionUtils.isEmpty(domainDataModelTreeDto1.getChildren())) {
                    getExtendDate(domainDataModelTreeDto1);
                }
            }
            if (!CollectionUtils.isEmpty(newChildrenList)) {
                domainDataModelTreeDto.setChildren(newChildrenList);
                return domainDataModelTreeDto;
            } else {
                if (STRING_0.equals(domainDataModelTreeDto.getIsExtend())) {
                    return domainDataModelTreeDto;
                } else {
                    return null;
                }
            }
        } else {
            if (STRING_0.equals(domainDataModelTreeDto.getIsExtend())) {
                return domainDataModelTreeDto;
            } else {
                return null;
            }
        }
    }

    /**
     * 根据backtrackingId查看它的生命周期
     *
     * @param backtrackingId 实时服务ID
     * @return 返回的生命周期结构
     */
    public List<TabDto> getServiceProperties(Long backtrackingId) {
        // 1.定义返回体
        List<TabDto> result = new ArrayList<>();
        // 2.获取生命周期
        result.add(buildLifeCyclePanelInfo(backtrackingId));
        // 3.返回结果
        return result;
    }

    /**
     * 获取生命周期的面板信息
     *
     * @param id 批量回溯的Id
     * @return 实时服务的生命周期信息
     */
    private TabDto buildLifeCyclePanelInfo(Long id) {
        // 1.先获取这个实时服务对应的变动记录
        List<VarProcessBatchBacktrackingLifecycle> varProcessBatchBacktrackingCycles = varProcessBatchBacktrackingLifecycleMapper.selectList(Wrappers.<VarProcessBatchBacktrackingLifecycle>lambdaQuery()
                .eq(VarProcessBatchBacktrackingLifecycle::getBacktrackingId, id)
                .orderByDesc(VarProcessBatchBacktrackingLifecycle::getCreatedTime));
        // 2.生成表格头
        List<TableContent.TableHeadInfo> tableHead = new ArrayList<>();
        tableHead.add(TableContent.TableHeadInfo.builder().lable("状态").key("status").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作类型").key("operation").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作人").key("operaUserName").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("操作时间").key("operaTime").build());
        tableHead.add(TableContent.TableHeadInfo.builder().lable("备注").key("description").build());
        // 3.生成表格内容
        List<JSONObject> tableData = new ArrayList<>();
        if (!CollectionUtils.isEmpty(varProcessBatchBacktrackingCycles)) {
            varProcessBatchBacktrackingCycles.forEach(lifeCycle -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", lifeCycle.getStatus().getDesc());
                jsonObject.put("operation", lifeCycle.getActionType().getDesc());
                String fullName = userService.getFullNameByUserName(lifeCycle.getCreatedUser());
                jsonObject.put("operaUserName", fullName);
                jsonObject.put("operaTime", DateUtil.parseDateToStr(lifeCycle.getCreatedTime(), MagicStrings.DATE_TIME_FORMAT));
                jsonObject.put("description", lifeCycle.getDescription());
                tableData.add(jsonObject);
            });
        }
        // 4.组装列表内容
        TableContent tableContent = TableContent.builder()
                .tableHead(tableHead)
                .tableData(tableData)
                .build();
        List<PanelDto> panelDtoList = new ArrayList<>();
        panelDtoList.add(PanelDto.builder()
                .title("生命周期")
                .type(LocalDataTypeEnum.LIFECYCLE.getCode())
                .datas(tableContent)
                .build());
        return TabDto.builder().name("生命周期").content(panelDtoList).build();
    }

    /**
     * 文件预览
     *
     * @param backtrackingFilePreviewInputVO 文件预览入参
     * @return MultipartPreviewRespVO   文件预览出参
     */
    public MultipartPreviewRespVO filePreview(BacktrackingFilePreviewInputVO backtrackingFilePreviewInputVO) throws IOException {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        S3Client s3Client = null;
        SftpClient sftpClient = null;
        try {
            if (backtrackingFilePreviewInputVO.getDataFileType() == BacktrackingFileImportTypeEnum.LOCAL && backtrackingFilePreviewInputVO.getFile() != null) {
                //本地文件：添加时预览
                inputStream = backtrackingFilePreviewInputVO.getFile().getInputStream();
            } else if (backtrackingFilePreviewInputVO.getDataFileType() == BacktrackingFileImportTypeEnum.LOCAL && backtrackingFilePreviewInputVO.getLocalFileId() != null) {
                //本地文件：编辑时预览
                SysOss sysOss = sysOssService.getById(backtrackingFilePreviewInputVO.getLocalFileId());
                ossFileService.setOssConfig(ossServer, ossBucket, ossAccessKey, ossSecretKey);
                GetObjectRequest objectRequest = GetObjectRequest.builder().key(sysOss.getOssPath()).bucket("sys-fileview").build();
                s3Client = getClient();
                inputStream = s3Client.getObject(objectRequest);
            } else if (backtrackingFilePreviewInputVO.getFtpServerId() != null && !StringUtils.isEmpty(backtrackingFilePreviewInputVO.getDirectory()) && !StringUtils.isEmpty(backtrackingFilePreviewInputVO.getFileName())) {
                //ftp文件预览
                sftpClient = sftpClientService.login(backtrackingFilePreviewInputVO.getFtpServerId());
                inputStream = sftpClientService.downloadStream(backtrackingFilePreviewInputVO.getDirectory(), backtrackingFilePreviewInputVO.getFileName(), sftpClient);
            }

            Assert.notNull(inputStream, "预览失败");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, backtrackingFilePreviewInputVO.getCharsetType().getDesc()));

            //分隔符
            String spiltChar;
            if (backtrackingFilePreviewInputVO.getSplit() == BacktrackingFileSpiltCharEnum.OTHER) {
                spiltChar = backtrackingFilePreviewInputVO.getSplitKey();
            } else {
                spiltChar = backtrackingFilePreviewInputVO.getSplit().getCode();
            }

            //表头
            List<String> headers = new ArrayList<>();
            if (backtrackingFilePreviewInputVO.getIncludeHeader()) {
                headers = getHeaders(bufferedReader, spiltChar);
            }

            //数据
            List<Map<String, Object>> dataMapList = getDataMapList(bufferedReader, spiltChar, backtrackingFilePreviewInputVO.getStartLine(), MagicNumbers.INT_100, headers);

            MultipartPreviewRespVO multipartPreviewRespVO = new MultipartPreviewRespVO();
            multipartPreviewRespVO.setHeaderPreviewResult(new MultipartPreviewRespVO.HeaderPreviewResult(0, 0, 0, 0, headers, null, null));
            multipartPreviewRespVO.setDataPreviewResult(new MultipartPreviewRespVO.DataPreviewResult(0, 0, 0, 0, dataMapList));
            return multipartPreviewRespVO;
        } catch (IOException | URISyntaxException e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_NOT_FOUND, "预览失败");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (s3Client != null) {
                s3Client.close();
            }

            if (sftpClient != null) {
                sftpClientService.logout(sftpClient);
            }
        }
    }


    /**
     * 获取表头
     *
     * @param bufferedReader 文件输出流
     * @param spiltChar      分隔符
     * @return 表头
     */
    public List<String> getHeaders(BufferedReader bufferedReader, String spiltChar) throws IOException {
        List<String> headers = new ArrayList<>();
        String line = bufferedReader.readLine();
        if (line != null) {
            headers = Arrays.asList(line.split(spiltChar));
        }
        return headers;
    }

    /**
     * 获取数据映射
     *
     * @param bufferedReader 文件输入流
     * @param spiltChar      分隔符
     * @param startLine      起始行
     * @param readSize       读取条数
     * @param headers        表头
     * @return java.util.List
     */
    public List<Map<String, Object>> getDataMapList(BufferedReader bufferedReader, String spiltChar, int startLine, int readSize, List<String> headers) throws IOException {
        //数据
        List<Map<String, Object>> dataMapList = new ArrayList<>(readSize);
        String line;
        int currenNo = 0;
        while (++currenNo <= readSize && (line = bufferedReader.readLine()) != null) {
            if (currenNo < startLine) {
                continue;
            }
            Map<String, Object> dataMap = new LinkedHashMap<>();
            String[] data = line.split(spiltChar);
            //如果表头为空说明没有表头，则初始化表头；表头不为空说明有表头
            if (CollectionUtils.isEmpty(headers)) {
                for (int i = 1; i <= data.length; i++) {
                    headers.add("C" + i);
                    dataMap.put("C" + i, data[i - 1]);
                }
            } else {
                for (int i = 0; i < headers.size(); i++) {
                    //防止最后的数据为空导致数组越界
                    if (i < data.length) {
                        dataMap.put(headers.get(i), data[i]);
                    }
                }
            }
            dataMapList.add(dataMap);
        }
        return dataMapList;
    }

}
