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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.OverViewQuantityAndStatusVo;
import com.wiseco.var.process.app.server.controller.vo.output.OverViewNameAndQuantityOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.OverViewQuantityAndStatusOutputVo;
import com.wiseco.var.process.app.server.controller.vo.output.OverviewTargetRankingOutputVo;
import com.wiseco.var.process.app.server.enums.CallVolumeByTimeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessService;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingOverallService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingTaskService;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.dto.OverviewTargetStatisticsDto;
import com.wiseco.var.process.app.server.service.dto.ServiceInfoDto;
import com.wiseco.var.process.app.server.service.dto.SortFieldSplittingDto;
import com.wiseco.var.process.app.server.service.dto.StartEndTimeDto;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import com.wiseco.var.process.app.server.service.statistics.StatisticsOverallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class OverViewBiz {


    @Resource
    private VarProcessManifestService varProcessManifestService;
    @Resource
    private VarProcessManifestVariableService varProcessManifestVariableService;

    @Resource
    private BacktrackingTaskService backtrackingTaskService;

    @Resource
    private BacktrackingService backtrackingService;

    @Resource
    private VarProcessVariableService varProcessVariableService;

    @Resource
    private VarProcessFunctionService varProcessFunctionService;

    @Resource
    private DbOperateService dbOperateService;

    @Resource
    private BacktrackingOverallService backtrackingOverallService;

    @Resource
    private StatisticsOverallService statisticsOverallService;

    @Resource
    private VarProcessServiceVersionService varProcessServiceVersionService;

    @Autowired
    private AuthService authService;

    @Autowired
    private VarProcessStatisticsConfigService varProcessStatisticsConfigService;

    @Autowired
    private VarProcessBatchBacktrackingStatisticsConfigService backtrackingStatisticsConfigService;

    private static final String VAR_PROCESS_LOG = "var_process_log";

    private static final String DESC = "desc";

    private static final String ASC = "asc";

    private static final String MIN = "min";

    private static final String MAX = "max";

    /**
     * 睿信概览统计
     *
     * @return 所有统计数量与状态
     */
    public OverViewQuantityAndStatusOutputVo getOverViewBizQuantityAndStatus() {

        getVariableQuantityAndStatus();
        OverViewQuantityAndStatusOutputVo variable = getVariableQuantityAndStatus();
        OverViewQuantityAndStatusOutputVo manifest = getManifestQuantityAndStatus();
        OverViewQuantityAndStatusOutputVo service = getServiceQuantityAndStatus();
        OverViewQuantityAndStatusOutputVo batchBacktracking = getBatchBacktrackingQuantityAndStatus();
        OverViewQuantityAndStatusOutputVo function = getFunctionQuantityAndStatus();


        OverViewQuantityAndStatusOutputVo outputVo = new OverViewQuantityAndStatusOutputVo();
        outputVo.setVariableAllNumber(variable.getVariableAllNumber());
        outputVo.setVariableAllVersionNumber(variable.getVariableAllVersionNumber());
        outputVo.setVariableUpNumber(variable.getVariableUpNumber());
        outputVo.setVariableEditNumber(variable.getVariableEditNumber());
        outputVo.setVariableDownNumber(variable.getVariableDownNumber());
        outputVo.setManifestAllNumber(manifest.getManifestAllNumber());
        outputVo.setServiceAllNumber(service.getServiceAllNumber());
        outputVo.setServiceAllVersionNumber(service.getServiceAllVersionNumber());
        outputVo.setServiceUpNumber(service.getServiceUpNumber());
        outputVo.setServiceEditNumber(service.getServiceEditNumber());
        outputVo.setServiceDownNumber(service.getServiceDownNumber());
        outputVo.setBatchBacktrackingAllNumber(batchBacktracking.getBatchBacktrackingAllNumber());
        outputVo.setBatchBacktrackingUpNumber(batchBacktracking.getBatchBacktrackingUpNumber());
        outputVo.setBatchBacktrackingEditNumber(batchBacktracking.getBatchBacktrackingEditNumber());
        outputVo.setBatchBacktrackingDownNumber(batchBacktracking.getBatchBacktrackingDownNumber());
        outputVo.setPrepAllNumber(function.getPrepAllNumber());
        outputVo.setTemplateAllNumber(function.getTemplateAllNumber());

        return outputVo;
    }


    /**
     * 获取变量的所有数量与各种状态数量
     *
     * @return 变量的所有数量与各种状态数量
     */
    public OverViewQuantityAndStatusOutputVo getVariableQuantityAndStatus() {

        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        LambdaQueryWrapper<VarProcessVariable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VarProcessVariable::getIdentifier, VarProcessVariable::getStatus);
        queryWrapper.eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessVariable::getDeptCode, roleDataAuthority.getDeptCodes());
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessVariable::getCreatedUser, roleDataAuthority.getUserNames());
        List<VarProcessVariable> dataList = varProcessVariableService.list(queryWrapper);

        Map<String, List<VarProcessVariable>> groupedByIdentifier = dataList.stream().collect(Collectors.groupingBy(VarProcessVariable::getIdentifier));

        OverViewQuantityAndStatusOutputVo overViewQuantityAndStatusVo = OverViewQuantityAndStatusOutputVo.builder()
                .variableAllNumber(groupedByIdentifier.size())
                .variableAllVersionNumber(dataList.size())
                .variableUpNumber((int) dataList.stream().filter(item -> item.getStatus() == VariableStatusEnum.UP).count())
                .variableEditNumber((int) dataList.stream().filter(item -> item.getStatus() == VariableStatusEnum.EDIT).count())
                .variableDownNumber((int) dataList.stream().filter(item -> item.getStatus() == VariableStatusEnum.DOWN).count())
                .build();
        return overViewQuantityAndStatusVo;
    }

    /**
     * 获取变量清单的所有状态的数量
     *
     * @return 变量清单的所有状态的数量
     */
    public OverViewQuantityAndStatusOutputVo getManifestQuantityAndStatus() {
        LambdaQueryWrapper<VarProcessManifest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VarProcessManifest::getId);
        queryWrapper.eq(VarProcessManifest::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());

        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessManifest::getDeptCode, roleDataAuthority.getDeptCodes());
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessManifest::getCreatedUser, roleDataAuthority.getUserNames());

        List<VarProcessManifest> dataList = varProcessManifestService.list(queryWrapper);
        int allNumber = dataList.size();

        OverViewQuantityAndStatusOutputVo overViewQuantityAndStatusVo = OverViewQuantityAndStatusOutputVo.builder()
                .manifestAllNumber(allNumber)
                .build();
        return overViewQuantityAndStatusVo;
    }

    /**
     * 获取实时服务的所有数量与各种状态数量
     *
     * @return 实时服务的所有数量与各种状态数量
     */
    public OverViewQuantityAndStatusOutputVo getServiceQuantityAndStatus() {

        QueryWrapper<VarProcessService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", MagicNumbers.ONE);

        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        List<ServiceInfoDto> dataList = varProcessServiceVersionService.findAllServiceInfos(roleDataAuthority.getDeptCodes(), roleDataAuthority.getUserNames());

        Map<String, List<ServiceInfoDto>> groupedByName = dataList.stream().collect(Collectors.groupingBy(ServiceInfoDto::getName));

        OverViewQuantityAndStatusOutputVo overViewQuantityAndStatusVo = OverViewQuantityAndStatusOutputVo.builder()
                .serviceAllNumber(groupedByName.size())
                .serviceAllVersionNumber(dataList.size())
                .serviceUpNumber((int) dataList.stream().filter(item -> item.getState() == VarProcessServiceStateEnum.ENABLED).count())
                .serviceEditNumber((int) dataList.stream().filter(item -> item.getState() == VarProcessServiceStateEnum.EDITING).count())
                .serviceDownNumber((int) dataList.stream().filter(item -> item.getState() == VarProcessServiceStateEnum.DISABLED).count())
                .build();
        return overViewQuantityAndStatusVo;
    }

    /**
     * 获取批量回溯的所有数量与各种状态数量
     *
     * @return 批量回溯的所有数量与各种状态数量
     */
    public OverViewQuantityAndStatusOutputVo getBatchBacktrackingQuantityAndStatus() {
        LambdaQueryWrapper<VarProcessBatchBacktracking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VarProcessBatchBacktracking::getId, VarProcessBatchBacktracking::getStatus);
        queryWrapper.eq(VarProcessBatchBacktracking::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());

        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessBatchBacktracking::getDeptCode, roleDataAuthority.getDeptCodes());
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessBatchBacktracking::getCreatedUser, roleDataAuthority.getUserNames());

        List<VarProcessBatchBacktracking> dataList = backtrackingService.list(queryWrapper);

        OverViewQuantityAndStatusOutputVo overViewQuantityAndStatusVo = OverViewQuantityAndStatusOutputVo.builder()
                .batchBacktrackingAllNumber(dataList.size())
                .batchBacktrackingUpNumber((int) dataList.stream().filter(item -> item.getStatus() == FlowStatusEnum.UP).count())
                .batchBacktrackingEditNumber((int) dataList.stream().filter(item -> item.getStatus() == FlowStatusEnum.EDIT).count())
                .batchBacktrackingDownNumber((int) dataList.stream().filter(item -> item.getStatus() == FlowStatusEnum.DOWN).count())
                .build();
        return overViewQuantityAndStatusVo;
    }

    /**
     * 获取预处理的所有数量
     *
     * @return 预处理的所有数量
     */
    public OverViewQuantityAndStatusOutputVo getFunctionQuantityAndStatus() {
        LambdaQueryWrapper<VarProcessFunction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VarProcessFunction::getId, VarProcessFunction::getFunctionType);
        queryWrapper.eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());

        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes()), VarProcessFunction::getCreatedDeptCode, roleDataAuthority.getDeptCodes());
        queryWrapper.in(!CollectionUtils.isEmpty(roleDataAuthority.getUserNames()), VarProcessFunction::getCreatedUser, roleDataAuthority.getUserNames());

        List<VarProcessFunction> dataList = varProcessFunctionService.list(queryWrapper);
        Map<FunctionTypeEnum, List<VarProcessFunction>> groupedByFunctionTypeEnum = dataList.stream().collect(Collectors.groupingBy(VarProcessFunction::getFunctionType));
        List<VarProcessFunction> prepList = groupedByFunctionTypeEnum.getOrDefault(FunctionTypeEnum.PREP, Collections.emptyList());
        List<VarProcessFunction> templateList = groupedByFunctionTypeEnum.getOrDefault(FunctionTypeEnum.TEMPLATE, Collections.emptyList());

        OverViewQuantityAndStatusOutputVo overViewQuantityAndStatusVo = OverViewQuantityAndStatusOutputVo.builder()
                .prepAllNumber(prepList.size())
                .templateAllNumber(templateList.size())
                .build();
        return overViewQuantityAndStatusVo;

    }

    /**
     * 获取变量模板的所有数量
     *
     * @return 变量模板的所有数量
     */
    public OverViewQuantityAndStatusVo getTemplateQuantityAndStatus() {
        QueryWrapper<VarProcessFunction> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id");
        queryWrapper.eq("delete_flag", MagicNumbers.ONE);
        queryWrapper.eq("function_type", "TEMPLATE");
        List<VarProcessFunction> dataList = varProcessFunctionService.list(queryWrapper);
        int allNumber = dataList.size();
        OverViewQuantityAndStatusVo overViewQuantityAndStatusVo = OverViewQuantityAndStatusVo.builder()
                .allNumber(allNumber)
                .build();
        return overViewQuantityAndStatusVo;

    }

    /**
     * 获取实时服务的调用量和平均响应时间
     *
     * @param timeEnum 时间限制的枚举
     * @return 睿信概览统计数量与状态
     */
    public List<OverViewNameAndQuantityOutputVo> getServiceCallAndAvgResponse(CallVolumeByTimeEnum timeEnum) {
        // 1.先获取处于启用+停用状态的实时服务名称
        List<VarProcessServiceStateEnum> states = Arrays.asList(VarProcessServiceStateEnum.ENABLED, VarProcessServiceStateEnum.DISABLED);

        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        List<ServiceInfoDto> serviceInfos = varProcessServiceVersionService.findServiceListByState(states, roleDataAuthority.getDeptCodes(), roleDataAuthority.getUserNames());
        Set<String> allServices = serviceInfos.stream().map(ServiceInfoDto::getName).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(allServices)) {
            return new ArrayList<>();
        }
        // 2.生成开始时间和结束时间
        StartEndTimeDto startEndTime = getStartEndTime(timeEnum, LocalDateTime.now().toLocalDate());
        // 3.求出在var_process_log表中出现过的实时服务名称
        StringBuilder existNames = new StringBuilder();
        existNames.append("SELECT DISTINCT service_name FROM ").append(VAR_PROCESS_LOG)
                .append(" WHERE interface_type = 1 AND request_time between '").append(startEndTime.getStartTime()).append("'")
                .append(" AND '").append(startEndTime.getEndTime()).append("'")
                .append(" AND service_name IN (");
        int count = 0;
        for (String name : allServices) {
            existNames.append("'").append(name).append("'");
            count++;
            if (count != allServices.size()) {
                existNames.append(", ");
            }
        }
        existNames.append(") GROUP BY service_name");
        // 4.调用sql, 获取有调用记录的实时服务的名称
        Set<String> existServices = new HashSet<>(dbOperateService.queryForList(existNames.toString(), String.class));
        if (CollectionUtils.isEmpty(existServices)) {
            return new ArrayList<>();
        }
        // 5.求两者之间的差集，即为空的那一部分
        allServices.removeAll(existServices);
        // 6.另起一个SQL语句
        StringBuilder sql = new StringBuilder();
            sql.append("SELECT service_name, count(*) AS quantity, round(avg(response_long_time),0) AS responseLongTime FROM ").append(VAR_PROCESS_LOG)
                .append(" WHERE interface_type = 1 AND request_time between '").append(startEndTime.getStartTime()).append("'")
                .append(" AND '").append(startEndTime.getEndTime()).append("'")
                .append(" AND service_name IN (");
        count = 0;
        for (String name : existServices) {
            sql.append("'").append(name).append("'");
            count++;
            if (count != existServices.size()) {
                sql.append(", ");
            }
        }
        sql.append(") GROUP BY service_name");
        // 7.执行这个SQL语句
        List<Map<String, Object>> maps = dbOperateService.queryForList(sql.toString());
        // 8.组装结果集
        List<OverViewNameAndQuantityOutputVo> result = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            OverViewNameAndQuantityOutputVo item = new OverViewNameAndQuantityOutputVo();
            item.setServiceName(map.get("service_name").toString());
            item.setServiceCallQuantity(Long.valueOf(map.get("quantity").toString()));
            item.setServiceResponseLongTime(new BigDecimal(map.get("responseLongTime").toString()));
            result.add(item);
        }
        return result;
    }

    private StartEndTimeDto getStartEndTime(CallVolumeByTimeEnum timeEnum, LocalDate nowDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StartEndTimeDto outputDto = new StartEndTimeDto();
        String startTime = "";
        String endTime = "";
        switch (timeEnum) {
            case TODAY:
                startTime = nowDate.atTime(0, 0, 0).format(formatter);
                endTime = nowDate.atTime(MagicNumbers.INT_23, MagicNumbers.INT_59, MagicNumbers.INT_59).format(formatter);
                break;
            case YESTERDAY:
                startTime = nowDate.minusDays(1).atTime(0, 0, 0).format(formatter);
                endTime = nowDate.minusDays(1).atTime(MagicNumbers.INT_23, MagicNumbers.INT_59, MagicNumbers.INT_59).format(formatter);
                break;
            case LAST_SEVEN_DAYS:
                //                最近七天实际是减6天
                startTime = nowDate.minusDays(MagicNumbers.SIX).atTime(0, 0, 0).format(formatter);
                endTime = nowDate.atTime(MagicNumbers.INT_23, MagicNumbers.INT_59, MagicNumbers.INT_59).format(formatter);
                break;
            case LAST_THIRTY_DAYS:
                //                最近七天实际是减29天
                startTime = nowDate.minusDays(MagicNumbers.INT_29).atTime(0, 0, 0).format(formatter);
                endTime = nowDate.atTime(MagicNumbers.INT_23, MagicNumbers.INT_59, MagicNumbers.INT_59).format(formatter);
                break;
            case LAST_TRIMESTER:
                startTime = nowDate.minusMonths(MagicNumbers.TWO).atTime(0, 0, 0).format(formatter);
                endTime = nowDate.atTime(MagicNumbers.INT_23, MagicNumbers.INT_59, MagicNumbers.INT_59).format(formatter);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "Unexpected value: " + Objects.requireNonNull(timeEnum));
        }

        outputDto.setStartTime(startTime);
        outputDto.setEndTime(endTime);

        return outputDto;
    }

    /**
     * 获取psi统计
     *
     * @param order 排序字段
     * @return psi统计
     */
    public List<OverviewTargetRankingOutputVo> getOverviewTargetPsiRanking(String order) {

        SortFieldSplittingDto sortFieldSplittingDto = sortFieldSplitting(order, MAX, DESC);
        String sortedKey = sortFieldSplittingDto.getSortKey();
        String sortMethod = sortFieldSplittingDto.getSortType();

        //查询统计配置信息
        List<Long> accessStatisticsConfig = varProcessStatisticsConfigService.findAccessConfig();
        List<Long> accessBacktrackingStatisticsConfig = backtrackingStatisticsConfigService.findAccessConfig();

        List<OverviewTargetStatisticsDto> backtrackingList = backtrackingOverallService.getOverviewTargetPsi(accessBacktrackingStatisticsConfig);
        List<OverviewTargetStatisticsDto> realTimeServiceList = statisticsOverallService.getOverviewTargetPsi(accessStatisticsConfig);
        backtrackingList.addAll(realTimeServiceList);

        List<OverviewTargetRankingOutputVo> outputVoList = getMinMaxList(backtrackingList);

        //排序
        List<OverviewTargetRankingOutputVo> topFiveList = sortPsiIvMr(outputVoList, sortedKey, sortMethod);

        for (OverviewTargetRankingOutputVo dto : topFiveList) {
            dto.setMax(Double.valueOf(new DecimalFormat("#,##0.000").format(dto.getMax())));
            dto.setMin(Double.valueOf(new DecimalFormat("#,##0.000").format(dto.getMin())));
        }

        return topFiveList;
    }

    /**
     * 获取iv统计
     *
     * @param order 排序字段
     * @return iv统计
     */
    public List<OverviewTargetRankingOutputVo> getOverviewTargetIvRanking(String order) {
        SortFieldSplittingDto sortFieldSplittingDto = sortFieldSplitting(order, MAX, DESC);
        String sortedKey = sortFieldSplittingDto.getSortKey();
        String sortMethod = sortFieldSplittingDto.getSortType();

        //查询统计配置信息
        List<Long> accessStatisticsConfig = varProcessStatisticsConfigService.findAccessConfig();
        List<Long> accessBacktrackingStatisticsConfig = backtrackingStatisticsConfigService.findAccessConfig();

        List<OverviewTargetStatisticsDto> backtrackingList = backtrackingOverallService.getOverviewTargetIv(accessBacktrackingStatisticsConfig);
        List<OverviewTargetStatisticsDto> realTimeServiceList = statisticsOverallService.getOverviewTargetIv(accessStatisticsConfig);
        backtrackingList.addAll(realTimeServiceList);

        List<OverviewTargetRankingOutputVo> outputVoList = getMinMaxList(backtrackingList);

        //排序
        List<OverviewTargetRankingOutputVo> topFiveList = sortPsiIvMr(outputVoList, sortedKey, sortMethod);

        for (OverviewTargetRankingOutputVo dto : topFiveList) {
            dto.setMax(Double.valueOf(new DecimalFormat("#,##0.000").format(dto.getMax())));
            dto.setMin(Double.valueOf(new DecimalFormat("#,##0.000").format(dto.getMin())));
        }


        return topFiveList;
    }

    /**
     * 获取缺失值统计
     *
     * @param order 排序字段
     * @return 缺失值统计
     */
    public List<OverviewTargetRankingOutputVo> getOverviewTargetMrRanking(String order) {
        SortFieldSplittingDto sortFieldSplittingDto = sortFieldSplitting(order, MAX, DESC);
        String sortedKey = sortFieldSplittingDto.getSortKey();
        String sortMethod = sortFieldSplittingDto.getSortType();

        //查询统计配置信息
        List<Long> accessStatisticsConfig = varProcessStatisticsConfigService.findAccessConfig();
        List<Long> accessBacktrackingStatisticsConfig = backtrackingStatisticsConfigService.findAccessConfig();

        List<OverviewTargetStatisticsDto> backtrackingList = backtrackingOverallService.getOverviewTargetMr(accessBacktrackingStatisticsConfig);
        List<OverviewTargetStatisticsDto> realTimeServiceList = statisticsOverallService.getOverviewTargetMr(accessStatisticsConfig);
        backtrackingList.addAll(realTimeServiceList);

        List<OverviewTargetRankingOutputVo> outputVoList = getMinMaxList(backtrackingList);

        //排序
        List<OverviewTargetRankingOutputVo> topFiveList = sortPsiIvMr(outputVoList, sortedKey, sortMethod);

        for (OverviewTargetRankingOutputVo dto : topFiveList) {
            dto.setMax(Double.valueOf(new DecimalFormat("#,##0.00").format(dto.getMax())));
            dto.setMin(Double.valueOf(new DecimalFormat("#,##0.00").format(dto.getMin())));
        }

        return topFiveList;
    }

    /**
     * 将变量分组获取最大值与最小值
     *
     * @param backtrackingList 数据
     * @return 分组后的最大值最小值
     */
    public List<OverviewTargetRankingOutputVo> getMinMaxList(List<OverviewTargetStatisticsDto> backtrackingList) {

        List<OverviewTargetRankingOutputVo> outputVoList = new ArrayList<>();
        Map<String, List<Double>> groupedValues = backtrackingList.stream()
                .collect(Collectors.groupingBy(OverviewTargetStatisticsDto::getName,
                        Collectors.mapping(OverviewTargetStatisticsDto::getTarget, Collectors.toList())));
        for (Map.Entry<String, List<Double>> entry : groupedValues.entrySet()) {
            OverviewTargetRankingOutputVo outputVo = new OverviewTargetRankingOutputVo();
            outputVo.setVariableName(entry.getKey());
            outputVo.setMax(Collections.max(entry.getValue()));
            outputVo.setMin(Collections.min(entry.getValue()));
            outputVoList.add(outputVo);
        }

        return outputVoList;
    }

    /**
     * 排序
     *
     * @param outputVoList 数据
     * @param sortedKey    排序字段
     * @param sortMethod   排序方法
     * @return 排序后的数据
     */
    public List<OverviewTargetRankingOutputVo> sortPsiIvMr(List<OverviewTargetRankingOutputVo> outputVoList, String sortedKey, String sortMethod) {
        //排序
        List<OverviewTargetRankingOutputVo> sortCollect = new ArrayList<>();
        //排序规则
        //最大值降序
        if (MAX.equals(sortedKey) && DESC.equals(sortMethod)) {
            sortCollect = outputVoList.stream()
                    .sorted(Comparator.comparing(OverviewTargetRankingOutputVo::getMax).reversed())
                    .collect(Collectors.toList());
        } else if (MAX.equals(sortedKey) && ASC.equals(sortMethod)) {
            //最大值升序
            sortCollect = outputVoList.stream()
                    .sorted(Comparator.comparing(OverviewTargetRankingOutputVo::getMax))
                    .collect(Collectors.toList());
        } else if (MIN.equals(sortedKey) && DESC.equals(sortMethod)) {
            //最小值降序
            sortCollect = outputVoList.stream()
                    .sorted(Comparator.comparing(OverviewTargetRankingOutputVo::getMin).reversed())
                    .collect(Collectors.toList());
        } else {
            //最小值升序
            sortCollect = outputVoList.stream()
                    .sorted(Comparator.comparing(OverviewTargetRankingOutputVo::getMin))
                    .collect(Collectors.toList());
        }

        List<OverviewTargetRankingOutputVo> topFiveList = sortCollect.subList(MagicNumbers.ZERO, Math.min(MagicNumbers.TWENTY, sortCollect.size()));

        return topFiveList;
    }

    /**
     * 分割排序字段
     *
     * @param order             排序字段
     * @param defaultSortedKey  默认排序字段
     * @param defaultSortMethod 默认排序方法
     * @return 默认排序字段和默认排序方法
     */
    public SortFieldSplittingDto sortFieldSplitting(String order, String defaultSortedKey, String defaultSortMethod) {
        String sortedKey;
        String sortMethod;
        if (StringUtils.isEmpty(order)) {
            sortedKey = defaultSortedKey;
            sortMethod = defaultSortMethod;
        } else {
            sortMethod = order.substring(order.indexOf("_") + 1);
            sortedKey = order.substring(0, order.indexOf("_"));
        }
        SortFieldSplittingDto sortFieldSplittingDto = new SortFieldSplittingDto();
        sortFieldSplittingDto.setSortKey(sortedKey);
        sortFieldSplittingDto.setSortType(sortMethod);

        return sortFieldSplittingDto;
    }

}
