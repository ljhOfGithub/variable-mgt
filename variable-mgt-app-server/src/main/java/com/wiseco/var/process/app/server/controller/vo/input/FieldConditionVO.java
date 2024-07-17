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
package com.wiseco.var.process.app.server.controller.vo.input;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.ComparisonOperatorEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mingao
 * @since 2023/8/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "条件封装")
public class FieldConditionVO implements Serializable {
    private static final String STRING_TYPES = " char,varchar,string,tinytext,text,varchar2,nvarchar2,nchar,date,datetime";
    @Schema(description = "字段名")
    private String fieldName;
    @Schema(description = "字段类型")
    private String fieldType;
    @Schema(description = "条件")
    private List<ConditionVO> conditions;
    @Schema(description = "逻辑值: AND/OR", example = "OR")
    private String logicalOperator;

    private static boolean isString(String fieldType) {
        return STRING_TYPES.contains(fieldType);
    }

    /**
     * 根据条件拼接sql
     *
     * @return 条件sql
     */
    public String buildSql() {
        Map<ComparisonOperatorEnum, List<String>> conditionMap = new HashMap<>(MagicNumbers.SIXTEEN);
        for (ConditionVO conditionVO : conditions) {
            if (conditionMap.containsKey(conditionVO.getType())) {
                List<String> list = conditionMap.get(conditionVO.getType());
                list.add(conditionVO.getValue());
            } else {
                List<String> list = new ArrayList<>();
                list.add(conditionVO.getValue());
                conditionMap.put(conditionVO.getType(), list);
            }
        }
        String conds = " 1=1 ";
        List<String> listConds;
        List<String> sqlConds = new ArrayList<>();
        Set<ComparisonOperatorEnum> comparisonOperatorEnums = conditionMap.keySet();
        for (ComparisonOperatorEnum co : comparisonOperatorEnums) {
            switch (co) {
                case EQUALS:
                    List<String> listEquals = conditionMap.get(ComparisonOperatorEnum.EQUALS);
                    conds = getConditionSql(listEquals, co.getCode());
                    break;
                case GREATER_THAN_OR_EQUALS:
                    listConds = conditionMap.get(ComparisonOperatorEnum.GREATER_THAN_OR_EQUALS);
                    conds = getConditionSql(listConds, co.getCode());
                    break;
                case GREATER_THAN:
                    listConds = conditionMap.get(ComparisonOperatorEnum.GREATER_THAN);
                    conds = getConditionSql(listConds, co.getCode());
                    break;
                case LESS_THAN:
                    listConds = conditionMap.get(ComparisonOperatorEnum.LESS_THAN);
                    conds = getConditionSql(listConds, co.getCode());
                    break;
                case LESS_THAN_OR_EQUALS:
                    listConds = conditionMap.get(ComparisonOperatorEnum.LESS_THAN_OR_EQUALS);
                    conds = getConditionSql(listConds, co.getCode());
                    break;
                case NOT_EQUALS:
                    listConds = conditionMap.get(ComparisonOperatorEnum.NOT_EQUALS);
                    conds = getConditionSql(listConds, co.getCode());
                    break;
                case NOT_CONTAINS:
                    List<String> listNotContains = conditionMap.get(ComparisonOperatorEnum.NOT_CONTAINS);
                    if (isString(fieldType)) {
                        conds = listNotContains.stream().map(s -> String.format("%s %s '%%%s%%'",fieldName,co.getCode(), s))
                                .collect(Collectors.joining(" " + logicalOperator + " "));
                    }
                    break;
                case CONTAINS:
                    List<String> listContains = conditionMap.get(ComparisonOperatorEnum.CONTAINS);
                    if (isString(fieldType)) {
                        conds = listContains.stream().map(s -> String.format("%s %s '%%%s%%'",fieldName,co.getCode(), s))
                                .collect(Collectors.joining(" " + logicalOperator + " "));
                    }
                    break;
                case START_WITH:
                    List<String> listStartWiths = conditionMap.get(ComparisonOperatorEnum.START_WITH);
                    if (isString(fieldType)) {
                        conds = listStartWiths.stream().map(s -> String.format("%s %s '%s%%'",fieldName,co.getCode(), s))
                                .collect(Collectors.joining(" " + logicalOperator + " "));
                    }
                    break;
                case END_WITH:
                    List<String> listEndWiths = conditionMap.get(ComparisonOperatorEnum.END_WITH);
                    if (isString(fieldType)) {
                        conds = listEndWiths.stream().map(s -> String.format("%s %s '%%%s'",fieldName,co.getCode(), s))
                                .collect(Collectors.joining(" " + logicalOperator + " "));
                    }
                    break;
                default:
            }
            sqlConds.add(conds);
        }
        return sqlConds.stream().filter(s -> !(StringUtil.isEmpty(s) || StringUtil.isBlank(s)))
                                .collect(Collectors.joining(" " + logicalOperator + " "));
    }

    private String getConditionSql(List<String> listConds, String code) {
        String conds;
        if (isString(fieldType)) {
            conds = listConds.stream().map(s -> String.format("%s %s '%s'",fieldName, code, s))
                    .collect(Collectors.joining(" " + logicalOperator + " "));
        } else {
            conds = listConds.stream().filter(s -> !(StringUtil.isEmpty(s) || StringUtil.isBlank(s)))
                    .map(s -> String.format("%s %s %s",fieldName, code, s))
                    .collect(Collectors.joining(" " + logicalOperator + " "));
        }
        return conds;
    }
}
