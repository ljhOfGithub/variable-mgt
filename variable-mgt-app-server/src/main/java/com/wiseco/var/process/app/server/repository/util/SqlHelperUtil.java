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
package com.wiseco.var.process.app.server.repository.util;

import com.google.common.collect.Lists;
import com.wiseco.var.process.app.server.commons.constant.SqlQueryKeyConstant;
import com.wiseco.var.process.app.server.controller.vo.FieldSqlRespVO;
import com.wiseco.var.process.app.server.controller.vo.TableJoinRespVO;
import com.wiseco.var.process.app.server.controller.vo.input.DataBaseFieldMapDto;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.innerdata.TableJoinDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * sql处理服务类
 *
 * @author yangyunsen
 * @since 2022/10/18-19:11
 */
public class SqlHelperUtil {
    /**
     * 表关联配置列表转换提取有效数据
     *
     * @param tableJoinList 入参
     * @return TableJoinRespVO
     */
    public static TableJoinRespVO tableJoinListFormat(List<TableJoinDto> tableJoinList) {
        Set<String> tableAlias = new HashSet<>();
        for (TableJoinDto tableJoinDto : tableJoinList) {
            String alias = tableJoinDto.getAlias();
            if (StringUtils.isNotBlank(alias)) {
                if (!alias.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "表别名不符合规则，别名可以使用字母、数字和下划线，且不能以数字开头");
                }
                if (!tableAlias.add(alias)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "表别名重复");
                }
            }
        }

        StringBuilder joinTableAndOnConditionSb = new StringBuilder();
        String mainTableName = null;
        if (tableJoinList.size() == 1) {
            TableJoinDto tableJoin = tableJoinList.get(0);
            if (StringUtils.isBlank(tableJoin.getAlias())) {
                mainTableName = tableJoin.getTableName();
            } else {
                mainTableName = tableJoin.getTableName() + " " + tableJoin.getAlias();
            }
        } else {
            for (int i = 0; i < tableJoinList.size(); i++) {
                TableJoinDto tableJoin = tableJoinList.get(i);
                if (i == 0) {
                    mainTableName = tableJoin.getTableName() + " " + tableJoin.getAlias();
                } else {
                    joinTableAndOnConditionSb.append(MessageFormat.format(SqlQueryKeyConstant.JOINTEMPLATE, tableJoin.getJoinType().getKeyWord(),
                            tableJoin.getTableName(), tableJoin.getAlias(),
                            StringUtils.isNotBlank(tableJoin.getJoinField()) ? "on " + tableJoin.getJoinField() : ""));
                }
            }
        }
        return TableJoinRespVO.builder().mainTableName(mainTableName).joinTableAndOnCondition(joinTableAndOnConditionSb.toString()).build();
    }

    /**
     * 字段映射关系转查询结果列sql
     *
     * @param fieldMappings 入参
     * @param targetMappings 入参
     * @param onlyMainTable 入参
     * @param transformSign 入参
     * @return FieldSqlRespVO
     */
    public static FieldSqlRespVO fieldMappingToFieldSql(List<DataBaseFieldMapDto> fieldMappings,
                                                        Map<String, List<String>> targetMappings,
                                                        boolean onlyMainTable,
                                                        String transformSign) {
        StringBuilder fieldSb = new StringBuilder();
        // 同时构建第几个字段对应哪一个目标字段
        Map<String, Integer> usedFieldMap = new HashMap(fieldMappings.size());
        Map<Integer, List<String>> resultIndexTargetPairMap = new HashMap(fieldMappings.size());
        Map<Integer, Pair<String, String>> resultIndexLabelNameAliasMap = new HashMap(fieldMappings.size());
        for (DataBaseFieldMapDto fieldMapParam : fieldMappings) {
            if (StringUtils.isBlank(fieldMapParam.getOrgFieldName())) {
                continue;
            }
            String field = null;
            if (onlyMainTable) {
                if (StringUtils.isBlank(fieldMapParam.getOrgTableName())) {
                    field = transformSign + fieldMapParam.getOrgFieldName() + transformSign;
                } else {
                    field = MessageFormat.format(SqlQueryKeyConstant.FIELDTEMPLATE,
                             fieldMapParam.getOrgTableName(),
                            transformSign + fieldMapParam.getOrgFieldName() + transformSign,
                            fieldMapParam.getOrgTableName(),
                            fieldMapParam.getOrgFieldName().replace(".", "_"));
                }
            } else {
                if (StringUtils.isBlank(fieldMapParam.getOrgTableName())) {
                    continue;
                }
                field = MessageFormat.format(SqlQueryKeyConstant.FIELDTEMPLATE,
                        fieldMapParam.getOrgTableName(),
                        transformSign + fieldMapParam.getOrgFieldName() + transformSign,
                        fieldMapParam.getOrgTableName(),
                        fieldMapParam.getOrgFieldName().replace(".", "_"));
            }
            if (!usedFieldMap.containsKey(field)) {
                fieldSb.append(field)
                        .append(",");
                resultIndexLabelNameAliasMap.put(usedFieldMap.size(), new Pair(fieldMapParam.getLabelField(), fieldMapParam.getSqlAliasField()));
                resultIndexTargetPairMap.put(usedFieldMap.size(),
                        //如果存在，就存放映射值，不存在，就存放默认别名
                        Optional.ofNullable(targetMappings)
                                .map(maps -> maps.get(fieldMapParam.getLabelField()))
                                .orElse(Lists.newArrayList(fieldMapParam.getSqlAliasField()))
                );
                usedFieldMap.put(field, usedFieldMap.size());
            }
        }
        String fieldSql = fieldSb.toString().substring(0, fieldSb.length() - 1);
        return FieldSqlRespVO.builder()
                .fieldSql(fieldSql)
                .resultIndexTargetPairMap(resultIndexTargetPairMap)
                .resultIndexLabelNameAliasMap(resultIndexLabelNameAliasMap)
                .build();
    }
}
