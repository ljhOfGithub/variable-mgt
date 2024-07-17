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
package com.wiseco.var.process.app.server.service.statistics;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.boot.commons.util.DateTimeUtils;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.web.util.ExcelExportUtils;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.controller.vo.AnalysisIndexMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigPsiMappingVo;
import com.wiseco.var.process.app.server.controller.vo.ConfigSpecialMappingVo;
import com.wiseco.var.process.app.server.controller.vo.PercentageMappingVo;
import com.wiseco.var.process.app.server.controller.vo.input.StatisticsConfigCreationInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.StatisticsReferenceFromMonitoringValueInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.StatisticsReferenceValueInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessStatisticsResultQueryVO;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingStatisticsResultPageOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.StatisticsConfigDetailOutputVO;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessServiceVersion;
import com.wiseco.var.process.app.server.repository.entity.VarProcessStatisticsConfig;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.VarProcessServiceVersionService;
import com.wiseco.var.process.app.server.service.VarProcessStatisticsConfigService;
import com.wiseco.var.process.app.server.service.VarProcessStatisticsResultService;
import com.wiseco.var.process.app.server.service.dto.OverviewTargetStatisticsDto;
import com.wiseco.var.process.app.server.statistics.template.OverallProcessStatistics;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatisticsOverallService {
    private static final String VAR_PROCESS_MANIFEST_HEADER = "var_process_manifest_header";
    private static final String CLICK_HOUSE_TABLE_PRIFIX = "var_process_manifest";
    private static final String AVERAGE_VAL = "均值";
    private static final String MAX_VAL = "最大值";
    private static final String MINIMUM_VAL = "最小值";
    private static final String UNIQUE_NUM = "唯一值数量";
    private static final String MISSING_RATIO = "缺失值占比";
    private static final String PSI_RESULT = "PSI";
    private static final String IV_RESULT = "IV";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static DecimalFormat df = new DecimalFormat("0.00%");
    @Resource
    VarProcessStatisticsConfigService statisticsConfigService;
    @Resource
    VarProcessStatisticsResultService resultService;
    @Resource
    OverallProcessStatistics overallProcessStatistics;
    @Resource
    private DbOperateService dbOperateService;
    @Autowired
    private VarProcessServiceVersionService varProcessServiceVersionService;

    /**
     * 获取变量清单发布指标信息
     *
     * @param varProcessManifestId 变量清单的Id
     * @return 指标名称列表
     */
    public List<String> getColumns(Long varProcessManifestId) {
        // 1.查看内部数据库中的var_process_manifest_id在不在
        String tableName = getTableName(varProcessManifestId);
        if (!dbOperateService.isTableExist(tableName)) {
            return new ArrayList<>();
        }

        //查询索引列
        String condition = "manifest_id = " + varProcessManifestId + " and is_index = 1";
        List<Map<String, Object>> indexColum = dbOperateService.queryForList(
                VAR_PROCESS_MANIFEST_HEADER, Collections.singletonList("variable_code"), condition, null, null, null);
        return indexColum.stream().map(item -> String.valueOf(item.get("variable_code"))).collect(Collectors.toList());
    }


    /**
     * 调用量统计-整体分析-分组分析字段下拉列表
     * 获取清单的列角色col_role的列表
     *
     * @param varProcessManifestId 变量清单的Id
     * @param varProcessManifestColRole 变量清单的列角色col_role
     * @return 分组变量名称的列表
     */
    public List<String> getGroupField(Long varProcessManifestId, String varProcessManifestColRole) {
        // 1.查看内部数据库中的var_process_manifest_id在不在
        String tableName = getTableName(varProcessManifestId);
        if (!dbOperateService.isTableExist(tableName)) {
            return new ArrayList<>();
        }

        // 2.查询分组字段
        String condition = String.format("manifest_id = " + varProcessManifestId + " and col_role = '%s'", varProcessManifestColRole);
        List<Map<String, Object>> indexColum = dbOperateService.queryForList(
                VAR_PROCESS_MANIFEST_HEADER, Collections.singletonList("variable_code"), condition, null, null, null);
        return indexColum.stream().map(item -> String.valueOf(item.get("variable_code"))).collect(Collectors.toList());
    }


    /**
     * 监控规则-IV参数设置-来源于实时服务选择good、bad标签值下拉列表
     *
     * @param serviceName          服务名称
     * @param serviceVersion       服务版本
     * @param varProcessManifestId 变量清单Id
     * @param fieldName            Y指标的名称(指标的名称)
     * @return 服务的唯一字段列表
     */
    public List<Object> getServiceUnicodeFromMonitoring(String serviceName, Integer serviceVersion, Long varProcessManifestId, String fieldName) {
        VarProcessServiceVersion varProcessService = varProcessServiceVersionService.findServiceByNameAndVersion(serviceName, serviceVersion);
        if (varProcessService == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_NOT_FOUND, "实时服务不存在");
        }
        return getServiceUnicode(varProcessService.getId(), varProcessManifestId, fieldName);
    }


    /**
     * 调用量统计-整体分析-(IV参数设置)来源于实时服务选择good、bad标签值下拉列表
     *
     * @param varProcessServiceId  实施服务id
     * @param varProcessManifestId 变量清单Id
     * @param fieldName            Y指标的名称(指标的名称)
     * @return 服务的唯一字段列表
     */
    public List<Object> getServiceUnicode(Long varProcessServiceId, Long varProcessManifestId, String fieldName) {
        // 1.拼接SQL语句(获取某个serviceId的独一无二的某个特定字段)
        String tableName = CLICK_HOUSE_TABLE_PRIFIX + "_" + varProcessManifestId.toString();
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("select distinct ").append(fieldName).append(" from ").append(tableName).append(" where service_id = ").append(varProcessServiceId.toString());
        List<List> list = dbOperateService.queryForList(stringBuffer.toString(), List.class);
        List<Object> serviceUnicodeList = list.stream().filter(e -> CollUtil.isNotEmpty(e) && e.get(0) != null).map(e -> (Object) e.get(0)).collect(Collectors.toList());
        // 2.字段唯一值大于50个，就报异常
        if (serviceUnicodeList.size() > MagicNumbers.INT_50) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "该字段唯一值个数超过50个，不能选择");
        }
        return serviceUnicodeList;
    }

    /**
     * 调用量统计-整体分析-(来源于内部数据表)选择good、bad标签值下拉列表
     *
     * @param fieldName Y指标的名称(内部数据表的字段)
     * @param tableName 内部数据表名
     * @return 内部数据表的唯一字段列表
     */
    public List<String> getInternalUnicode(String fieldName, String tableName) {
        // 1.拼接SQL语句(获取某个内部数据表的独一无二的某个特定字段)
        List<Object> list = dbOperateService.getColumnDataDistinct(tableName, fieldName, " 1=1 ");
        List<String> internalUnicodeList = list.stream().map(Object::toString).collect(Collectors.toList());
        // 2.字段唯一值大于50个，就报异常
        if (internalUnicodeList.size() > MagicNumbers.INT_50) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "该字段唯一值个数超过50个，不能选择");
        }
        return internalUnicodeList;
    }

    /**
     * 统计
     *
     * @return Object
     */
    public Object statistics() {
        return null;
    }

    /**
     * 新增整体分析配置
     *
     * @param inputVO 输入实体类对象
     * @return ID 新配置统计对象的Id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addStatisticsConfigHandler(StatisticsConfigCreationInputVO inputVO) {
        // 1.参数校验
        validParam(inputVO, true);
        // 2.组装入参对象
        VarProcessStatisticsConfig statisticsConfig = VarProcessStatisticsConfig
                .builder()
                .varProcessServiceId(inputVO.getVarProcessServiceId())
                .varProcessManifestId(inputVO.getVarProcessManifestId())
                .startDate(inputVO.getStartDate())
                .endDate(inputVO.getEndDate())
                .analysisIndex(JSONObject.toJSONString(inputVO.getIndexMappingVo()))
                .ivConfig(inputVO.getIvMappingVo() == null ? null : JSONObject.toJSONString(inputVO.getIvMappingVo()))
                .psiConfig(inputVO.getPsiMappingVo() == null ? null : JSONObject.toJSONString(inputVO.getPsiMappingVo()))
                .specialValConfig(CollUtil.isEmpty(inputVO.getSpecialMappingVoList()) ? null : JSONObject.toJSONString(inputVO.getSpecialMappingVoList()))
                .createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build();
        // 3.保存入参对象
        if (!statisticsConfigService.save(statisticsConfig)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "新增统计配置失败!");
        }
        // 4.计算处理
        overallProcessStatistics.calculateHandler(inputVO.getVarProcessServiceId(), inputVO.getVarProcessManifestId());
        return statisticsConfig.getId();
    }

    /**
     * 参数校验
     *
     * @param inputVO 输入实体类对象
     * @param isAdd   是否新增
     */
    private void validParam(StatisticsConfigCreationInputVO inputVO, boolean isAdd) {
        // 1.第一行和第二行的必填项不能为空
        if (inputVO == null || inputVO.getVarProcessServiceId() == null || inputVO.getStartDate() == null || inputVO.getEndDate() == null || inputVO.getVarProcessManifestId() == null || inputVO.getIndexMappingVo() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "新增或编辑参数必填项不允许为空！");
        }
        // 2.开始时间不能大于结束时间
        if (inputVO.getStartDate().compareTo(inputVO.getEndDate()) > 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "开始时间不允许大于结束时间！");
        }
        // 3.如果要新增,则不能重复
        if (isAdd) {
            List<VarProcessStatisticsConfig> statisticsConfigList = statisticsConfigService.list(Wrappers.<VarProcessStatisticsConfig>lambdaQuery()
                    .select(VarProcessStatisticsConfig::getId)
                    .eq(VarProcessStatisticsConfig::getVarProcessServiceId, inputVO.getVarProcessServiceId())
                    .eq(VarProcessStatisticsConfig::getVarProcessManifestId, inputVO.getVarProcessManifestId()));
            if (CollUtil.isNotEmpty(statisticsConfigList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "该实时服务对应的变量清单已存在相关配置，请勿重复添加！");
            }
        }
        // 4.分析指标至少要选中一个
        if (!inputVO.getIndexMappingVo().isIv() && !inputVO.getIndexMappingVo().isPsi()
                && !inputVO.getIndexMappingVo().isZeroRatio() && !inputVO.getIndexMappingVo().isUniqueNum()
                && !inputVO.getIndexMappingVo().isPercentage() && !inputVO.getIndexMappingVo().isMissingRatio()
                && !inputVO.getIndexMappingVo().isSpecialRatio()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "未选中需要计算的指标！");
        }
        // 5.iv值计算参数设置不能为空
        if (inputVO.getIndexMappingVo() != null && inputVO.getIndexMappingVo().isIv() && inputVO.getIvMappingVo() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "iv参数设置不允许为空！");
        }
        // 6.psi参数设置不能为空
        if (inputVO.getIndexMappingVo() != null && inputVO.getIndexMappingVo().isPsi() && inputVO.getPsiMappingVo() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "psi参数设置不允许为空！");
        }
        // 7.特殊值设置也不能为空
        if (inputVO.getIndexMappingVo() != null && inputVO.getIndexMappingVo().isSpecialRatio() && CollUtil.isEmpty(inputVO.getSpecialMappingVoList())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "特殊值设置参数设置不允许为空！");
        }
        // 8.特殊值不能重复设置
        if (CollUtil.isNotEmpty(inputVO.getSpecialMappingVoList()) && inputVO.getSpecialMappingVoList().size() > MagicNumbers.ONE) {
            List<ConfigSpecialMappingVo> list = inputVO.getSpecialMappingVoList().stream()
                    .collect(
                            Collectors.collectingAndThen(
                                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> e.getDataType().getDesc() + e.getSpecialVal()))),
                                    ArrayList::new
                            )
                    );
            if (list.size() != inputVO.getSpecialMappingVoList().size()) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "特殊值设置不允许重复设置！");
            }
        }
    }

    /**
     * 编辑整体分析配置
     * @param inputVO 输入实体类对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void editStatisticsConfigHandler(StatisticsConfigCreationInputVO inputVO) {

        //参数校验
        validParam(inputVO, false);

        List<VarProcessStatisticsConfig> statisticsConfigList = statisticsConfigService.list(Wrappers.<VarProcessStatisticsConfig>lambdaQuery()
                .eq(VarProcessStatisticsConfig::getVarProcessServiceId, inputVO.getVarProcessServiceId())
                .eq(VarProcessStatisticsConfig::getVarProcessManifestId, inputVO.getVarProcessManifestId()));

        if (CollUtil.isEmpty(statisticsConfigList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_STATUS_NO_MATCH, "未找到该实时服务对应的变量清单相关配置！");
        }

        if (statisticsConfigList.size() > 1) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_STATUS_NO_MATCH, "该实时服务对应的变量清单相关统计配置存在多个！");
        }

        VarProcessStatisticsConfig statisticsConfig = VarProcessStatisticsConfig.builder().varProcessServiceId(inputVO.getVarProcessServiceId())
                .id(statisticsConfigList.get(0).getId())
                .varProcessManifestId(inputVO.getVarProcessManifestId())
                .startDate(inputVO.getStartDate())
                .endDate(inputVO.getEndDate())
                .analysisIndex(JSONObject.toJSONString(inputVO.getIndexMappingVo()))
                .ivConfig(inputVO.getIvMappingVo() == null ? null : JSONObject.toJSONString(inputVO.getIvMappingVo()))
                .psiConfig(inputVO.getPsiMappingVo() == null ? null : JSONObject.toJSONString(inputVO.getPsiMappingVo()))
                .specialValConfig(
                        CollUtil.isEmpty(inputVO.getSpecialMappingVoList()) ? null : JSON.toJSONString(inputVO.getSpecialMappingVoList()))
                .updatedUser(SessionContext.getSessionUser().getUsername()).build();

        statisticsConfigService.updateById(statisticsConfig);

        overallProcessStatistics.calculateHandler(inputVO.getVarProcessServiceId(), inputVO.getVarProcessManifestId());
    }

    /**
     * 获取缓存数据
     * @param varProcessServiceId 服务Id
     * @param varProcessManifestId 清单Id
     * @return StatisticsConfigDetailOutputVO
     */
    public StatisticsConfigDetailOutputVO getConfigDetail(Long varProcessServiceId, Long varProcessManifestId) {
        if (varProcessServiceId == null || varProcessManifestId == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "获取配置详情参数varProcessServiceId 和 varProcessManifestId不允许为空!");
        }
        StatisticsConfigDetailOutputVO outputVO = new StatisticsConfigDetailOutputVO();

        List<VarProcessStatisticsConfig> statisticsConfigList = statisticsConfigService.list(Wrappers.<VarProcessStatisticsConfig>lambdaQuery()
                .eq(VarProcessStatisticsConfig::getVarProcessServiceId, varProcessServiceId)
                .eq(VarProcessStatisticsConfig::getVarProcessManifestId, varProcessManifestId));

        if (CollUtil.isEmpty(statisticsConfigList)) {
            return null;
        }

        if (statisticsConfigList.size() > 1) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_STATUS_NO_MATCH, "该实时服务对应的变量清单相关统计配置存在多个！");
        }

        VarProcessStatisticsConfig config = statisticsConfigList.get(0);
        outputVO.setVarProcessServiceId(config.getVarProcessServiceId());
        outputVO.setVarProcessManifestId(config.getVarProcessManifestId());
        outputVO.setStartDate(config.getStartDate());
        outputVO.setEndDate(config.getEndDate());
        outputVO.setStatisticsConfigId(config.getId());


        if (StringUtils.isNotEmpty(config.getAnalysisIndex())) {
            AnalysisIndexMappingVo indexMappingVo = JSONObject.parseObject(config.getAnalysisIndex(), AnalysisIndexMappingVo.class);
            outputVO.setIndexMappingVo(indexMappingVo);
        }

        if (StringUtils.isNotEmpty(config.getIvConfig())) {
            ConfigIvMappingVo ivMappingVo = JSONObject.parseObject(config.getIvConfig(), ConfigIvMappingVo.class);
            outputVO.setIvMappingVo(ivMappingVo);
        }

        if (StringUtils.isNotEmpty(config.getPsiConfig())) {
            ConfigPsiMappingVo psiMappingVo = JSONObject.parseObject(config.getPsiConfig(), ConfigPsiMappingVo.class);
            outputVO.setPsiMappingVo(psiMappingVo);
        }

        if (StringUtils.isNotEmpty(config.getSpecialValConfig())) {
            List<ConfigSpecialMappingVo> specialMappingVoList = JSONObject.parseArray(config.getSpecialValConfig(), ConfigSpecialMappingVo.class);
            outputVO.setSpecialMappingVoList(specialMappingVoList);
        }

        return outputVO;
    }

    /**
     * calculateIndexHandler
     * @param varProcessServiceId 服务Id
     * @param varProcessManifestId 清单Id
     */
    @Transactional(rollbackFor = Exception.class)
    public void calculateIndexHandler(Long varProcessServiceId, Long varProcessManifestId) {
        overallProcessStatistics.calculateHandler(varProcessServiceId, varProcessManifestId);
    }

    /**
     * 获取结果页
     *
     * @param inputVO 前端发送的实体
     * @return 批量回溯分页查询结果Dto
     */
    public BacktrackingStatisticsResultPageOutputVO getResultPage(VarProcessStatisticsResultQueryVO inputVO) {

        BacktrackingStatisticsResultPageOutputVO outputVO = new BacktrackingStatisticsResultPageOutputVO();

        if (inputVO == null || inputVO.getVarProcessServiceId() == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "新增或编辑参数必填项不允许为空！");
        }

        List<VarProcessStatisticsConfig> statisticsConfigList = statisticsConfigService.list(Wrappers.<VarProcessStatisticsConfig>lambdaQuery()
                .select(VarProcessStatisticsConfig::getId, VarProcessStatisticsConfig::getAnalysisIndex)
                .eq(VarProcessStatisticsConfig::getVarProcessServiceId, inputVO.getVarProcessServiceId())
                .eq(VarProcessStatisticsConfig::getVarProcessManifestId, inputVO.getVarProcessManifestId()));

        if (CollUtil.isEmpty(statisticsConfigList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "未找到对应的配置！");
        }
        AnalysisIndexMappingVo indexMappingVo = JSON.parseObject(statisticsConfigList.get(0).getAnalysisIndex(), AnalysisIndexMappingVo.class);

        //获取表头
        outputVO.setHeadList(getHeadList(indexMappingVo));


        String order = inputVO.getOrder();
        if (StringUtils.isEmpty(inputVO.getOrder())) {
            inputVO.setSortKey("vpsr.var_name");
            inputVO.setSortType("ASC");
        } else {
            String sortType = order.substring(order.lastIndexOf("_") + 1);
            String sortKey = order.substring(0, order.lastIndexOf("_"));
            inputVO.setSortKey("vpsr." + getSortKey(sortKey));
            inputVO.setSortType(sortType);
        }

        IPage<Map<String, Object>> pageList = resultService.getPageList(new Page<>(inputVO.getCurrentNo(), inputVO.getSize()), inputVO);

        if (CollectionUtils.isEmpty(pageList.getRecords())) {
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
            PercentageMappingVo percentageMappingVo = JSON.parseObject((String) map.get("percentageResult"), PercentageMappingVo.class);
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
     * 获取排序key
     * @param key key
     * @return 获取排序key
     */
    public String getSortKey(String key) {
        String sortKey = null;
        if (StringUtils.isEmpty(key)) {
            return sortKey;
        }
        if (IV_RESULT.equals(key)) {
            sortKey = "iv_result";
        }
        if (PSI_RESULT.equals(key)) {
            sortKey = "psi_result";
        }
        if (MISSING_RATIO.equals(key)) {
            sortKey = "missing_ratio";
        }
        if (UNIQUE_NUM.equals(key)) {
            sortKey = "unique_num";
        }
        if (MINIMUM_VAL.equals(key)) {
            sortKey = "minimum_val";
        }
        if (MAX_VAL.equals(key)) {
            sortKey = "max_val";
        }
        if (AVERAGE_VAL.equals(key)) {
            sortKey = "average_val";
        }
        return sortKey;
    }

    /**
     * 获取表头
     *
     * @param indexMappingVo 分析指标配置vo
     * @return 表头
     */
    public List<String> getHeadList(AnalysisIndexMappingVo indexMappingVo) {
        List<String> headList = new ArrayList<>();
        headList.add("变量名称");
        headList.add("数据类型");

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
     * 导出
     * @param inputVO 输入实体类对象
     * @param response 响应对象
     */
    public void export(VarProcessStatisticsResultQueryVO inputVO, HttpServletResponse response) {

        BacktrackingStatisticsResultPageOutputVO backtrackingStatisticsResultPageOutputVO = getResultPage(inputVO);

        if (backtrackingStatisticsResultPageOutputVO == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "未获取到数据！");
        }

        List<String> headList = backtrackingStatisticsResultPageOutputVO.getHeadList();
        //插入数据
        List<Map<String, Object>> dataMapList = backtrackingStatisticsResultPageOutputVO.getPage().getRecords();

        executeExport(headList, dataMapList, response);
    }

    /**
     * 导出结果
     * @param headList 结果头列表
     * @param dataMapList 数据map列表
     * @param response 响应对象
     */
    public void executeExport(List<String> headList, List<Map<String, Object>> dataMapList, HttpServletResponse response) {
        //key：索引 value:属性名
        HashMap<Integer, String> headNameMap = new HashMap<>(MagicNumbers.SIXTEEN);
        for (int i = 0; i < headList.size(); i++) {
            headNameMap.put(i, headList.get(i));
        }

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            //创建表头
            XSSFRow headRow = sheet.createRow(0);
            for (int j = 0; j < headNameMap.size(); j++) {
                XSSFCell cell = headRow.createCell(j);
                cell.setCellValue(headNameMap.get(j));
            }

            int rowNo = 1;
            for (Map<String, Object> map : dataMapList) {
                XSSFRow row = sheet.createRow(rowNo);
                for (int i = 0; i < headList.size(); i++) {
                    XSSFCell cell = row.createCell(i);
                    if (headNameMap.get(i).contains("指标名称") || headNameMap.get(i).contains("数据类型")
                            || headNameMap.get(i).contains("缺失值占比") || headNameMap.get(i).contains("特殊值占比")
                            || headNameMap.get(i).contains("零值占比")) {
                        cell.setCellValue((String) map.get(headNameMap.get(i)));
                    } else if (headNameMap.get(i).contains("唯一值数量")) {
                        cell.setCellValue(map.get(headNameMap.get(i)) == null ? null : (String.valueOf(map.get(headNameMap.get(i)))));
                    } else {
                        cell.setCellValue(map.get(headNameMap.get(i)) == null ? null : (map.get(headNameMap.get(i))).toString());
                    }
                }
                rowNo++;

            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ExcelExportUtils.setResponseHeader(response, sdf.format(new Date()) + ".xlsx");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("导出统计结果Excel文件异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "导出统计结果Excel文件异常");
        }
    }

    /**
     * 获取基准值
     *
     * @param queryVo 指标回溯统计结果查询
     * @return 基准值集合
     */
    public List<String> getReferenceValue(StatisticsReferenceValueInputVO queryVo) {
        // 1.查看内部数据源中的表是否存在(var_process_manifest_id)
        String tableName = getTableName(queryVo.getVarProcessManifestId());
        if (!dbOperateService.isTableExist(tableName)) {
            return null;
        }
        // 2.拼接SQL语句后面的条件(查询字段的唯一值)
        String whereCondition = generateWhereCondition(queryVo);

        List<String> referenceValueList = dbOperateService.getColumnDataDistinct(tableName, queryVo.getIndexName(), whereCondition, queryVo.getVarProcessManifestId()).stream().map(String::valueOf).collect(Collectors.toList());
        if (referenceValueList.size() > MagicNumbers.INT_50) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "该字段唯一值个数超过50个，不能选择");
        }
        return referenceValueList;
    }


    /**
     * 指标监控——获取基准值
     *
     * @param queryVo 指标回溯统计结果查询
     * @return 基准值集合
     */
    public List<String> getReferenceValueFromMonitoring(StatisticsReferenceFromMonitoringValueInputVO queryVo) {
        //查看内部数据源中的表是否存在(var_process_manifest_id)
        String tableName = getTableName(queryVo.getVarProcessManifestId());
        if (!dbOperateService.isTableExist(tableName)) {
            return null;
        }
        //查找serviceId
        VarProcessServiceVersion varProcessService = varProcessServiceVersionService.findServiceByNameAndVersion(queryVo.getServiceName(), queryVo.getServiceVersion());
        if (varProcessService == null) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.SERVICE_NOT_FOUND, "实时服务不存在");
        }

        //拼接SQL语句后面的条件(查询字段的唯一值)
        String whereCondition;
        if (queryVo.getBaseIndexCallDate() && queryVo.getTime() != null && queryVo.getTimeUnit() != null) {
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minus((long) queryVo.getTime() * queryVo.getTimeUnit().getMinute(), ChronoUnit.MINUTES);
            whereCondition = generateWhereCondition(varProcessService.getId(), startTime, endTime);
        } else {
            whereCondition = generateWhereCondition(varProcessService.getId(), null, null);
        }

        List<String> referenceValueList = dbOperateService
                .getColumnDataDistinct(tableName, queryVo.getIndexName(), whereCondition, queryVo.getVarProcessManifestId()).stream().map(String::valueOf).collect(Collectors.toList());
        if (referenceValueList.size() > MagicNumbers.INT_50) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "该字段唯一值个数超过50个，不能选择");
        }
        return referenceValueList;
    }

    protected String getTableName(Long paramId) {
        return CLICK_HOUSE_TABLE_PRIFIX + "_" + paramId.toString();
    }

    private String generateWhereCondition(StatisticsReferenceValueInputVO queryVo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" service_id = '").append(queryVo.getVarProcessServiceId()).append("'");
        if (queryVo.getStartDate() != null && queryVo.getBaseIndexCallDate()) {
            sb.append(" and request_date >= '").append(queryVo.getStartDate().atStartOfDay().format(FORMATTER)).append("'");
        }
        if (queryVo.getEndDate() != null && queryVo.getBaseIndexCallDate()) {
            sb.append(" and request_date <= '")
                    .append(LocalDateTimeUtil.endOfDay(queryVo.getEndDate().atStartOfDay()).format(FORMATTER)).append("'");
        }
        return sb.toString();
    }

    private String generateWhereCondition(Long serviceId, LocalDateTime startDate, LocalDateTime endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(" service_id = '").append(serviceId).append("'");
        if (startDate != null) {
            sb.append(" and request_date >= '")
                    .append(startDate.format(FORMATTER)).append("'");
        }
        if (endDate != null) {
            sb.append(" and request_date <= '")
                    .append(endDate.format(FORMATTER)).append("'");
        }
        return sb.toString();
    }

    /**
     * 获取基准数据项
     * @param serviceId 实时服务的Id
     * @param manifestId 变量清单的Id
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @param baseIndexCallDate 基准指标的调用时间段 true/与设置的时间维度一致; false/所有时间段
     * @param indexName 基准分组指标的下拉值
     * @return 基准数据项
     */
    public List<String> getBasicValueItem(Long serviceId, Long manifestId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean baseIndexCallDate, String indexName) {
        // 1.查看内部数据源中的表是否存在
        String tableName = MagicStrings.VAR_PROCESS_MANIFEST + manifestId.toString();
        if (!dbOperateService.isTableExist(tableName)) {
            return new ArrayList<>();
        }
        // 2.拼接SQL语句
        String whereCondition = this.concatWhereCondition(serviceId, startDateTime, endDateTime, baseIndexCallDate);
        // 3.获取结果
        List<Object> objects = dbOperateService.getColumnDataDistinct(tableName, indexName, whereCondition, manifestId);
        if (objects.size() > MagicNumbers.INT_50) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "该指标唯一值数量超过50个，不适合作为基准分组指标!");
        }
        List<String> result = new ArrayList<>();
        for (Object item : objects) {
            if (item instanceof Date) {
                String str = DateUtil.parseDateToStr((Date) item, MagicStrings.DATE_TIME_FORMAT);
                result.add(str);
                continue;
            }
            result.add(String.valueOf(item));
        }
        return result;
    }

    /**
     * SQL语句后面的where条件拼接
     * @param serviceId 实时服务的Id
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @param baseIndexCallDate 基准指标的调用时间段 true/与设置的时间维度一致; false/所有时间段
     * @return where条件
     */
    private String concatWhereCondition(Long serviceId, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean baseIndexCallDate) {
        // 1.定义where后面的条件
        StringBuilder sb = new StringBuilder();
        sb.append(" service_id = '").append(serviceId).append("'");
        if (baseIndexCallDate) {
            // 1.1 报错的场景
            boolean flag = (startDateTime != null && endDateTime == null) || (startDateTime == null && endDateTime != null);
            if (flag) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "开始时间和结束时间要么全部赋值, 要么全部不赋值!");
            }
            if (startDateTime != null) {
                checkLocalDateTime(startDateTime, endDateTime);
            }
            // 1.2 如果开始时间和结束时间不为空，就拼接它们
            if (startDateTime != null) {
                sb.append(" and request_date >= '").append(DateTimeUtils.parse(startDateTime)).append("'");
                sb.append(" and request_date <= '").append(DateTimeUtils.parse(endDateTime)).append("'");
            }
        }
        return sb.toString();
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
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "开始时间与结束时间的间隔不得超过一年!");
        }
    }

    /**
     * 获取实时服务PSI数据
     *
     * @param configIds 配置id
     * @return 实时服务PSI数据
     */
    public List<OverviewTargetStatisticsDto> getOverviewTargetPsi(List<Long> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return new ArrayList<>();
        }
        return resultService.getOverviewTargetPsi(configIds);
    }

    /**
     * 获取实时服务IV数据
     *
     * @param configIds 配置id
     * @return 实时服务IV数据
     */
    public List<OverviewTargetStatisticsDto> getOverviewTargetIv(List<Long> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return new ArrayList<>();
        }
        return resultService.getOverviewTargetIv(configIds);
    }

    /**
     * 获取实时服务缺失值数据
     *
     * @param configIds 配置id
     * @return 实时服务缺失值数据
     */
    public List<OverviewTargetStatisticsDto> getOverviewTargetMr(List<Long> configIds) {
        if (CollectionUtils.isEmpty(configIds)) {
            return new ArrayList<>();
        }
        return resultService.getOverviewTargetMr(configIds);
    }



    /**
     * 定义任务执行更新统计
     */
    public void runTask() {
        List<VarProcessStatisticsConfig> statisticsConfigList = statisticsConfigService.findCurrentStatistics();
        log.info("查询到统计任务{}个", statisticsConfigList.size());
        for (VarProcessStatisticsConfig varProcessStatisticsConfig : statisticsConfigList) {
            try {
                overallProcessStatistics.calculateHandler(varProcessStatisticsConfig.getVarProcessServiceId(), varProcessStatisticsConfig.getVarProcessManifestId());
            } catch (Exception e) {
                log.error("执行统计分析任务serviceId={},manifestId={}失败", varProcessStatisticsConfig.getVarProcessServiceId(), varProcessStatisticsConfig.getVarProcessManifestId());
                log.error(e.toString());
            }
        }
    }

}
