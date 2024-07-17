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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.CaseFormat;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.DmAdapter;
import com.wiseco.var.process.app.server.commons.util.WisecoJobOperateUtil;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingOutputFile;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingQueryInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingUpdateStatusInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingListOutputVO;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowActionTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.OutputType;
import com.wiseco.var.process.app.server.enums.SysDynamicBusinessBucketEnum;
import com.wiseco.var.process.app.server.enums.VarProcessManifestStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingLifecycleMapper;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingMapper;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingTaskMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingLifecycle;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.service.SysDynamicServiceBiz;
import com.wiseco.var.process.app.server.service.VarProcessParamService;
import com.wiseco.var.process.app.server.service.VariablePublishBiz;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.BacktrackingDetailDto;
import com.wiseco.var.process.app.server.service.dto.BacktrackingQueryDto;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskDto;
import com.wiseco.var.process.app.server.service.dto.input.VariableDynamicSaveInputDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author wuweikang
 */
@RefreshScope
@Service
@Slf4j
public class BacktrackingListBiz {

    private static final String JOB_NAME = "backtracking_job_";
    @Resource
    VarProcessBatchBacktrackingLifecycleMapper varProcessBatchBacktrackingLifecycleMapper;
    @Resource
    VarProcessManifestService varProcessManifestService;
    @Resource
    private BacktrackingService backtrackingService;
    @Resource
    private BacktrackingTaskService backtrackingTaskService;
    @Resource
    private VarProcessBatchBacktrackingTaskMapper varProcessBatchBacktrackingTaskMapper;
    @Resource
    private SysDynamicServiceBiz sysDynamicServiceBiz;
    @Resource
    private VarProcessParamService varProcessParamService;
    @Resource
    private WisecoJobOperateUtil jobOperateUtil;
    @Resource
    private VarProcessBatchBacktrackingMapper backtrackingMapper;
    @Resource
    private BacktrackingBiz backtrackingBiz;
    @Resource
    private UserService userService;
    @Resource
    private AuthService authService;

    @Autowired
    private VariablePublishBiz variablePublishBiz;

    @Autowired
    private DmAdapter dmAdapter;


    public static final String NULL = "null";

