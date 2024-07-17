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
package com.wiseco.var.process.app.server.service.monitoring;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.DisplayDimensionVo;
import com.wiseco.var.process.app.server.controller.vo.IndicatorMappingVo;
import com.wiseco.var.process.app.server.controller.vo.MonitorObjectMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ReportFormItemVo;
import com.wiseco.var.process.app.server.controller.vo.ReportFormPsiMappingVo;
import com.wiseco.var.process.app.server.controller.vo.output.MonitoringDiagramOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.ServiceManifestNameVo;
import com.wiseco.var.process.app.server.enums.MonitorIndicatorEnum;
import com.wiseco.var.process.app.server.enums.ReportFormCategoryEnum;
import com.wiseco.var.process.app.server.enums.ReportFormDisplayDimensionEnum;
import com.wiseco.var.process.app.server.enums.ReportFormPsiEnum;
import com.wiseco.var.process.app.server.enums.ReportFormTypeEnum;
import com.wiseco.var.process.app.server.enums.ReportFromStateEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessReportForm;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormCreateInputDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormDeleteInputDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormDuplicationInputDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormSearchInputDto;
import com.wiseco.var.process.app.server.service.dto.input.ReportFormStatusInputDto;
import com.wiseco.var.process.app.server.service.dto.output.ReportFormDetailOutputDto;
import com.wiseco.var.process.app.server.service.dto.output.ReportFormListOutputDto;
import com.wiseco.var.process.app.server.service.dto.output.ReportFormOutputDto;
import com.wiseco.var.process.app.server.service.dto.output.ReportFormsOutputDto;
import com.wiseco.var.process.app.server.service.monitoring.strategy.GenerateReportFormServiceFactory;
import com.wiseco.var.process.app.server.service.monitoring.strategy.GenerateReportFormStrategy;
import com.wisecotech.json.JSONObject;
import com.wisecotech.json.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 监控报表的复杂逻辑类
 */

@Service
@Slf4j
public class VarProcessReportFormServiceBiz {

    @Autowired
    private VarProcessReportFormService varProcessReportFormService;
    @Resource
    private UserService userService;
    @Autowired
    private GenerateReportFormServiceFactory generateReportFormServiceFactory;
    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;
    @Autowired
    private AuthService authService;

    /**
     * 添加监控报表
     *
     * @param inputDto 输入实体类对象
     * @return 新报表的ID
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long addReportForm(ReportFormCreateInputDto inputDto) {
        // 1.先校验
        this.checkReportFormPreview(inputDto, true);
        // 2.再填充实体类
        VarProcessReportForm reportForm = VarProcessReportForm.builder()
                .name(inputDto.getName()).category(inputDto.getCategoryEnum())
                .startTime(inputDto.getStartTime()).endTime(inputDto.getEndTime())
                .monitorObject(JSONObject.toJSONString(inputDto.getMonitorObjectMappingVo()))
                .manifests(JSONObject.toJSONString(inputDto.getManifests()))
                .monitorIndicator(JSONObject.toJSONString(inputDto.getIndicatorMappingVo(), SerializerFeature.WriteDateUseDateFormat))
                .displayDimension(JSONObject.toJSONString(inputDto.getDisplayDimensionVo()))
                .type(inputDto.getType()).reportFormOrder(inputDto.getOrder())
                .state(ReportFromStateEnum.EDIT).deleteFlag(DeleteFlagEnum.USABLE.getCode())
                .deptCode(SessionContext.getSessionUser().getUser().getDepartment().getCode())
                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build();
        // 3.插入数据
        varProcessReportFormService.save(reportForm);
        return reportForm.getId();
    }

    /**
     * 校验监控报表实体类
     * @param inputDto 输入实体类对象
     * @param isAdd 是否添加(true-添加, false-修改)
     */
    private void checkReportFormPreview(ReportFormCreateInputDto inputDto, Boolean isAdd) {
        // 1.报表名称不能重复
        if (isAdd) {
            // 1.1 如果时添加, 新报表的名称就不能重复
            this.checkName(inputDto.getName());
        } else {
            // 1.2 如果是修改, 则名字可以不变, 但新名字不能重复了
            VarProcessReportForm reportForm = varProcessReportFormService.getOne(Wrappers.<VarProcessReportForm>lambdaQuery()
                    .select(VarProcessReportForm::getId, VarProcessReportForm::getName)
                    .eq(VarProcessReportForm::getId, inputDto.getId()));
            if (!reportForm.getName().equals(inputDto.getName())) {
                this.checkName(inputDto.getName());
            }
        }
        // 2.时间范围的开始时间和结束时间的校验
        this.checkLocalDateTime(inputDto.getStartTime(), inputDto.getEndTime());
        // 3.监控对象的校验
        this.checkMonitorObject(inputDto);
        // 4.监控指标的校验
        this.checkIndicator(inputDto);
        // 5.展示维度的校验
        this.checkDisplayDimension(inputDto);
        // 6.其他的校验
        this.checkOther(inputDto);
    }

