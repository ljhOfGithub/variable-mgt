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
package com.wiseco.var.process.app.server.statistics.template;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.ConfigIvMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.controller.vo.output.BacktrackingConfigDetailOutputVO;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingStatisticsResult;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.service.BacktrackingStatisticsResultService;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingOverallService;
import com.wiseco.var.process.app.server.service.backtracking.BacktrackingService;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OverallBacktrackingStatistics extends AbstractOverallStatisticsTemplate {

    @Resource
    BacktrackingStatisticsResultService resultService;

    @Resource
    BacktrackingOverallService backtrackingOverallService;

    @Resource
    private BacktrackingService backtrackingService;

    @Resource
    private DbOperateService dbOperateService;

    @Resource
    private VarProcessVariableService varProcessVariableService;

    @Resource
    private VarProcessBatchBacktrackingMapper varBatchBacktrackingMapper;

    /**
     * 计算处理
     * @param backtrackingId 批量回溯的Id
     */
    public void calculateHandler(Long backtrackingId) {
        BacktrackingConfigDetailOutputVO detailOutputVO = backtrackingOverallService.getConfigDetail(backtrackingId);
        Assert.notNull(detailOutputVO, "未找到配置详情！");
        Assert.notNull(detailOutputVO.getIndexMappingVo(), "计算指标不允许为空！");

        if (!detailOutputVO.getIndexMappingVo().isIv() && !detailOutputVO.getIndexMappingVo().isPsi()
                && !detailOutputVO.getIndexMappingVo().isZeroRatio() && !detailOutputVO.getIndexMappingVo().isUniqueNum()
                && !detailOutputVO.getIndexMappingVo().isPercentage() && !detailOutputVO.getIndexMappingVo().isMissingRatio()
                && !detailOutputVO.getIndexMappingVo().isSpecialRatio()) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "不存在需要计算的指标！");
        }

        //数据准备
        IndexCalculateContext calculateContext = new IndexCalculateContext();
        calculateContext.setIndexMappingVo(detailOutputVO.getIndexMappingVo());
        calculateContext.setIvMappingVo(detailOutputVO.getIvMappingVo());
        calculateContext.setPsiMappingVo(detailOutputVO.getPsiMappingVo());
        calculateContext.setSpecialMappingVoList(detailOutputVO.getSpecialMappingVoList());
        calculateContext.setBatchNumber(detailOutputVO.getBatchNumber());
        calculateContext.setTableSourceId(backtrackingId);
        calculateContext.setBacktrackingConfigId(detailOutputVO.getBacktrackingConfigId());
        calculateContext.setVarNameMap(getIndexNameMap(backtrackingId));
        final VarProcessBatchBacktracking batchBacktracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                        .select(VarProcessBatchBacktracking::getManifestId)
                        .eq(VarProcessBatchBacktracking::getId, backtrackingId));
        calculateContext.setManifestId(batchBacktracking.getManifestId());
        executeCalculate(calculateContext);
    }

    @Override
    protected String aggregateSql(IndexCalculateContext calculateContext) {
        String sql = null;
        final ConfigIvMappingVo ivMappingVo = calculateContext.getIvMappingVo();
        if (ivMappingVo.getSourceType() == MagicNumbers.ONE) {
            // 来源于内部数据表
            sql = "select %s as name,%s as target,max(id) from %s group by %s";
            sql = String.format(sql, ivMappingVo.getRelationField(), ivMappingVo.getTargetField(), ivMappingVo.getTableName(), ivMappingVo.getRelationField());
        }
        return sql;
    }

    @Override
    protected String getTableName(Long backtrackingId) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getResultTable)
                .eq(VarProcessBatchBacktracking::getId, backtrackingId));
        return backtracking.getResultTable();
    }

    @Override
    protected List<Map<String, Object>> getDataList(String tableName, IndexCalculateContext calculateContext) {
        String querySql = "select " + "increment_id " + "," + SERIAL_NO + ", " + MYSQL_VARIABLES + " from "
                + tableName
                + " where batch_no in (" + calculateContext.getBatchNumber() + ") ";
        return queryData(calculateContext, tableName, querySql);
    }

    @Override
    protected Map<String, String> getIndexNameMap(Long paramId) {
        VarProcessBatchBacktracking backtracking = backtrackingService.getOne(Wrappers.<VarProcessBatchBacktracking>lambdaQuery()
                .select(VarProcessBatchBacktracking::getManifestId)
                .eq(VarProcessBatchBacktracking::getId, paramId));
        if (backtracking == null) {
            return new HashMap<>(MagicNumbers.SIXTEEN);
        }

        // 获取变量清单发布变量信息
        List<VarProcessVariable> publishedVariableList = varProcessVariableService.findManifestOutputVariableList(backtracking.getManifestId());

        return publishedVariableList.stream().collect(Collectors.toMap(VarProcessVariable::getName, VarProcessVariable::getLabel));
    }

    @Override
    protected void saveData(IndexCalculateContext calculateContext, boolean isDelete) {
        if (isDelete) {
            LambdaQueryWrapper<VarProcessBatchBacktrackingStatisticsResult> resultLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resultLambdaQueryWrapper.eq(VarProcessBatchBacktrackingStatisticsResult::getBacktrackingConfigId, calculateContext.getBacktrackingConfigId());
            resultService.remove(resultLambdaQueryWrapper);
        }

        //保存
        if (CollUtil.isNotEmpty(calculateContext.getResultList())) {
            List<VarProcessBatchBacktrackingStatisticsResult> list = calculateContext.getResultList().stream().map(e -> {
                VarProcessBatchBacktrackingStatisticsResult result = new VarProcessBatchBacktrackingStatisticsResult();
                BeanUtils.copyProperties(e, result);
                result.setBacktrackingConfigId(calculateContext.getBacktrackingConfigId());
                result.setCreatedUser(SessionContext.getSessionUser().getUsername());
                result.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                return result;
            }).collect(Collectors.toList());

            resultService.saveBatch(list);
        }
    }

    @Override
    public Map<String, List<Object>> getPsiDataMap(StatisticsResultVo result, IndexCalculateContext calculateContext) {
        String indexCode = result.getVarCode();
        List<Map<String, Object>> dataList = calculateContext.getDataList();
        String tableName = getTableName(calculateContext.getTableSourceId());

        Map<String, List<Object>> psiDataMap = new HashMap<>(MagicNumbers.EIGHT);
        List<Object> targetList = null;
        List<Object> standardList = null;
        if (Boolean.TRUE.equals(calculateContext.getPsiMappingVo().getBaseIndexFlag())) {
            // 基准数据
            // 获取首个批次号
            String backtrackingFirstTaskCode = varBatchBacktrackingMapper.findBacktrackingFirstTaskCode(calculateContext.getTableSourceId());
            String whereCondition = generateWhereCondition(calculateContext.getTableSourceId(), backtrackingFirstTaskCode, null, null);

            // 获取目标数列
            targetList = dataList.stream().filter(Objects::nonNull).map(e -> e.get(indexCode)).collect(Collectors.toList());

            // 获取基准数列
            final int varIndex = getVarIndex(calculateContext.getAllManifestVarsType(), indexCode);
            standardList = dbOperateService.getColumnData(tableName, varIndex, whereCondition);
        } else {
            // 基准指标
            String baseIndex = calculateContext.getPsiMappingVo().getBaseIndex();
            String baseIndexVal = calculateContext.getPsiMappingVo().getBaseIndexVal();

            // 获取目标数列
            targetList = dataList.stream().filter(e -> e != null && !baseIndexVal.equals(String.valueOf(e.get(baseIndex)))).map(e -> e.get(indexCode)).collect(Collectors.toList());

            // 获取基准数列
            standardList = dataList.stream().filter(e -> e != null && baseIndexVal.equals(String.valueOf(e.get(baseIndex)))).map(e -> e.get(indexCode)).collect(Collectors.toList());
        }
        psiDataMap.put("data1", standardList);
        psiDataMap.put("data2", targetList);
        return psiDataMap;
    }


    /**
     * 批量回溯---生成where条件
     *
     * @param backtrackingId 批量回溯id
     * @param backtrackingTaskCodes 首次任务批次号
     * @param baseIndex 基准指标
     * @param baseIndexVal 基准指标数据项
     * @return where条件
     */
    private String generateWhereCondition(Long backtrackingId, String backtrackingTaskCodes, String baseIndex, String baseIndexVal) {
        StringBuilder sb = new StringBuilder();
        sb.append(" backtracking_id = ").append(backtrackingId).append(" AND ");
        sb.append("batch_no").append(" IN ('").append(backtrackingTaskCodes).append("')");

        if (baseIndex != null) {
            sb.append(" AND ").append(baseIndex).append(" = '").append(baseIndexVal).append("'");
        }
        return sb.toString();
    }
}