    /**
     * list
     *
     * @param inputVO 入参
     * @return com.baomidou.mybatisplus.core.metadata.IPage
     */
    public IPage<BacktrackingListOutputVO> list(BacktrackingQueryInputVO inputVO) {
        Page<BacktrackingListOutputVO> page = new Page<>(inputVO.getCurrentNo(), inputVO.getSize());
        BacktrackingQueryDto backtrackingQueryDto = new BacktrackingQueryDto();
        BeanUtils.copyProperties(inputVO, backtrackingQueryDto);
        if (inputVO.getTaskStatus() != null) {
            backtrackingQueryDto.setTaskStatus(inputVO.getTaskStatus().name());
        }
        //构造排序条件
        String order = inputVO.getOrder();
        if (StringUtils.isEmpty(order)) {
            backtrackingQueryDto.setSortKey("updated_time");
            backtrackingQueryDto.setSortType("DESC");
        } else {
            String sortType = order.substring(order.indexOf("_") + 1);
            String sortKey = order.substring(0, order.indexOf("_"));
            sortKey = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sortKey);
            backtrackingQueryDto.setSortKey(sortKey);
            backtrackingQueryDto.setSortType(sortType);
        }
        //构造权限过滤条件
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        backtrackingQueryDto.setDeptCodes(roleDataAuthority.getDeptCodes());
        backtrackingQueryDto.setUserNames(roleDataAuthority.getUserNames());
        //查询批量回溯数据
        IPage<BacktrackingDetailDto> pageList = backtrackingService.findBacktrackingList(page, backtrackingQueryDto);
        if (CollectionUtils.isEmpty(pageList.getRecords())) {
            return page;
        }
        //获取用户名映射
        Map<String, String> userNameMap = getUserNameMap(pageList);
        List<BacktrackingListOutputVO> list = new ArrayList<>();
        for (BacktrackingDetailDto backtracking : pageList.getRecords()) {
            BacktrackingListOutputVO outputVO = new BacktrackingListOutputVO();
            BeanUtils.copyProperties(backtracking, outputVO);
            outputVO.setTriggerType(backtracking.getTriggerType());
            outputVO.setManifestId(backtracking.getManifestId());
            BacktrackingTaskDto task = varProcessBatchBacktrackingTaskMapper.getBacktrackingSingleTask(backtracking.getId());
            if (backtracking.getTaskStatus() != null) {
                outputVO.setTaskStatus(task.getStatus());
                outputVO.setTaskId(task.getId());
            } else {
                outputVO.setTaskStatus(BacktrackingTaskStatusEnum.NOT_EXECUTED);
            }
            if (backtracking.getStartTime() != null) {
                outputVO.setStartTime(task.getStartTime());
                backtracking.setStartTime(task.getStartTime());
            } else {
                outputVO.setStartTime(null);
            }
            //用户名
            outputVO.setCreatedUser(userNameMap.get(backtracking.getCreatedUser()));
            outputVO.setUpdatedUser(userNameMap.get(backtracking.getUpdatedUser()));
            // 审核拒绝后的描述
            if (outputVO.getStatus().equals(FlowStatusEnum.REFUSE)) {
                //查询当前状态
                VarProcessBatchBacktrackingLifecycle lifecycle = varProcessBatchBacktrackingLifecycleMapper.findLastHistory(backtracking.getId());
                if (lifecycle != null && lifecycle.getStatus().equals(FlowStatusEnum.REFUSE)) {
                    JSONObject desc = new JSONObject();
                    String fullName = userService.getFullNameByUserName(lifecycle.getCreatedUser());
                    if (!StringUtils.isEmpty(fullName)) {
                        desc.put("审核人", fullName);
                    }
                    desc.put("审核时间", lifecycle.getCreatedTime());
                    desc.put("拒绝原因", lifecycle.getDescription());
                    outputVO.setStatusDescription(desc.toJSONString());
                }
            }
            //失败原因
            if (backtracking.getTaskStatus() != null && backtracking.getTaskStatus() == BacktrackingTaskStatusEnum.FAIL) {
                outputVO.setErrorMessage(varProcessBatchBacktrackingTaskMapper.getErrorMessage(backtracking.getId()));
            } else {
                outputVO.setErrorMessage(null);
            }
            list.add(outputVO);
        }
        page.setTotal(pageList.getTotal());
        page.setPages(pageList.getPages());
        page.setRecords(list);
        return page;
    }

    private Map<String, String> getUserNameMap(IPage<BacktrackingDetailDto> pageList) {
        Set<String> userName = new HashSet<>();
        pageList.getRecords().forEach(item -> {
            userName.add(item.getCreatedUser());
            userName.add(item.getUpdatedUser());
        });
        return userService.findFullNameMapByUserNames(new ArrayList<>(userName));
    }

    /**
     * updateStatus
     *
     * @param inputDto 输入实体类对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(BacktrackingUpdateStatusInputDto inputDto) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getById(inputDto.getId());
        switch (inputDto.getActionType()) {
            case SUBMIT:
                checkManifestVersion(inputDto);
                checkApprovedSystem(inputDto, backtracking);
                checkIsNull(backtracking);
                break;
            case UP:
                checkManifestVersion(inputDto);
                break;
            case DELETE:
                backtracking.setDeleteFlag(DeleteFlagEnum.DELETED.getCode());
                break;
            case DOWN:
            case APPROVED:
            case REFUSE:
            case RETURN_EDIT:
            default:
        }

        checkApprovedSystem(inputDto, backtracking);
        backtracking.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        backtracking.setUpdatedTime(new Date());

        //启用则发布批量回溯 由于批量回溯停用时清单可改变，所以不需要判断发布状态
        if (backtracking.getStatus() == FlowStatusEnum.UP) {
            boolean isSuccess = variablePublishBiz.publishVariable(backtracking.getManifestId().toString(), MagicNumbers.TWO);
            if (!isSuccess) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_PUBLISH_FAIL,"批量回溯启用失败，变量清单" + backtracking.getManifestId() + "未成功发布");
            }
        }

        backtrackingService.updateById(backtracking);

        //保存动态
        saveDynamic(inputDto.getSpaceId(), backtracking.getId(), inputDto.getActionType().getDesc(), "");

        //生命周期
        varProcessBatchBacktrackingLifecycleMapper.insert(VarProcessBatchBacktrackingLifecycle.builder().backtrackingId(inputDto.getId())
                .actionType(inputDto.getActionType()).status(inputDto.getActionType().getNextStatus()).description(inputDto.getDescription())
                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build());

        //保存定时任务
        savePowerJob(backtracking);
    }


    /**
     * 修改状态校验
     *
     * @param inputDto 修改批量回溯状态 DTO
     * @return String 是否可以修改
     */
    @Transactional(rollbackFor = Exception.class)
    public String updateStatusCheck(BacktrackingUpdateStatusInputDto inputDto) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getById(inputDto.getId());
        String message;
        switch (inputDto.getActionType()) {
            case SUBMIT:
                if (!checkCronIsExpire(backtracking)) {
                    message = "批量任务执行时间已过期，无法执行，确认提交？";
                } else {
                    message = "确定要提交吗？";
                }
                break;
            case UP:
                checkManifestVersion(inputDto);
                if (!checkCronIsExpire(backtracking)) {
                    message = " 批量任务执行时间已过期，无法执行，确认启用？";
                } else {
                    message = "确认启用？";
                }
                break;
            case DELETE:
                message = deleteCheck(inputDto);
                break;
            case DOWN:
                message = stopCheck(inputDto.getId());
                break;
            case APPROVED:
                if (!checkCronIsExpire(backtracking)) {
                    message = " 批量任务执行时间已过期，无法执行，确认审核通过？";
                } else {
                    message = null;
                }
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "未知操作");
        }
        return message;
    }

    /**
     * 验证是否过期
     *
     * @param backtracking 批量回溯
     * @return true:没过期 false:过期了
     */
    private boolean checkCronIsExpire(VarProcessBatchBacktracking backtracking) {
        boolean isExpire = true;
        String taskInfoJson = backtracking.getTaskInfo();
        if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.SCHEDULED && !StringUtils.isEmpty(taskInfoJson) && !NULL.equals(taskInfoJson)) {
            ///获取批量回溯并解析cron
            BacktrackingSaveInputVO.TaskInfo taskInfoDto = JSON.parseObject(backtracking.getTaskInfo(), BacktrackingSaveInputVO.TaskInfo.class);
            String cron = jobOperateUtil.getCronStr(taskInfoDto);
            isExpire = backtrackingBiz.isExpire(cron);
        }
        return isExpire;
    }

    /**
     * 删除校验
     *
     * @param inputDto 输入实体类对象
     * @return 删除校验的结果
     */
    @Transactional(rollbackFor = Exception.class)
    public String deleteCheck(BacktrackingUpdateStatusInputDto inputDto) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getStatus)
                .eq(VarProcessBatchBacktracking::getId, inputDto.getId()));
        String message;
        if (backtracking.getStatus() == FlowStatusEnum.EDIT) {
            message = "确认删除？";
        } else if (backtracking.getStatus() == FlowStatusEnum.DOWN) {
            List<BacktrackingTaskDto> backtrackingTaskDtoList = backtrackingTaskService.getBacktrackingTaskByBacktrackingId(inputDto.getId());
            if (!CollectionUtils.isEmpty(backtrackingTaskDtoList)) {
                message = "该任务已经产生执行数据，确认删除？";
            } else {
                message = "确认删除？";
            }
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_STATUS_NO_MATCH, "非编辑中、停用状态不能删除");
        }

        return message;
    }

    /**
     * 批量回溯停用操作校验
     *
     * @param id 批量回溯ID
     * @return 是否可以执行
     */
    @Transactional(rollbackFor = Exception.class)
    public String stopCheck(Long id) {
        if (determineInProgress(id)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_STATUS_NO_MATCH, "处理中的任务不允许停用");
        } else {
            return "确认停用？";
        }
    }

    /**
     * 批量回溯执行操作校验
     *
     * @param id 批量回溯ID
     * @return 是否可以执行
     */
    @Transactional(rollbackFor = Exception.class)
    public String backtrackingExecuteCheck(Long id) {
        BacktrackingTaskDto task = varProcessBatchBacktrackingTaskMapper.getBacktrackingSingleTask(id);
        if (task == null) {
            return "确认执行？";
        } else {
            if (determineInProgress(id)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_STATUS_NO_MATCH, "最后执行状态处理中的任务不能执行");
            } else {
                return "确认执行？";
            }
        }
    }

    /**
     * 验证批量回溯最后执行状态是否为处理中
     *
     * @param id 批量回溯ID
     * @return 是否在处理中
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean determineInProgress(Long id) {
        BacktrackingTaskDto task = varProcessBatchBacktrackingTaskMapper.getBacktrackingSingleTask(id);
        if (task == null) {
            return false;
        } else {
            return task.getStatus() == BacktrackingTaskStatusEnum.IN_PROGRESS;
        }

    }

    private void savePowerJob(VarProcessBatchBacktracking backtracking) {
        if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.SCHEDULED) {
            switch (backtracking.getStatus()) {
                case UP:
                    backtrackingBiz.saveJobTask(backtracking);
                    break;
                case DOWN:
                case DELETE:
                    String jobName = JOB_NAME + backtracking.getId();
                    jobOperateUtil.deleteJob(jobName);
                    break;
                default:
                    break;
            }

        }
    }

    private void checkIsNull(VarProcessBatchBacktracking backtracking) {
        if (OutputType.valueOf(backtracking.getOutputType()) == OutputType.FILE) {
            BacktrackingOutputFile outputFile = JSON.parseObject(backtracking.getOutputInfo(), BacktrackingOutputFile.class);
            if (outputFile == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "结果输出信息不能为空");
            }
        } else if (OutputType.valueOf(backtracking.getOutputType()) == OutputType.DB) {
            BacktrackingSaveInputVO.BacktrackingOutputDb outputDb = new BacktrackingSaveInputVO.BacktrackingOutputDb();
            outputDb.setTableName(backtracking.getResultTable());
            if (outputDb.getTableName() == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "结果输出信息不能为空");
            }
        }

        if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.SCHEDULED) {
            if (backtracking.getTaskInfo() == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "任务信息不可为空");
            }
        }

    }

    private void saveDynamic(Long spaceId, Long backtrackingId, String operateType, String parentName) {

        //记录系统动态: 在[..]下 动作  + 类型 + ： + 名称
        VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getName)
                .eq(VarProcessBatchBacktracking::getId, backtrackingId));
        String businessDesc = backtracking.getName();
        if (!StringUtils.isEmpty(parentName)) {
            businessDesc += " 复制于" + parentName;
        }

        VariableDynamicSaveInputDto dynamicSaveInputDto = VariableDynamicSaveInputDto.builder().varSpaceId(spaceId).operateType(operateType)
                .typeEnum(SysDynamicBusinessBucketEnum.BATCH_BACKTRACKING).businessId(backtrackingId).businessDesc(businessDesc).build();
        sysDynamicServiceBiz.saveDynamicVariable(dynamicSaveInputDto);
    }

    private void checkManifestVersion(BacktrackingUpdateStatusInputDto inputDto) {
        BacktrackingDetailDto backtracking = backtrackingMapper.findBacktrackingById(inputDto.getId());
        VarProcessManifest manifest = varProcessManifestService.getOne(Wrappers.<VarProcessManifest>lambdaQuery()
                .eq(VarProcessManifest::getId, backtracking.getManifestId())
                .select(VarProcessManifest::getDeleteFlag, VarProcessManifest::getState));
        if (Objects.isNull(manifest) || manifest.getDeleteFlag().equals(0)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_NOT_FOUND, "引用的变量清单【" + backtracking.getManifestName() + "】不存在");
        } else {
            if (manifest.getState().equals(VarProcessManifestStateEnum.DOWN)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "引用的变量清单【" + backtracking.getManifestName() + "】未启用");
            }
        }
    }

    /**
     * checkApprovedSystem
     *
     * @param inputDto     inputDto
     * @param backtracking backtracking
     */
    private void checkApprovedSystem(BacktrackingUpdateStatusInputDto inputDto, VarProcessBatchBacktracking backtracking) {
        if (inputDto.getActionType().equals(FlowActionTypeEnum.SUBMIT)) {
            Boolean approveSystem = varProcessParamService.getParamStatus("batch_backtrack_task_review");
            if (approveSystem) {
                backtracking.setStatus(inputDto.getActionType().getNextStatus());
            } else {
                backtracking.setStatus(FlowActionTypeEnum.UP.getNextStatus());
            }
        } else {
            backtracking.setStatus(inputDto.getActionType().getNextStatus());
        }

    }
}