    /**
     * 校验监控报表实体类(用于生成报表)
     * @param inputDto 输入实体类对象
     */
    private void checkReportFormPreview(ReportFormCreateInputDto inputDto) {
        // 1.时间范围的开始时间和结束时间的校验
        this.checkLocalDateTime(inputDto.getStartTime(), inputDto.getEndTime());
        // 2.监控对象的校验
        this.checkMonitorObject(inputDto);
        // 3.监控指标的校验
        this.checkIndicator(inputDto);
        // 4.展示维度的校验
        this.checkDisplayDimension(inputDto);
        // 5.其他的校验
        this.checkOther(inputDto);
    }

    /**
     * 名称重复的校验
     * @param name 报表名称
     */
    private void checkName(String name) {
        long count = varProcessReportFormService.count(Wrappers.<VarProcessReportForm>lambdaQuery()
                .eq(VarProcessReportForm::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .eq(VarProcessReportForm::getName, name));
        if (count > 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "报表名称重复");
        }
    }

    /**
     * 判断两个LocalDateTime之间的问题
     * @param start 开始时间
     * @param end 结束时间
     */
    private void checkLocalDateTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "开始时间必须在结束时间之前!");
        }
        LocalDateTime newStart = start.plus(MagicNumbers.ONE, ChronoUnit.YEARS);
        if (newStart.isBefore(end)) {
            log.info("开始时间与结束时间的间隔超过了一年!");
        }
    }

    /**
     * 监控对象的校验
     * @param inputDto 输入实体类对象
     */
    private void checkMonitorObject(ReportFormCreateInputDto inputDto) {
        MonitorObjectMappingVo monitorObjectMappingVo = inputDto.getMonitorObjectMappingVo();
        ReportFormCategoryEnum categoryEnum = inputDto.getCategoryEnum();
        // 1.如果是服务报表
        if (categoryEnum.equals(ReportFormCategoryEnum.SERVICE)) {
            if (CollectionUtils.isEmpty(monitorObjectMappingVo.getServiceIds())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "监控对象不能为空!");
            }
        } else if (categoryEnum.equals(ReportFormCategoryEnum.SINGLE_VARIABLE_ANALYZE)) {
            // 2.如果是单指标分析报表
            if (monitorObjectMappingVo.getVariableMappingVo() == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "监控对象不能为空!");
            }
            if (CollectionUtils.isEmpty(inputDto.getManifests())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "变量清单不能为空!");
            }
        } else {
            // 3.如果是指标对比分析报表
            if (CollectionUtils.isEmpty(monitorObjectMappingVo.getVariableMappingVos())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "监控对象不能为空!");
            }
            if (CollectionUtils.isEmpty(inputDto.getManifests())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "变量清单不能为空!");
            }
        }
    }

    /**
     * 监控指标的校验
     * @param inputDto 输入实体类对象
     */
    private void checkIndicator(ReportFormCreateInputDto inputDto) {
        IndicatorMappingVo indicatorMappingVo = inputDto.getIndicatorMappingVo();
        ReportFormCategoryEnum categoryEnum = inputDto.getCategoryEnum();
        if (categoryEnum.equals(ReportFormCategoryEnum.SERVICE)) {
            // 1.如果选择了服务报表, 且选择了响应码占比
            if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.RESPONSE_CODE_RATIO)) {
                if (StringUtils.isEmpty(indicatorMappingVo.getResponseCode())) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "响应码不能为空!");
                }
                if (indicatorMappingVo.getResponseCode().contains(MagicStrings.COMMA_CHINESE)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "不能输入中文逗号!");
                }
                List<String> result = new ArrayList<>();
                String[] split = indicatorMappingVo.getResponseCode().split(MagicStrings.COMMA_ENGLISH);
                for (String s : split) {
                    result.add(s.replace(MagicStrings.SPACE, MagicStrings.EMPTY_STRING));
                }
                Set<String> set = new HashSet<>(result);
                if (set.size() != result.size()) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "响应码不能重复!");
                }
            }
        } else if (categoryEnum.equals(ReportFormCategoryEnum.SINGLE_VARIABLE_ANALYZE)) {
            // 2.如果选择了单指标分析报表
            this.checkSingleVariableMonitorIndicator(inputDto.getManifests(), indicatorMappingVo);
        } else {
            // 3.如果选择了指标对比分析报表
            this.checkVariableCompareMonitorIndicator(inputDto.getManifests(), indicatorMappingVo);
        }
    }

    /**
     * 单指标分析报表的监控指标校验
     * @param manifestNameVos 变量清单
     * @param indicatorMappingVo 监控指标
     */
    private void checkSingleVariableMonitorIndicator(List<ServiceManifestNameVo> manifestNameVos, IndicatorMappingVo indicatorMappingVo) {
        // 2.1 如果是选择了特殊值占比
        if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.SPECIAL_RATIO)) {
            if (CollectionUtils.isEmpty(indicatorMappingVo.getSpecialMappingVoList())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "特殊值占比不能为空!");
            }
        }
        // 2.2 如果是选择了PSI
        if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.PSI)) {
            ReportFormPsiMappingVo psiMappingVo = indicatorMappingVo.getPsiMappingVo();
            if (psiMappingVo.getBaseIndexFlag() == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "必须要选定PSI中的一项!");
            }
            // 2.2.1 如果选择了时间范围数据作为基准
            if (psiMappingVo.getBaseIndexFlag().equals(ReportFormPsiEnum.DATETIME_SCOPE_DATA)) {
                if (psiMappingVo.getStartDateTime() == null || psiMappingVo.getEndDateTime() == null) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "时间范围不能为空!");
                }
                this.checkLocalDateTime(psiMappingVo.getStartDateTime(), psiMappingVo.getEndDateTime());
            } else if (psiMappingVo.getBaseIndexFlag().equals(ReportFormPsiEnum.BASIC_INDICATOR)) {
                // 2.2.2 如果选择了基准指标
                if (manifestNameVos.size() != MagicNumbers.ONE) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "只有选择了一个变量清单才可以勾选选择基准指标!");
                }
                if (StringUtils.isEmpty(psiMappingVo.getBaseIndex()) || StringUtils.isEmpty(psiMappingVo.getBaseIndexVal()) || psiMappingVo.getBaseIndexCallDate() == null) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "选择基准指标以后, 必须指定基准分组指标、基准数据项和基准指标调用时间段!");
                }
            } else if (psiMappingVo.getBaseIndexFlag().equals(ReportFormPsiEnum.MANIFEST)) {
                // 2.2.3 如果选择了清单
                if (manifestNameVos.size() < MagicNumbers.TWO) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "只有选择了多个变量清单, 才能勾选清单!");
                }
                if (psiMappingVo.getServiceManifestNameVo() == null) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "基准清单不能为空!");
                }
            }
            // 2.2.4 最后校验特殊值
            if (CollectionUtils.isEmpty(indicatorMappingVo.getSpecialMappingVoList())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "特殊值占比不能为空!");
            }
        }
        // 2.3 如果是选择了IV
        if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.IV)) {
            ConfigIvMappingVo ivMappingVo = indicatorMappingVo.getIvMappingVo();
            if (ivMappingVo.getSourceType() == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "必须要选定IV中的一项!");
            }
            // 2.3.1 只能选择来源于内部数据表
            if (ivMappingVo.getSourceType() != MagicNumbers.ONE) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "IV只能选择来源于内部数据表");
            }
            // 2.3.2 五个必选的下拉框不能为空
            if (StringUtils.isEmpty(ivMappingVo.getTableName()) || StringUtils.isEmpty(ivMappingVo.getRelationField()) || StringUtils.isEmpty(ivMappingVo.getTargetField()) || CollectionUtils.isEmpty(ivMappingVo.getGoodValues()) || CollectionUtils.isEmpty(ivMappingVo.getBadValues())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "选择来源于内部数据表以后, 数据表、表关联字段、Y指标、good标签值和bad标签值必须要指定!");
            }
            // 2.3.3 最后校验特殊值占比
            if (CollectionUtils.isEmpty(indicatorMappingVo.getSpecialMappingVoList())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "特殊值占比不能为空!");
            }
        }
    }

    /**
     * 指标分析对比报表中的监控指标校验
     * @param manifestNameVos 选中的变量清单List
     * @param indicatorMappingVo 监控指标实体类
     */
    private void checkVariableCompareMonitorIndicator(List<ServiceManifestNameVo> manifestNameVos, IndicatorMappingVo indicatorMappingVo) {
        // 3.1 如果是选择了特殊值占比
        if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.SPECIAL_RATIO)) {
            if (CollectionUtils.isEmpty(indicatorMappingVo.getSpecialMappingVoList())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "特殊值占比不能为空!");
            }
        }
        // 3.2 如果是选择了PSI
        if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.PSI)) {
            ReportFormPsiMappingVo psiMappingVo = indicatorMappingVo.getPsiMappingVo();
            if (psiMappingVo.getBaseIndexFlag() == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "必须要选定PSI中的一项!");
            } else if (psiMappingVo.getBaseIndexFlag().equals(ReportFormPsiEnum.DATETIME_SCOPE_DATA)) {
                // 3.2.1 如果选择了时间范围数据作为基准
                if (psiMappingVo.getStartDateTime() == null || psiMappingVo.getEndDateTime() == null) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "时间范围不能为空!");
                }
                this.checkLocalDateTime(psiMappingVo.getStartDateTime(), psiMappingVo.getEndDateTime());
            } else if (psiMappingVo.getBaseIndexFlag().equals(ReportFormPsiEnum.BASIC_INDICATOR)) {
                // 3.2.2 选择了基准指标
                if (manifestNameVos.size() != MagicNumbers.ONE) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "只有选择了一个变量清单才可以勾选选择基准指标!");
                }
                if (StringUtils.isEmpty(psiMappingVo.getBaseIndex()) || StringUtils.isEmpty(psiMappingVo.getBaseIndexVal()) || psiMappingVo.getBaseIndexCallDate() == null) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "选择基准指标以后, 必须指定基准分组指标、基准数据项和基准指标调用时间段!");
                }
            }
            // 3.2.3 最后校验特殊值占比
            if (CollectionUtils.isEmpty(indicatorMappingVo.getSpecialMappingVoList())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "特殊值占比不能为空!");
            }
        }
        // 3.3 如果是选择了IV
        if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.IV)) {
            ConfigIvMappingVo ivMappingVo = indicatorMappingVo.getIvMappingVo();
            if (ivMappingVo.getSourceType() == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "必须要选定IV中的一项!");
            }
            // 3.3.1 只能选择来源于内部数据表
            if (ivMappingVo.getSourceType() != MagicNumbers.ONE) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "IV只能选择来源于内部数据表");
            }
            // 3.3.2 五个必选的下拉框不能为空
            if (StringUtils.isEmpty(ivMappingVo.getTableName()) || StringUtils.isEmpty(ivMappingVo.getRelationField()) || StringUtils.isEmpty(ivMappingVo.getTargetField()) || CollectionUtils.isEmpty(ivMappingVo.getGoodValues()) || CollectionUtils.isEmpty(ivMappingVo.getBadValues())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "选择来源于内部数据表以后, 数据表、表关联字段、Y指标、good标签值和bad标签值必须要指定!");
            }
            // 3.3.3 最后校验特殊值占比
            if (CollectionUtils.isEmpty(indicatorMappingVo.getSpecialMappingVoList())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "特殊值占比不能为空!");
            }
        }
    }

    /**
     * 展示维度的校验
     * @param inputDto 输入实体类对象
     */
    private void checkDisplayDimension(ReportFormCreateInputDto inputDto) {
        DisplayDimensionVo displayDimensionVo = inputDto.getDisplayDimensionVo();
        // 1.如果展示维度为为时间维度
        ReportFormTypeEnum type = inputDto.getType();
        if (displayDimensionVo.getDisplayDimension().equals(ReportFormDisplayDimensionEnum.TIME_SCOPE)) {
            if (displayDimensionVo.getTimeUnit() == null) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "选择时间维度以后, 必须指定时间刻度!");
            }
            if (type.equals(ReportFormTypeEnum.TOP_CHART) || type.equals(ReportFormTypeEnum.RING_CHART)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "展示维度选择时间范围以后, 不能选择TOP图和环形图!");
            }
        }
        // 2.匹配错误的情况
        if (inputDto.getCategoryEnum().equals(ReportFormCategoryEnum.SERVICE) && displayDimensionVo.getDisplayDimension().equals(ReportFormDisplayDimensionEnum.MANIFEST)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "服务报表中的展示维度不可以是变量清单!");
        }
        if (inputDto.getCategoryEnum().equals(ReportFormCategoryEnum.SINGLE_VARIABLE_ANALYZE) && displayDimensionVo.getDisplayDimension().equals(ReportFormDisplayDimensionEnum.MONITOR_OBJECT)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "单指标分析报表中的展示维度不可以是监控对象!");
        }
        if (inputDto.getCategoryEnum().equals(ReportFormCategoryEnum.VARIABLE_COMPARE_ANALYZE) && displayDimensionVo.getDisplayDimension().equals(ReportFormDisplayDimensionEnum.MANIFEST)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "指标对比分析报表中的展示维度不可以是变量清单!");
        }
    }

    /**
     * 其他的校验
     * @param inputDto 输入实体类对象
     */
    private void checkOther(ReportFormCreateInputDto inputDto) {
        // 1.报表类型和监控指标的校验
        ReportFormCategoryEnum categoryEnum = inputDto.getCategoryEnum();
        IndicatorMappingVo indicatorMappingVo = inputDto.getIndicatorMappingVo();
        if (categoryEnum.equals(ReportFormCategoryEnum.SERVICE)) {
            if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.MISSING_RATIO) || indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.SPECIAL_RATIO) || indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.PSI) || indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.IV)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "服务报表不能选择缺失率、特殊值占比、PSI和IV!");
            }
        } else {
            if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.CALL_VOLUME) || indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.FAILURE_RATE) || indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.MAX_RESPONSE_TIME) || indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.AVG_RESPONSE_TIME) || indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.RESPONSE_CODE_RATIO)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "指标相关的报表不能选择调用量、失败率、最大响应时间、平均响应时间和响应码占比!");
            }
        }
        // 2.如果是单指标分析报表和指标对比分析报表
        if (categoryEnum.equals(ReportFormCategoryEnum.SINGLE_VARIABLE_ANALYZE) || categoryEnum.equals(ReportFormCategoryEnum.VARIABLE_COMPARE_ANALYZE)) {
            // 2.1 校验IV的good和bad标签是否重复
            if (indicatorMappingVo.getMonitorIndicatorEnum().equals(MonitorIndicatorEnum.IV)) {
                ConfigIvMappingVo ivMappingVo = indicatorMappingVo.getIvMappingVo();
                if (ivMappingVo.getSourceType() == MagicNumbers.ONE) {
                    Set<String> good = new HashSet<>(ivMappingVo.getGoodValues());
                    Set<String> bad = new HashSet<>(ivMappingVo.getBadValues());
                    good.retainAll(bad);
                    if (!CollectionUtils.isEmpty(good)) {
                        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "good标签和bad标签的值必须互斥!");
                    }
                }
            }
        }
    }

    /**
     * 报表管理处的报表查询
     * @param inputDto 输入实体类对象
     * @return 报表的列表
     */
    public ReportFormListOutputDto searchReportForm(ReportFormSearchInputDto inputDto) {
        // 1.组件条件查询的SQL语句
        IPage<VarProcessReportForm> page = new Page<>(inputDto.getCurrentNo(), inputDto.getSize());
        LambdaQueryWrapper<VarProcessReportForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VarProcessReportForm::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());
        wrapper.orderByDesc(VarProcessReportForm::getUpdatedTime);
        if (inputDto.getCategory() != null) {
            wrapper.eq(VarProcessReportForm::getCategory, inputDto.getCategory());
        }
        if (inputDto.getState() != null) {
            wrapper.eq(VarProcessReportForm::getState, inputDto.getState());
        }
        if (!StringUtils.isEmpty(inputDto.getName())) {
            wrapper.like(VarProcessReportForm::getName, inputDto.getName());
        }
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        if (!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes())) {
            wrapper.in(VarProcessReportForm::getDeptCode, roleDataAuthority.getDeptCodes());
        }
        if (!CollectionUtils.isEmpty(roleDataAuthority.getUserNames())) {
            wrapper.in(VarProcessReportForm::getCreatedUser, roleDataAuthority.getUserNames());
        }
        // 2.直接查询出结果
        IPage<VarProcessReportForm> pageResult = varProcessReportFormService.page(page, wrapper);
        List<VarProcessReportForm> list = pageResult.getRecords();
        // 3.调整数据结构
        ReportFormListOutputDto result = new ReportFormListOutputDto();
        List<ReportFormOutputDto> records = new ArrayList<>();

        List<String> userNameList = list.stream().flatMap(obj -> Stream.of(obj.getCreatedUser(), obj.getUpdatedUser())).distinct().collect(Collectors.toList());
        Map<String, String> fullNameMap = userService.findFullNameMapByUserNames(userNameList);
        for (VarProcessReportForm item : list) {
            ReportFormOutputDto reportForm = new ReportFormOutputDto();
            reportForm.setId(item.getId());
            reportForm.setName(item.getName());
            reportForm.setCategory(item.getCategory());
            reportForm.setType(item.getType());
            IndicatorMappingVo indicatorMappingVo = JSONObject.parseObject(item.getMonitorIndicator(), IndicatorMappingVo.class);
            reportForm.setIndicator(indicatorMappingVo.getMonitorIndicatorEnum());
            reportForm.setState(item.getState());
            reportForm.setCreatedUser(fullNameMap.get(item.getCreatedUser()));
            reportForm.setCreatedTime(item.getCreatedTime());
            reportForm.setUpdatedUser(fullNameMap.get(item.getUpdatedUser()));
            reportForm.setUpdatedTime(item.getUpdatedTime());
            records.add(reportForm);
        }
        // 4.返回
        result.setRecords(records);
        result.setTotal(pageResult.getTotal());
        return result;
    }

    /**
     * 删除监控报表
     * @param inputDto 输入实体类对象
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Boolean deleteReportForm(ReportFormDeleteInputDto inputDto) {
        // 1.先获取该监控报表
        VarProcessReportForm reportForm = varProcessReportFormService.getOne(Wrappers.<VarProcessReportForm>lambdaQuery()
                .select(VarProcessReportForm::getId, VarProcessReportForm::getDeleteFlag)
                .eq(VarProcessReportForm::getId, inputDto.getId()));
        if (reportForm == null || reportForm.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该监控报表已经被删除了，不可以重复删除!");
        }
        // 2.逻辑删除该监控报表
        LambdaUpdateWrapper<VarProcessReportForm> reportFormLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        reportFormLambdaUpdateWrapper.eq(VarProcessReportForm::getId, inputDto.getId());
        reportFormLambdaUpdateWrapper.set(VarProcessReportForm::getDeleteFlag, DeleteFlagEnum.DELETED.getCode());
        reportFormLambdaUpdateWrapper.set(VarProcessReportForm::getUpdatedUser, SessionContext.getSessionUser().getUsername());
        boolean isUpdate = varProcessReportFormService.update(reportFormLambdaUpdateWrapper);
        if (!isUpdate) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "监控报表删除失败!");
        } else {
            return true;
        }
    }

    /**
     * 复制监控报表
     * @param inputDto 输入实体类对象
     * @return 新监控报表的Id
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long duplicateReportForm(ReportFormDuplicationInputDto inputDto) {
        // 1.先查看被复制的监控报表是否存在
        VarProcessReportForm reportForm = varProcessReportFormService.getById(inputDto.getId());
        if (reportForm == null || reportForm.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该监控报表已经被删除了，不可以进行复制操作!");
        }
        // 2.开始复制，先对比名称是否重复
        this.checkName(inputDto.getName());
        // 3.开始复制
        VarProcessReportForm duplication = VarProcessReportForm.builder()
                .name(inputDto.getName()).category(reportForm.getCategory())
                .startTime(reportForm.getStartTime()).endTime(reportForm.getEndTime())
                .monitorObject(reportForm.getMonitorObject()).manifests(reportForm.getManifests())
                .monitorIndicator(reportForm.getMonitorIndicator()).displayDimension(reportForm.getDisplayDimension())
                .type(reportForm.getType()).reportFormOrder(reportForm.getReportFormOrder())
                .state(ReportFromStateEnum.EDIT).deleteFlag(DeleteFlagEnum.USABLE.getCode())
                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername())
                .build();
        // 4.插入数据
        varProcessReportFormService.save(duplication);
        return duplication.getId();
    }

    /**
     * 查看详情
     * @param reportFormId 监控报表的Id
     * @return 详情信息
     */
    public ReportFormDetailOutputDto getDetail(Long reportFormId) {
        // 1.先获取监控报表
        VarProcessReportForm reportForm = varProcessReportFormService.getById(reportFormId);
        if (reportForm == null || reportForm.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该监控报表已经被删除了，不可以进行复制操作!");
        }
        // 2.填充要返回给前端的字段
        ReportFormDetailOutputDto result = new ReportFormDetailOutputDto();
        this.parseObjectToOutput(reportForm, result);
        return result;
    }

    /**
     * 把从数据库中查出来的报表实体转换为给前端看的实体类
     * @param reportForm 从数据库中查出来的报表实体
     * @param result 给前端看的实体类
     */
    private void parseObjectToOutput(VarProcessReportForm reportForm, ReportFormDetailOutputDto result) {
        result.setId(reportForm.getId());
        result.setName(reportForm.getName());
        result.setCategory(reportForm.getCategory());
        result.setStartTime(reportForm.getStartTime());
        result.setEndTime(reportForm.getEndTime());
        MonitorObjectMappingVo monitorObjectMappingVo = JSONObject.parseObject(reportForm.getMonitorObject(), MonitorObjectMappingVo.class);
        if (!CollectionUtils.isEmpty(monitorObjectMappingVo.getServiceIds())) {
            monitorObjectMappingVo.setServiceIdNameMap(varProcessServiceVersionService.findserviceListByVersionIds(monitorObjectMappingVo.getServiceIds())
                    .stream().collect(Collectors.toMap(ServiceInfoDto::getId,item -> item.getName() + StringPool.LEFT_BRACKET + item.getVersion() + StringPool.RIGHT_BRACKET)));
        }
        result.setMonitorObjectMappingVo(monitorObjectMappingVo);
        List<ServiceManifestNameVo> manifests = JSONObject.parseObject(reportForm.getManifests(), new TypeReference<List<ServiceManifestNameVo>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        result.setManifests(manifests);
        IndicatorMappingVo indicatorMappingVo = JSONObject.parseObject(reportForm.getMonitorIndicator(), IndicatorMappingVo.class);
        result.setIndicatorMappingVo(indicatorMappingVo);
        DisplayDimensionVo displayDimensionVo = JSONObject.parseObject(reportForm.getDisplayDimension(), DisplayDimensionVo.class);
        result.setDisplayDimensionVo(displayDimensionVo);
        result.setType(reportForm.getType());
        result.setOrder(reportForm.getReportFormOrder());
        result.setState(reportForm.getState());
    }

    /**
     * 启用监控报表
     * @param inputDto 输入实体类对象
     * @return 启用后的结果
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Boolean enableReportForm(ReportFormStatusInputDto inputDto) {
        // 1.先获取监控报表
        VarProcessReportForm reportForm = varProcessReportFormService.getById(inputDto.getId());
        if (reportForm == null || reportForm.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该监控报表已经被删除了，不可以进行复制操作!");
        }
        // 2.更改状态
        reportForm.setState(ReportFromStateEnum.UP);
        reportForm.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        reportForm.setUpdatedTime(new Date());
        return varProcessReportFormService.updateById(reportForm);
    }

    /**
     * 停用监控报表
     * @param inputDto 输入实体类对象
     * @return 停用后的结果
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Boolean disableReportForm(ReportFormStatusInputDto inputDto) {
        // 1.先获取监控报表
        VarProcessReportForm reportForm = varProcessReportFormService.getById(inputDto.getId());
        if (reportForm == null || reportForm.getDeleteFlag().equals(DeleteFlagEnum.DELETED.getCode())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该监控报表已经被删除了，不可以进行复制操作!");
        }
        // 2.更改状态
        if (reportForm.getState().equals(ReportFromStateEnum.UP)) {
            reportForm.setState(ReportFromStateEnum.DOWN);
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "操作有误!");
        }
        reportForm.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        return varProcessReportFormService.updateById(reportForm);
    }

    /**
     * 获取报表列表
     * @return 报表列表
     */
    public ReportFormsOutputDto getReportFormList() {
        // 1.添加权限
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        List<String> deptCodes = roleDataAuthority.getDeptCodes();
        List<String> userNames = roleDataAuthority.getUserNames();
        // 2.先获取服务报表的列表
        List<ReportFormItemVo> serviceReportForm = varProcessReportFormService.getServiceReportFormList(deptCodes, userNames);
        // 3.在获取单指标分析报表的列表
        List<ReportFormItemVo> variableReportForm = varProcessReportFormService.getVariableReportFormList(deptCodes, userNames);
        // 4.最后获取指标对比分析报表
        List<ReportFormItemVo> variableCompareReportFrom = varProcessReportFormService.getVariableCompareReportFromList(deptCodes, userNames);
        // 5.组装实体类，返回
        return new ReportFormsOutputDto(serviceReportForm, variableReportForm, variableCompareReportFrom);
    }

    /**
     * 保存监控报表的更新
     * @param inputDto 输入实体类对象
     * @return 原对象的ID
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Long saveUpdate(ReportFormCreateInputDto inputDto) {
        // 1.先校验
        this.checkReportFormPreview(inputDto, false);

        MonitorObjectMappingVo monitorObjectMappingVo = inputDto.getMonitorObjectMappingVo();
        if (!CollectionUtils.isEmpty(monitorObjectMappingVo.getServiceIds())) {
            monitorObjectMappingVo.setServiceIdNameMap(varProcessServiceVersionService.findserviceListByVersionIds(monitorObjectMappingVo.getServiceIds())
                    .stream().collect(Collectors.toMap(ServiceInfoDto::getId,item -> item.getName() + StringPool.LEFT_BRACKET + item.getVersion() + StringPool.RIGHT_BRACKET)));
        }

        // 2.再确定修改的东西
        LambdaUpdateWrapper<VarProcessReportForm> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(VarProcessReportForm::getId, inputDto.getId());
        wrapper.eq(VarProcessReportForm::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());
        wrapper.set(VarProcessReportForm::getName, inputDto.getName());
        wrapper.set(VarProcessReportForm::getCategory, inputDto.getCategoryEnum());
        wrapper.set(VarProcessReportForm::getStartTime, inputDto.getStartTime());
        wrapper.set(VarProcessReportForm::getEndTime, inputDto.getEndTime());
        wrapper.set(VarProcessReportForm::getMonitorObject, JSONObject.toJSONString(monitorObjectMappingVo));
        wrapper.set(VarProcessReportForm::getManifests, JSONObject.toJSONString(inputDto.getManifests()));
        wrapper.set(VarProcessReportForm::getMonitorIndicator, JSONObject.toJSONString(inputDto.getIndicatorMappingVo(), SerializerFeature.WriteDateUseDateFormat));
        wrapper.set(VarProcessReportForm::getDisplayDimension, JSONObject.toJSONString(inputDto.getDisplayDimensionVo()));
        wrapper.set(VarProcessReportForm::getType, inputDto.getType());
        wrapper.set(VarProcessReportForm::getReportFormOrder, inputDto.getOrder());
        wrapper.set(VarProcessReportForm::getUpdatedUser, SessionContext.getSessionUser().getUsername());
        wrapper.set(VarProcessReportForm::getUpdatedTime, new Date());
        // 3.更新
        varProcessReportFormService.update(wrapper);
        return inputDto.getId();
    }


    /**
     * 预览监控图表
     * @param inputDto 入参
     * @return 图表jSON
     */
    public MonitoringDiagramOutputVo previewDiagram(ReportFormCreateInputDto inputDto) {
        // 1.先校验实体类
        this.checkReportFormPreview(inputDto);
        // 2.获取对应的工厂类对象
        GenerateReportFormStrategy generateReportFormService = generateReportFormServiceFactory.getGenerateReportFormService(inputDto.getCategoryEnum());
        // 3.调用类对象中的方法,获取报表
        return generateReportFormService.generateReportForm(inputDto);
    }

    /**
     * 查看报表
     * @param id 监控报表的ID
     * @return Echarts数据
     */
    public MonitoringDiagramOutputVo viewReportForm(Long id) {
        // 1.先查询出监控报表
        VarProcessReportForm varProcessReportForm = varProcessReportFormService.getById(id);
        if (varProcessReportForm == null || varProcessReportForm.getDeleteFlag() == MagicNumbers.ZERO || !varProcessReportForm.getState().equals(ReportFromStateEnum.UP)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该监控不存在或者不可用!");
        }
        // 2.获取dto
        ReportFormDetailOutputDto outputDto = new ReportFormDetailOutputDto();
        parseObjectToOutput(varProcessReportForm, outputDto);
        ReportFormCreateInputDto inputDto = new ReportFormCreateInputDto();
        BeanUtil.copyProperties(outputDto, inputDto);
        inputDto.setCategoryEnum(outputDto.getCategory());
        // 3.得到结果
        return this.previewDiagram(inputDto);
    }
}
