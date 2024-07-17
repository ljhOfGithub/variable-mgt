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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.AnalysisIndexMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigPsiMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigSpecialMappingVo;
import com.wiseco.var.process.app.server.controller.vo.PercentageMappingVo;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingOverallInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingStatisticsReferenceValueQueryVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingConfigDetailOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingStatisticsResultPageOutputVO;
import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingStatisticsConfig;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wiseco.var.process.app.server.service.BacktrackingStatisticsResultService;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.VarProcessBatchBacktrackingStatisticsConfigService;
import com.wiseco.var.process.app.server.service.dto.BacktrackingTaskDto;
import com.wiseco.var.process.app.server.service.dto.OverviewTargetStatisticsDto;
import com.wiseco.var.process.app.server.service.statistics.StatisticsOverallService;
import com.wiseco.var.process.app.server.statistics.template.OverallBacktrackingStatistics;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BacktrackingOverallService {

    static DecimalFormat df = new DecimalFormat("0.00%");
    @Resource
    private BacktrackingTaskService backtrackingTaskService;
    @Resource
    private BacktrackingService backtrackingService;
    @Resource
    private VarProcessBatchBacktrackingStatisticsConfigService statisticsConfigService;
    @Resource
    private BacktrackingStatisticsResultService resultService;
    @Resource
    private StatisticsOverallService overallService;
    @Resource
    private OverallBacktrackingStatistics overallBacktrackingStatistics;
    @Resource
    private DbOperateService dbOperateService;
    @Resource
    private BacktrackingOverallService backtrackingOverallService;

    private static final String BACKTRACT_CODE = "batch_no";

    private static final String VAR_PROCESS_MANIFEST_HEADER = "var_process_manifest_header";

    /**
     * getColumns
     *
     * @param backtrackingId 批量回溯Id
     * @return java.util.List
     */
    public List<String> getColumns(Long backtrackingId) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getManifestId)
                .eq(VarProcessBatchBacktracking::getId, backtrackingId));
        if (backtracking == null) {
            return new ArrayList<>();
        }

        String tableName = getTableName(backtrackingId);
        if (!dbOperateService.isTableExist(tableName)) {
            return new ArrayList<>();
        }

        //查询索引列
        String condition = "manifest_id = " + backtracking.getManifestId() + " and is_index = 1";
        List<Map<String, Object>> indexColum = dbOperateService.queryForList(
                VAR_PROCESS_MANIFEST_HEADER, Collections.singletonList("variable_code"), condition, null, null, null);

        return indexColum.stream().map(item -> String.valueOf(item.get("variable_code"))).collect(Collectors.toList());
    }

    /**
     * getUnicode
     *
     * @param queryVo 批量回溯查询参数
     * @return java.util.List
     */
    public List<Object> getInternalDataUnicode(BacktrackingStatisticsReferenceValueQueryVO queryVo) {
        String tableName;
        String whereCondition;
        List<Object> columnDataDistinct;
        if (StringUtils.isEmpty(queryVo.getInternalTableName())) {
            tableName = getTableName(queryVo.getBacktrackingId());
            whereCondition = generateWhereCondition(queryVo.getBacktrackingId(), queryVo.getBatchNumberList());
            final VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery().select(VarProcessBatchBacktracking::getManifestId).eq(VarProcessBatchBacktracking::getId, queryVo.getBacktrackingId()));
            columnDataDistinct = dbOperateService.getColumnDataDistinct(tableName, queryVo.getIndexName(), whereCondition, backtracking.getManifestId());
        } else {
            tableName = queryVo.getInternalTableName();
            whereCondition = "1=1";
            columnDataDistinct = dbOperateService.getColumnDataDistinct(tableName, queryVo.getIndexName(), whereCondition);
        }
        List<Object> backtrackingUnicodeList = columnDataDistinct.stream().map(String::valueOf).collect(Collectors.toList());
        if (backtrackingUnicodeList.size() > MagicNumbers.INT_50) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_ANALYSE, "该字段唯一值个数超过50个，不能选择");
        }
        return backtrackingUnicodeList;
    }

    /**
     * submit
     *
     * @param inputVO 输入实体类对象
     * @return 新增回溯统计配置的id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long submit(BacktrackingOverallInputVO inputVO) {
        //参数校验
        validParam(inputVO, true);

        StringBuilder batchNumberSb = new StringBuilder();
        for (String batchNumber : inputVO.getBatchNumberList()) {
            batchNumberSb.append("'").append(batchNumber).append("'").append(",");
        }

        VarProcessBatchBacktrackingStatisticsConfig statisticsConfig = VarProcessBatchBacktrackingStatisticsConfig
                .builder()
                .batchNumber(batchNumberSb.substring(0, batchNumberSb.lastIndexOf(",")))
                .backtrackingId(inputVO.getBacktrackingId())
                .analysisIndex(JSON.toJSONString(inputVO.getIndexMappingVo()))
                .ivConfig(inputVO.getIvMappingVo() == null ? null : JSON.toJSONString(inputVO.getIvMappingVo()))
                .psiConfig(inputVO.getPsiMappingVo() == null ? null : JSON.toJSONString(inputVO.getPsiMappingVo()))
                .specialValConfig(CollUtil.isEmpty(inputVO.getSpecialMappingVoList()) ? null : JSON.toJSONString(inputVO.getSpecialMappingVoList()))
                .createdUser(SessionContext.getSessionUser().getUsername())
                .updatedUser(SessionContext.getSessionUser().getUsername())
                .build();

        if (!statisticsConfigService.save(statisticsConfig)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_DATABASE_ERROR, "新增回溯统计配置失败!");
        }

        //计算
        overallBacktrackingStatistics.calculateHandler(inputVO.getBacktrackingId());

        return statisticsConfig.getId();
    }

    /**
     * validParam
     *
     * @param inputVO 输入实体类对象
     * @param isAdd   是否添加
     */
    private void validParam(BacktrackingOverallInputVO inputVO, boolean isAdd) {
        if (inputVO == null || inputVO.getIndexMappingVo() == null || inputVO.getBacktrackingId() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "新增或编辑参数必填项不允许为空！");
        }
        if (CollectionUtils.isEmpty(inputVO.getBatchNumberList())) {
            // 手动需要自动找批次号
            final List<String> batchNoList = backtrackingOverallService.getBatchNo(inputVO.getBacktrackingId());
            if (CollectionUtils.isEmpty(batchNoList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_ANALYSE, "无成功的批次！");
            }
            inputVO.setBatchNumberList(batchNoList);
        }

        if (isAdd) {
            List<VarProcessBatchBacktrackingStatisticsConfig> statisticsConfigList = statisticsConfigService.list(Wrappers.<VarProcessBatchBacktrackingStatisticsConfig>lambdaQuery()
                    .select(VarProcessBatchBacktrackingStatisticsConfig::getId)
                    .eq(VarProcessBatchBacktrackingStatisticsConfig::getBacktrackingId, inputVO.getBacktrackingId()));
            if (CollectionUtil.isNotEmpty(statisticsConfigList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_ANALYSE, "该回溯id已存在相关配置，请勿重复添加！");
            }
        }

        if (!inputVO.getIndexMappingVo().isIv() && !inputVO.getIndexMappingVo().isPsi() && !inputVO.getIndexMappingVo().isZeroRatio() && !inputVO.getIndexMappingVo().isUniqueNum() && !inputVO.getIndexMappingVo().isPercentage() && !inputVO.getIndexMappingVo().isMissingRatio() && !inputVO.getIndexMappingVo().isSpecialRatio()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "未选中需要计算的指标！");
        }


        if (inputVO.getIndexMappingVo() != null && inputVO.getIndexMappingVo().isIv() && inputVO.getIvMappingVo() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "iv参数设置不允许为空！");
        }

        if (inputVO.getIndexMappingVo() != null && inputVO.getIndexMappingVo().isPsi() && inputVO.getPsiMappingVo() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "psi参数设置不允许为空！");
        }

        if (inputVO.getIndexMappingVo() != null && inputVO.getIndexMappingVo().isSpecialRatio() && CollectionUtil.isEmpty(inputVO.getSpecialMappingVoList())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "特殊值设置参数设置不允许为空！");
        }

        if (CollectionUtil.isNotEmpty(inputVO.getSpecialMappingVoList()) && inputVO.getSpecialMappingVoList().size() > MagicNumbers.ONE) {
            List<ConfigSpecialMappingVo> list = inputVO.getSpecialMappingVoList().stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> e.getDataType().getDesc() + e.getSpecialVal()))), ArrayList::new));
            if (list.size() != inputVO.getSpecialMappingVoList().size()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "特殊值设置不允许重复设置！");
            }
        }

    }

    /**
     * editConfig
     *
     * @param inputVO 输入实体类对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void editConfig(BacktrackingOverallInputVO inputVO) {
        //参数校验
        validParam(inputVO, false);

        List<VarProcessBatchBacktrackingStatisticsConfig> statisticsConfigList = statisticsConfigService.list(Wrappers.<VarProcessBatchBacktrackingStatisticsConfig>lambdaQuery()
                .select(VarProcessBatchBacktrackingStatisticsConfig::getId)
                .eq(VarProcessBatchBacktrackingStatisticsConfig::getBacktrackingId, inputVO.getBacktrackingId()));
        if (CollectionUtil.isEmpty(statisticsConfigList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_ANALYSE, "未找到该批次号对应的相关配置！");
        }

        if (statisticsConfigList.size() > 1) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_ANALYSE, "该批次号统计配置存在多个！");
        }

        VarProcessBatchBacktrackingStatisticsConfig statisticsConfig = VarProcessBatchBacktrackingStatisticsConfig
                .builder()
//                .batchNumber(buffer.substring(0, buffer.lastIndexOf(",")))
                .id(statisticsConfigList.get(0).getId())
                .analysisIndex(JSON.toJSONString(inputVO.getIndexMappingVo()))
                .ivConfig(inputVO.getIvMappingVo() == null ? null : JSON.toJSONString(inputVO.getIvMappingVo()))
                .psiConfig(inputVO.getPsiMappingVo() == null ? null : JSON.toJSONString(inputVO.getPsiMappingVo()))
                .specialValConfig(CollUtil.isEmpty(inputVO.getSpecialMappingVoList()) ? null : JSON.toJSONString(inputVO.getSpecialMappingVoList()))
                .updatedUser(SessionContext.getSessionUser().getUsername())
                .build();

        //如果是手动任务，更新最新批次号
        VarProcessBatchBacktracking backtracking = backtrackingService.getById(inputVO.getBacktrackingId());
        if (backtracking.getTriggerType() == BatchBacktrackingTriggerTypeEnum.MANUAL) {
            VarProcessBatchBacktrackingTask task = backtrackingTaskService.getOne(new LambdaQueryWrapper<VarProcessBatchBacktrackingTask>()
                    .eq(VarProcessBatchBacktrackingTask::getBacktrackingId, inputVO.getBacktrackingId()).select(VarProcessBatchBacktrackingTask::getCode));
            statisticsConfig.setBatchNumber("'" + task.getCode() + "'");
        }
        statisticsConfigService.updateById(statisticsConfig);

        //计算
        overallBacktrackingStatistics.calculateHandler(inputVO.getBacktrackingId());

    }

    /**
     * getTableName
     *
     * @param backtrackingId
     * @return java.lang.String
     */
    private String getTableName(Long backtrackingId) {
        final VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery().select(VarProcessBatchBacktracking::getResultTable).eq(VarProcessBatchBacktracking::getId, backtrackingId));
        return backtracking.getResultTable();
    }

    /**
     * getConfigDetail
     *
     * @param backtrackingId 批量回溯Id
     * @return 批量回溯返回给前端的配置详情实体类
     */
    public BacktrackingConfigDetailOutputVO getConfigDetail(Long backtrackingId) {
        if (backtrackingId == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "获取配置详情参数backtrackingId不允许为空!");
        }
        BacktrackingConfigDetailOutputVO outputVO = new BacktrackingConfigDetailOutputVO();

        List<VarProcessBatchBacktrackingStatisticsConfig> statisticsConfigList = statisticsConfigService.list(Wrappers.<VarProcessBatchBacktrackingStatisticsConfig>lambdaQuery().eq(VarProcessBatchBacktrackingStatisticsConfig::getBacktrackingId, backtrackingId));

        if (CollUtil.isEmpty(statisticsConfigList)) {
            return null;
        }

        if (statisticsConfigList.size() > 1) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_ANALYSE, "该backtrackingId相关统计配置存在多个！");
        }

        VarProcessBatchBacktrackingStatisticsConfig config = statisticsConfigList.get(0);
        if (StringUtils.isNotEmpty(config.getBatchNumber())) {
            List<String> list = new ArrayList<>();
            Arrays.asList(config.getBatchNumber().split(",")).forEach(e -> list.add(e.substring(1, e.lastIndexOf("'"))));
            outputVO.setBatchNumberList(list);
        }
        outputVO.setBatchNumber(config.getBatchNumber());
        outputVO.setBacktrackingConfigId(config.getId());
        outputVO.setBacktrackingId(backtrackingId);

        if (StringUtils.isNotEmpty(config.getAnalysisIndex())) {
            AnalysisIndexMappingVo indexMappingVo = JSON.parseObject(config.getAnalysisIndex(), AnalysisIndexMappingVo.class);
            outputVO.setIndexMappingVo(indexMappingVo);
        }

        if (StringUtils.isNotEmpty(config.getIvConfig())) {
            ConfigIvMappingVo ivMappingVo = JSON.parseObject(config.getIvConfig(), ConfigIvMappingVo.class);
            outputVO.setIvMappingVo(ivMappingVo);
        }

        if (StringUtils.isNotEmpty(config.getPsiConfig())) {
            ConfigPsiMappingVo psiMappingVo = JSON.parseObject(config.getPsiConfig(), ConfigPsiMappingVo.class);
            outputVO.setPsiMappingVo(psiMappingVo);
        }

        if (StringUtils.isNotEmpty(config.getSpecialValConfig())) {
            List<ConfigSpecialMappingVo> specialMappingVoList = JSON.parseArray(config.getSpecialValConfig(), ConfigSpecialMappingVo.class);
            outputVO.setSpecialMappingVoList(specialMappingVoList);
        }

        return outputVO;
    }

    /**
     * getResultPage
     *
     * @param inputVO 输入实体类对象
     * @return 批量回溯分页查询结果 VO
     */
    public BacktrackingStatisticsResultPageOutputVO getResultPage(BacktrackingStatisticsResultQueryVO inputVO) {

        BacktrackingStatisticsResultPageOutputVO outputVO = new BacktrackingStatisticsResultPageOutputVO();

        if (inputVO.getBacktrackingId() == null) {
            return outputVO;
        }

        //获取表头
        outputVO.setHeadList(getHeadList(inputVO));

        String order = inputVO.getOrder();
        if (StringUtils.isEmpty(inputVO.getOrder())) {
            inputVO.setSortKey("bsr.var_name");
            inputVO.setSortType("ASC");
        } else {
            String sortType = order.substring(order.lastIndexOf("_") + 1);
            String sortKey = order.substring(0, order.lastIndexOf("_"));
            inputVO.setSortKey("bsr." + overallService.getSortKey(sortKey));
            inputVO.setSortType(sortType);
        }

        // 分页设置
        IPage<Map<String, Object>> pageList = null;
        try {
            pageList = resultService.getPageList(new Page<>(inputVO.getCurrentNo(), inputVO.getSize()), inputVO);
        } catch (Exception e) {
            log.error("批量回溯整体分析查询数据异常", e);
        }

        if (pageList == null || CollectionUtils.isEmpty(pageList.getRecords())) {
            return outputVO;
        }

        for (Map<String, Object> map : pageList.getRecords()) {
            if (map.get("缺失值占比") != null) {
                map.put("缺失值占比", df.format(map.get("缺失值占比")));
            }
            if (map.get("特殊值占比") != null) {
                map.put("特殊值占比", df.format(map.get("特殊值占比")));
            }
            if (map.get("零值占比") != null) {
                map.put("零值占比", df.format(map.get("零值占比")));
            }

            if (map.get("percentageResult") == null) {
                continue;
            }
            PercentageMappingVo percentageMappingVo = JSONObject.parseObject((String) map.get("percentageResult"), PercentageMappingVo.class);
            map.put("1%", percentageMappingVo.getPercentage1());
            map.put("5%", percentageMappingVo.getPercentage5());
            map.put("25%", percentageMappingVo.getPercentage25());
            map.put("50%", percentageMappingVo.getPercentage50());
            map.put("75%", percentageMappingVo.getPercentage75());
            map.put("95%", percentageMappingVo.getPercentage95());
        }

        List<Map<String, Object>> data = new ArrayList<>();

        for (Map<String, Object> record : pageList.getRecords()) {
            Map<String, Object> map = new LinkedHashMap<>();
            record.forEach(((key, value) -> {
                map.put(key, value == null ? null : value.toString());
            }));
            data.add(map);
        }

        pageList.setRecords(data);
        outputVO.setPage(pageList);
        return outputVO;
    }

    /**
     * getHeadList
     *
     * @param inputVO 入参
     * @return List
     */
    private List<String> getHeadList(BacktrackingStatisticsResultQueryVO inputVO) {
        List<String> headList = new ArrayList<>();
        headList.add("变量名称");
        headList.add("数据类型");

        List<VarProcessBatchBacktrackingStatisticsConfig> statisticsConfigList = statisticsConfigService.list(Wrappers.<VarProcessBatchBacktrackingStatisticsConfig>lambdaQuery()
                .select(VarProcessBatchBacktrackingStatisticsConfig::getAnalysisIndex)
                .eq(VarProcessBatchBacktrackingStatisticsConfig::getBacktrackingId, inputVO.getBacktrackingId()));
        if (CollectionUtil.isEmpty(statisticsConfigList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_ANALYSE, "分析指标配置有误!");
        }

        VarProcessBatchBacktrackingStatisticsConfig config = statisticsConfigList.get(0);
        if (config.getAnalysisIndex() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.BACK_TRACKING_ANALYSE, "分析指标配置有误!");
        }
        AnalysisIndexMappingVo indexMappingVo = JSONObject.parseObject(config.getAnalysisIndex(), AnalysisIndexMappingVo.class);

        if (indexMappingVo.isIv()) {
            headList.add("IV");
        }

        if (indexMappingVo.isPsi()) {
            headList.add("PSI");
        }

        if (indexMappingVo.isMissingRatio()) {
            headList.add("缺失值占比");
        }

        if (indexMappingVo.isSpecialRatio()) {
            headList.add("特殊值占比");
        }

        headList.add("唯一值数量");

        if (indexMappingVo.isZeroRatio()) {
            headList.add("零值占比");
        }
        headList.add("最小值");
        headList.add("最大值");
        headList.add("均值");

        if (indexMappingVo.isPercentage()) {
            headList.add("1%");
            headList.add("5%");
            headList.add("25%");
            headList.add("50%");
            headList.add("75%");
            headList.add("95%");
        }

        return headList;
    }

    /**
     * calculateIndex
     *
     * @param backtrackingId 批量回溯Id
     */
    @Transactional(rollbackFor = Exception.class)
    public void calculateIndex(Long backtrackingId) {
        overallBacktrackingStatistics.calculateHandler(backtrackingId);
    }

    /**
     * getBatchNo
     *
     * @param backtrackingId 文件名
     * @return java.util.List
     */
    public List<String> getBatchNo(Long backtrackingId) {
        List<BacktrackingTaskDto> list = backtrackingTaskService.getBacktrackingTaskByBacktrackingId(backtrackingId);

        List<String> list1 = new ArrayList<>();
        for (BacktrackingTaskDto e : list) {
            if (e.getStatus() != null) {
                if (e.getStatus().equals(BacktrackingTaskStatusEnum.SUCCESS) || e.getStatus().equals(BacktrackingTaskStatusEnum.FILE_GENERATING)) {
                    if (!list1.contains(e.getCode())) {
                        list1.add(e.getCode());

                    }
                }

            }
        }
        return list1;
    }

    /**
     * export
     *
     * @param inputVO  输入实体类对象
     * @param response response对象
     */
    public void export(BacktrackingStatisticsResultQueryVO inputVO, HttpServletResponse response) {
        BacktrackingStatisticsResultPageOutputVO backtrackingStatisticsResultPageOutputVO = getResultPage(inputVO);

        if (backtrackingStatisticsResultPageOutputVO == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_DATA_ERROR, "未获取到数据！");
        }

        List<String> headList = backtrackingStatisticsResultPageOutputVO.getHeadList();
        //插入数据
        List<Map<String, Object>> dataMapList = backtrackingStatisticsResultPageOutputVO.getPage().getRecords();

        overallService.executeExport(headList, dataMapList, response);
    }

    /**
     * 生成where条件
     *
     * @param backtrackingId        批量回溯任务id
     * @param backtrackingTaskCodes 批量回溯任务下的批次号
     * @return where条件
     */
    private String generateWhereCondition(Long backtrackingId, List<String> backtrackingTaskCodes) {
        StringBuilder sb = new StringBuilder();

        // 拼装where条件
        sb.append(" backtracking_id = ").append(backtrackingId);
        boolean flag = backtrackingTaskCodes != null && !backtrackingTaskCodes.isEmpty();
        if (flag) {
            sb.append(" AND ");
            String result = backtrackingTaskCodes.stream()
                    .map(s -> "'" + s + "'")
                    .collect(Collectors.joining(", "));
            String finalResult = "(" + result + ")";
            sb.append(BACKTRACT_CODE).append(" IN ").append(finalResult);
        }
        return sb.toString();
    }

    /**
     * 获取PSI统计
     *
     * @param configIds 配置id
     * @return PSI统计
     */
     public List<OverviewTargetStatisticsDto> getOverviewTargetPsi(List<Long> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return new ArrayList<>();
        }
        return resultService.getOverviewTargetPsi(configIds);
    }

    /**
     * 获取IV统计
     *
     * @param configIds 配置id
     * @return IV统计
     */
    public List<OverviewTargetStatisticsDto> getOverviewTargetIv(List<Long> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return new ArrayList<>();
        }
        return resultService.getOverviewTargetIv(configIds);
    }

    /**
     * 获取缺失值统计
     *
     * @param configIds 配置id
     * @return 缺失值统计
     */
    public List<OverviewTargetStatisticsDto> getOverviewTargetMr(List<Long> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return new ArrayList<>();
        }
        return resultService.getOverviewTargetMr(configIds);
    }


}
