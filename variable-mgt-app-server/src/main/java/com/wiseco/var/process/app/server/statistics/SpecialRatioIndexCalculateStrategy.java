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
package com.wiseco.var.process.app.server.statistics;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.AnalysisIndexEnum;
import com.wiseco.var.process.app.server.controller.vo.ConfigSpecialMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import com.wiseco.var.process.app.server.enums.VarMathSymbolTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;


@Component
@Slf4j
public class SpecialRatioIndexCalculateStrategy implements IndexCalculateStrategy {

    /**
     * 计算特殊值
     * @param result 结果vo
     * @param calculateContext  计算vo
     */
    @Override
    public void calculateVar(StatisticsResultVo result, IndexCalculateContext calculateContext) {
        String fieldType = result.getDataType();
        boolean flag = !((StringUtils.isEmpty(fieldType) || fieldType.equals(CommonConstant.LONG_STR)) || (calculateContext.getDataTypeConfigMap() == null || result.getDataType() == null) || calculateContext.getSpecialMappingVoList() == null);
        if (flag) {
            List<ConfigSpecialMappingVo> specialMappingVoList = calculateContext.getSpecialMappingVoList();

            //对所有特殊值按照类型进行分组
            Map<VarDataTypeEnum, List<ConfigSpecialMappingVo>> groupedDataType = specialMappingVoList.stream()
                    .collect(Collectors.<ConfigSpecialMappingVo, VarDataTypeEnum>groupingBy(ConfigSpecialMappingVo::getDataType));
            //获取当前字类型段的特殊值list
            List<ConfigSpecialMappingVo> fieldTypeList = groupedDataType.get(VarDataTypeEnum.getEnumFromDesc(fieldType));
            if (CollectionUtils.isEmpty(fieldTypeList)) {
                result.setSpecialRatio(null);
                return;
            }
            //获取所有操作类型
            List<String> operatorLost = extractOperator(fieldTypeList);
            Boolean empty = operatorLost.contains(VarMathSymbolTypeEnum.EMPTY.getDesc());
            List<Map<String, Object>> allDataList = calculateContext.getDataList();
            List<Object> dataList = getData(allDataList,result);
            List<Object> dataIsSpecialValueList;

            //如果fieldType为int、double、date、datetime 则有=、>、>=、<、<=、为空 这几种操作类型
            if (VarDataTypeEnum.INTEGER.getDesc().equals(fieldType) || VarDataTypeEnum.DOUBLE.getDesc().equals(fieldType) || VarDataTypeEnum.DATE.getDesc().equals(fieldType) || VarDataTypeEnum.DATETIME.getDesc().equals(fieldType)) {
                dataIsSpecialValueList = getIntDoubleDataDataTimeEqual(fieldTypeList, calculateContext, result, fieldType, empty, operatorLost);
            }  else if (VarDataTypeEnum.STRING.getDesc().equals(fieldType)) {
                dataIsSpecialValueList = getStringEqual(fieldTypeList,calculateContext,result,fieldType,empty);
            } else if (VarDataTypeEnum.BOOLEAN.getDesc().equals(fieldType)) {
                dataIsSpecialValueList = getBoolenEqual(fieldTypeList,calculateContext,result,fieldType);
            } else {
                throw new RuntimeException("未知的数据类型");
            }
            result.setSpecialRatio(new BigDecimal(dataIsSpecialValueList.size()).divide(new BigDecimal(dataList.size()), MagicNumbers.FOUR, RoundingMode.HALF_UP));

        }
    }

    /**
     *  获取int\double\date\datetime的特殊值列表
     * @param fieldTypeList 特殊值列表
     * @param calculateContext  计算相关数据
     * @param result 结果VO
     * @param fieldType 字段类型
     * @param empty  是否有为空类型操作
     * @param operatorLost 操作符列表
     * @return 特殊值列表
     */
    public static List<Object> getIntDoubleDataDataTimeEqual(List<ConfigSpecialMappingVo> fieldTypeList,IndexCalculateContext calculateContext,StatisticsResultVo result,String fieldType,Boolean empty,List<String> operatorLost) {
        List<Object> dataIsSpecialValueList = new ArrayList<>();
        //将操作类型进行分类 0为只有= 1为只有>/>=和= 2为只有</<=和= 3为其他(至少有一对</<= >/>=)
        int condition = determineCondition(operatorLost);
        if (condition == MagicNumbers.ZERO) {
            dataIsSpecialValueList = getOnlyEqual(fieldTypeList,calculateContext,result,fieldType,empty);
        } else if (condition == MagicNumbers.ONE) {
            dataIsSpecialValueList = getGreater(fieldTypeList,calculateContext,result,fieldType,empty);
        } else if (condition == MagicNumbers.TWO) {
            dataIsSpecialValueList = getLess(fieldTypeList,calculateContext,result,fieldType,empty);
        } else if (condition == MagicNumbers.THREE) {
            dataIsSpecialValueList = getLessAndGreater(fieldTypeList,calculateContext,result,fieldType,empty);
        }

        return dataIsSpecialValueList;

    }

    /**
     * 获取int/double特殊值列表
     * @param result 结果VO
     * @param calculateContext 计算所需数据
     * @param fieldType 字段类型
     * @return  特殊值列表
     */
    public static List<Double> getSpecialNumberValuesList(StatisticsResultVo result, IndexCalculateContext calculateContext,String fieldType) {
        List<Object> dataIsSpecialValueList;
        List<Double> doubleList = new ArrayList<>();

        if (VarDataTypeEnum.INTEGER.getDesc().equals(fieldType) || VarDataTypeEnum.DOUBLE.getDesc().equals(fieldType)) {
            List<ConfigSpecialMappingVo> specialMappingVoList = calculateContext.getSpecialMappingVoList();
            if (specialMappingVoList.size() == MagicNumbers.ONE && specialMappingVoList.get(0).getDataType() == null) {
                return  doubleList;
            }
            //对所有特殊值按照类型进行分组
            Map<VarDataTypeEnum, List<ConfigSpecialMappingVo>> groupedDataType = specialMappingVoList.stream()
                    .collect(Collectors.<ConfigSpecialMappingVo, VarDataTypeEnum>groupingBy(ConfigSpecialMappingVo::getDataType));
            //获取当前字类型段的特殊值list
            List<ConfigSpecialMappingVo> fieldTypeList = groupedDataType.get(VarDataTypeEnum.getEnumFromDesc(fieldType));
            if (CollectionUtils.isEmpty(fieldTypeList)) {
                return  doubleList;
            }
            //获取所有操作类型
            List<String> operatorLost = extractOperator(fieldTypeList);

            dataIsSpecialValueList = getIntDoubleDataDataTimeEqual(fieldTypeList, calculateContext, result, fieldType, Boolean.FALSE, operatorLost);


        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, fieldType + "类型不允许获取特殊值列表");
        }


        if (!CollectionUtils.isEmpty(dataIsSpecialValueList)) {
            doubleList = dataIsSpecialValueList.stream()
                    .filter(obj -> obj instanceof Number)
                    .map(obj -> ((Number) obj).doubleValue())
                    .collect(Collectors.toList());
            return doubleList;

        } else {
            return doubleList;
        }

    }

    @Override
    public String getIndexName() {
        return AnalysisIndexEnum.SPECIAL_RATIO.getCode();
    }

    /**
     * 获取操作类型
     * @param dataTypeList  特殊值list
     * @return 操作类型list
     */
    public static List<String> extractOperator(List<ConfigSpecialMappingVo> dataTypeList) {
        List<String> operatorTypesList = new ArrayList<>();
        if (CollectionUtils.isEmpty(dataTypeList)) {
            return  operatorTypesList;
        } else {
            for (ConfigSpecialMappingVo specialMappingVo : dataTypeList) {
                String operator = specialMappingVo.getOperator().getDesc();
                operatorTypesList.add(operator);
            }

            return operatorTypesList;
        }

    }

    /**
     * 判断操作类型
     * @param operatorTypesList 操作符列表
     * @return 类型
     */
    public static int determineCondition(List<String> operatorTypesList) {
        boolean containsGreaterThan = false;
        boolean containsLessThan = false;
        boolean containsEquals = false;

        for (String operatorType : operatorTypesList) {
            if (operatorType.startsWith(VarMathSymbolTypeEnum.GREATER_EQUAL.getDesc()) || operatorType.startsWith(VarMathSymbolTypeEnum.MORE_THAN.getDesc())) {
                containsGreaterThan = true;
            } else if (operatorType.startsWith(VarMathSymbolTypeEnum.LESS_EQUAL.getDesc()) || operatorType.startsWith(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
                containsLessThan = true;
            } else if (operatorType.startsWith("=")) {
                containsEquals = true;
            }
        }

        if (containsEquals && !containsGreaterThan && !containsLessThan) {
            return MagicNumbers.ZERO;
        } else if (operatorTypesList.size() == 1 && operatorTypesList.get(0).startsWith(VarMathSymbolTypeEnum.EMPTY.getDesc())) {
            return MagicNumbers.ZERO;
        } else if (containsGreaterThan  && !containsLessThan) {
            return MagicNumbers.ONE;
        } else if (containsLessThan && !containsGreaterThan) {
            return MagicNumbers.TWO;
        } else {
            return MagicNumbers.THREE;
        }
    }

    /**
     * 获取最大值 例如大于1,大于5 取大于1
     * @param intTypeList 所有特殊值
     * @param fieldType 字段类型
     * @return 最小的大于值
     */
    public static List<ConfigSpecialMappingVo> filterMinOperatorValues(List<ConfigSpecialMappingVo> intTypeList,String fieldType) {
        List<ConfigSpecialMappingVo> filteredList = new ArrayList<>();
        for (ConfigSpecialMappingVo configSpecialMappingVo : intTypeList) {
            String operatorCondition = configSpecialMappingVo.getOperator().getDesc();

            if (operatorCondition.startsWith(VarMathSymbolTypeEnum.GREATER_EQUAL.getDesc()) || operatorCondition.startsWith(VarMathSymbolTypeEnum.MORE_THAN.getDesc())) {
                if (filteredList.isEmpty()) {
                    filteredList.add(configSpecialMappingVo);
                } else {
                    if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                        ConfigSpecialMappingVo minConfigSpecialMappingVo = filteredList.get(0);
                        if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) < Double.parseDouble(minConfigSpecialMappingVo.getSpecialVal())) {
                            filteredList.clear();
                            filteredList.add(configSpecialMappingVo);
                        } else if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) == Double.parseDouble(minConfigSpecialMappingVo.getSpecialVal())) {
                            if (configSpecialMappingVo.getOperator().getDesc().startsWith(VarMathSymbolTypeEnum.GREATER_EQUAL.getDesc())) {
                                filteredList.clear();
                                filteredList.add(configSpecialMappingVo);
                            }
                        }
                    }
                    if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                        ConfigSpecialMappingVo minConfigSpecialMappingVo = filteredList.get(0);
                        if (configSpecialMappingVo.getSpecialVal() != null && parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) < parseStringToTimestamp(minConfigSpecialMappingVo.getSpecialVal())) {
                            filteredList.clear();
                            filteredList.add(configSpecialMappingVo);
                        } else if (configSpecialMappingVo.getSpecialVal() != null && parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) == parseStringToTimestamp(minConfigSpecialMappingVo.getSpecialVal())) {
                            if (configSpecialMappingVo.getOperator().getDesc().startsWith(VarMathSymbolTypeEnum.GREATER_EQUAL.getDesc())) {
                                filteredList.clear();
                                filteredList.add(configSpecialMappingVo);
                            }
                        }
                    }

                }
            }
        }
        return filteredList;
    }

    /**
     * 获取大于等于情况中 等于的值
     * @param intTypeList 所有特殊值LIST
     * @param greaterThanList 大于等于的唯一list
     * @param fieldType 字段类型
     * @return list
     */
    public static List<Object> getGreatCondition(List<ConfigSpecialMappingVo> intTypeList,List<ConfigSpecialMappingVo> greaterThanList,String fieldType) {
        List<Object> newList = new ArrayList<>();
        for (ConfigSpecialMappingVo configSpecialMappingVo : intTypeList) {
            String operatorCondition = configSpecialMappingVo.getOperator().getDesc();
            //例如大于的情况为>6  =3 =4  将3和4 装入list
            if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                if (operatorCondition.startsWith("=") &&  configSpecialMappingVo.getSpecialVal() != null) {
                    if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) == Double.parseDouble(greaterThanList.get(0).getSpecialVal()) && greaterThanList.get(0).getOperator().getDesc().equals(VarMathSymbolTypeEnum.MORE_THAN.getDesc())) {
                        newList.add(configSpecialMappingVo.getSpecialVal());
                    }
                    if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) < Double.parseDouble(greaterThanList.get(0).getSpecialVal())) {
                        newList.add(configSpecialMappingVo.getSpecialVal());
                    }
                }
                if (operatorCondition.startsWith("=") &&  configSpecialMappingVo.getSpecialVal() == null) {
                    newList.add(null);
                }
            }
            if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                if (operatorCondition.startsWith("=") &&  configSpecialMappingVo.getSpecialVal() != null) {
                    if (parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) == parseStringToTimestamp(greaterThanList.get(0).getSpecialVal()) && greaterThanList.get(0).getOperator().getDesc().equals(VarMathSymbolTypeEnum.MORE_THAN.getDesc())) {
                        newList.add(configSpecialMappingVo.getSpecialVal());
                    }
                    if (parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) < parseStringToTimestamp(greaterThanList.get(0).getSpecialVal())) {
                        newList.add(configSpecialMappingVo.getSpecialVal());
                    }
                }
                if (operatorCondition.startsWith("=") &&  configSpecialMappingVo.getSpecialVal() == null) {
                    newList.add(null);
                }
            }
        }
        return newList;
    }
    /**
     * 获取小于等于和等于情况中 等于的值
     * @param intTypeList 所有特殊值LIST
     * @param fieldType 字段类型
     * @param greaterThanList 小于等于的唯一list
     * @return list
     */
    public static List<Object> getLessCondition(List<ConfigSpecialMappingVo> intTypeList,List<ConfigSpecialMappingVo> greaterThanList,String fieldType) {
        List<Object> newList = new ArrayList<>();
        for (ConfigSpecialMappingVo configSpecialMappingVo : intTypeList) {
            String operatorCondition = configSpecialMappingVo.getOperator().getDesc();
            //例如最小的情况为<5  =7 =8  将7和8 装入list
            if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                if (operatorCondition.startsWith("=") &&  configSpecialMappingVo.getSpecialVal() != null) {
                    if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) == Double.parseDouble(greaterThanList.get(0).getSpecialVal()) && greaterThanList.get(0).getOperator().getDesc().equals(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
                        newList.add(configSpecialMappingVo.getSpecialVal());
                    }
                    if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) > Double.parseDouble(greaterThanList.get(0).getSpecialVal())) {
                        newList.add(configSpecialMappingVo.getSpecialVal());
                    }
                }
                if (operatorCondition.startsWith("=") &&  configSpecialMappingVo.getSpecialVal() == null) {
                    newList.add(configSpecialMappingVo.getSpecialVal());
                }
            }
            if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                if (operatorCondition.startsWith("=") &&  configSpecialMappingVo.getSpecialVal() != null) {
                    if (parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) == parseStringToTimestamp(greaterThanList.get(0).getSpecialVal()) && greaterThanList.get(0).getOperator().getDesc().equals(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
                        newList.add(configSpecialMappingVo.getSpecialVal());
                    }
                    if (parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) > parseStringToTimestamp(greaterThanList.get(0).getSpecialVal())) {
                        newList.add(configSpecialMappingVo.getSpecialVal());
                    }
                }
                if (operatorCondition.startsWith("=") &&  configSpecialMappingVo.getSpecialVal() == null) {
                    newList.add(configSpecialMappingVo.getSpecialVal());
                }
            }
        }
        return newList;
    }

    /**
     * 获取最大值 例如小于1,小于5 取小于5
     * @param intTypeList 所有特殊值
     * @param fieldType 字段类型
     * @return 最大的小于值
     */
    public static List<ConfigSpecialMappingVo> filterMaxOperatorValues(List<ConfigSpecialMappingVo> intTypeList,String fieldType) {
        List<ConfigSpecialMappingVo> filteredList = new ArrayList<>();
        for (ConfigSpecialMappingVo configSpecialMappingVo : intTypeList) {
            String operatorCondition = configSpecialMappingVo.getOperator().getDesc();

            if (operatorCondition.startsWith(VarMathSymbolTypeEnum.LESS_EQUAL.getDesc()) || operatorCondition.startsWith(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
                if (filteredList.isEmpty()) {
                    filteredList.add(configSpecialMappingVo);
                } else {
                    if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                        ConfigSpecialMappingVo minConfigSpecialMappingVo = filteredList.get(0);
                        if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) > Double.parseDouble(minConfigSpecialMappingVo.getSpecialVal())) {
                            filteredList.clear();
                            filteredList.add(configSpecialMappingVo);
                        } else if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) == Double.parseDouble(minConfigSpecialMappingVo.getSpecialVal())) {
                            if (configSpecialMappingVo.getOperator().getDesc().startsWith(VarMathSymbolTypeEnum.LESS_EQUAL.getDesc())) {
                                filteredList.clear();
                                filteredList.add(configSpecialMappingVo);
                            }
                        }
                    }
                    if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                        ConfigSpecialMappingVo minConfigSpecialMappingVo = filteredList.get(0);
                        if (configSpecialMappingVo.getSpecialVal() != null && parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) > parseStringToTimestamp(minConfigSpecialMappingVo.getSpecialVal())) {
                            filteredList.clear();
                            filteredList.add(configSpecialMappingVo);
                        } else if (configSpecialMappingVo.getSpecialVal() != null && parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) == parseStringToTimestamp(minConfigSpecialMappingVo.getSpecialVal())) {
                            if (configSpecialMappingVo.getOperator().getDesc().startsWith(VarMathSymbolTypeEnum.LESS_EQUAL.getDesc())) {
                                filteredList.clear();
                                filteredList.add(configSpecialMappingVo);
                            }
                        }
                    }

                }
            }
        }
        return filteredList;
    }

    /**
     * 计算Boolen类型特殊值
     * @param fieldTypeList 数据
     * @param calculateContext 计算所需vo
     * @param result 结果vo
     * @param fieldType 字段类型
     * @return 数据中的特殊值
     */
    public static List<Object> getBoolenEqual(List<ConfigSpecialMappingVo> fieldTypeList,IndexCalculateContext calculateContext,StatisticsResultVo result,String fieldType) {
        List<Object> dataIsSpecialValueList = new ArrayList<>();
        //获取特殊值
        List<Object> newList = new ArrayList<>();
        for (ConfigSpecialMappingVo configSpecialMappingVo : fieldTypeList) {
            if ("0".equals(configSpecialMappingVo.getSpecialVal().toString()) || "false".equals(configSpecialMappingVo.getSpecialVal().toString())) {
                newList.add("false");
            }
            if ("1".equals(configSpecialMappingVo.getSpecialVal().toString()) || "true".equals(configSpecialMappingVo.getSpecialVal().toString())) {
                newList.add("true");
            }

        }
        //获取数据
        List<Map<String, Object>> dataList = calculateContext.getDataList();
        List<Object> indexCodeDataList = getData(dataList, result);
        dataIsSpecialValueList = getAllEqual(indexCodeDataList, newList,dataIsSpecialValueList,fieldType);

        return dataIsSpecialValueList;

    }

    /**
     * 计算String类型特殊值
     * @param fieldTypeList 数据
     * @param calculateContext 计算所需vo
     * @param result 结果vo
     * @param fieldType 字段类型
     * @param empty 是否有空值
     * @return 数据中的特殊值
     */
    public static List<Object> getStringEqual(List<ConfigSpecialMappingVo> fieldTypeList,IndexCalculateContext calculateContext,StatisticsResultVo result,String fieldType,Boolean empty) {
        List<Object> dataIsSpecialValueList = new ArrayList<>();
        //获取特殊值
        List<Object> newList = new ArrayList<>();
        List<Object> includeList = new ArrayList<>();
        for (ConfigSpecialMappingVo configSpecialMappingVo : fieldTypeList) {
            if (configSpecialMappingVo.getOperator().getDesc().startsWith("=")) {
                newList.add(configSpecialMappingVo.getSpecialVal());
            }
            if (configSpecialMappingVo.getOperator().getDesc().startsWith(VarMathSymbolTypeEnum.INCLUDE.getDesc())) {
                includeList.add(configSpecialMappingVo.getSpecialVal());
            }

        }
        if (empty) {
            newList.add(null);
        }
        //获取数据
        List<Map<String, Object>> dataList = calculateContext.getDataList();
        List<Object> indexCodeDataList = getData(dataList, result);
        for (Object dataValue : indexCodeDataList) {
            if (dataValue == null) {
                if (newList.contains(null)) {
                    dataIsSpecialValueList.add(null);
                }
            } else {
                if (newList.contains(dataValue) || determineStringContains(dataValue, includeList)) {
                    dataIsSpecialValueList.add(dataValue);
                }
            }

        }

        return  dataIsSpecialValueList;
    }

    /**
     * 是否包含特殊值
     * @param dataValue  数据
     * @param includeList 特殊值
     * @return 是否包含
     */
    public static boolean determineStringContains(Object dataValue, List<Object> includeList) {
        boolean isInclude = false;
        if (!CollectionUtils.isEmpty(includeList)) {
            for (Object include : includeList) {
                if (include != null && dataValue != null && dataValue.toString().contains(include.toString())) {
                    isInclude = true;
                }
            }
        }
        return isInclude;
    }
    /**
     * 计算只有等于的情况
     * @param fieldTypeList 所有特殊值对象
     * @param calculateContext 计算所需信息
     * @param fieldType  字段类型
     * @param empty 是否有空值
     * @param result 结果
     * @return 数据中的特殊值
     */
    public static List<Object> getOnlyEqual(List<ConfigSpecialMappingVo> fieldTypeList,IndexCalculateContext calculateContext,StatisticsResultVo result,String fieldType,Boolean empty) {
        List<Object> dataIsSpecialValueList = new ArrayList<>();
        //获取特殊值
        List<Object> specialValueList = new ArrayList<>();
        for (ConfigSpecialMappingVo configSpecialMappingVo : fieldTypeList) {
            if (configSpecialMappingVo.getOperator().getDesc().startsWith("=")) {
                specialValueList.add(configSpecialMappingVo.getSpecialVal());
            }
        }
        if (Boolean.TRUE.equals(empty)) {
            specialValueList.add(null);
        }
        //获取数据
        //获取数据
        List<Map<String, Object>> allDataList = calculateContext.getDataList();
        List<Object> dataList = getData(allDataList,result);
        if (CollectionUtils.isNotEmpty(specialValueList)) {
            return getAllEqual(dataList, specialValueList,dataIsSpecialValueList,fieldType);
        } else {
            return dataIsSpecialValueList;
        }


    }
    /**
     * 计算等于和大于或大于等于的情况
     * @param fieldTypeList 所有特殊值对象
     * @param calculateContext 计算所需信息
     * @param fieldType  字段类型
     * @param empty 是否有空值
     * @param result 结果
     * @return 数据中的特殊值
     */
    public static List<Object> getGreater(List<ConfigSpecialMappingVo> fieldTypeList,IndexCalculateContext calculateContext,StatisticsResultVo result,String fieldType,Boolean empty) {
        List<Object> dataIsSpecialValueList = new ArrayList<>();
        //获取>/>=   >3 >5 取>3
        List<ConfigSpecialMappingVo> greaterThanList = filterMinOperatorValues(fieldTypeList,fieldType);
        //获取=
        List<Object> greatCondition = getGreatCondition(fieldTypeList, greaterThanList,fieldType);
        if (Boolean.TRUE.equals(empty)) {
            greatCondition.add(null);
        }

        //获取数据
        List<Map<String, Object>> dataList = calculateContext.getDataList();
        List<Object> indexCodeDataList = getData(dataList,result);
        for (Object indexCodeData : indexCodeDataList) {
            if (indexCodeData != null) {
                if (VarMathSymbolTypeEnum.GREATER_EQUAL.equals(greaterThanList.get(0).getOperator())) {
                    if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                        if (Double.parseDouble(indexCodeData.toString()) >= Double.parseDouble(greaterThanList.get(0).getSpecialVal())) {
                            dataIsSpecialValueList.add(indexCodeData);
                        }
                    }
                    if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                        if (parseStringToTimestamp(convertDateFormat(indexCodeData.toString())) >= parseStringToTimestamp(greaterThanList.get(0).getSpecialVal())) {
                            dataIsSpecialValueList.add(indexCodeData);
                        }
                    }


                } else if (VarMathSymbolTypeEnum.MORE_THAN.equals(greaterThanList.get(0).getOperator())) {
                    if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                        if (Double.parseDouble(indexCodeData.toString()) > Double.parseDouble(greaterThanList.get(0).getSpecialVal())) {
                            dataIsSpecialValueList.add(indexCodeData);
                        }
                    }
                    if (fieldType.equals(VarDataTypeEnum.DATETIME.getDesc()) || fieldType.equals(VarDataTypeEnum.DATE.getDesc())) {
                        if (parseStringToTimestamp(convertDateFormat(indexCodeData.toString())) > parseStringToTimestamp(greaterThanList.get(0).getSpecialVal())) {
                            dataIsSpecialValueList.add(indexCodeData);
                        }
                    }
                }
            }
        }

        if (greatCondition.size() == 0) {
            return dataIsSpecialValueList;
        } else {
            return getAllEqual(indexCodeDataList, greatCondition,dataIsSpecialValueList, fieldType);
        }

    }
    /**
     * 计算等于和小于或小于等于的情况
     * @param fieldTypeList 所有特殊值对象
     * @param calculateContext 计算所需信息
     * @param fieldType  字段类型
     * @param empty 是否有空值
     * @param result 结果
     * @return 数据中的特殊值
     */
    public static List<Object> getLess(List<ConfigSpecialMappingVo> fieldTypeList,IndexCalculateContext calculateContext,StatisticsResultVo result,String fieldType,Boolean empty) {
        List<Object> dataIsSpecialValueList = new ArrayList<>();
        //获取</<=
        List<ConfigSpecialMappingVo> greaterThanList = filterMaxOperatorValues(fieldTypeList,fieldType);
        //获取=
        List<Object> lessCondition = getLessCondition(fieldTypeList, greaterThanList,fieldType);
        if (Boolean.TRUE.equals(empty)) {
            lessCondition.add(null);
        }
        //获取数据
        List<Map<String, Object>> dataList = calculateContext.getDataList();
        List<Object> indexCodeDataList = getData(dataList,result);

        for (Object indexCodeData : indexCodeDataList) {
            if (indexCodeData != null) {
                if (VarMathSymbolTypeEnum.LESS_EQUAL.equals(greaterThanList.get(0).getOperator())) {
                    if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                        if (Double.parseDouble(indexCodeData.toString()) <= Double.parseDouble(greaterThanList.get(0).getSpecialVal())) {
                            dataIsSpecialValueList.add(indexCodeData);
                        }
                    }
                    if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                        if (parseStringToTimestamp(convertDateFormat(indexCodeData.toString())) <= parseStringToTimestamp(greaterThanList.get(0).getSpecialVal())) {
                            dataIsSpecialValueList.add(convertDateFormat(indexCodeData.toString()));
                        }
                    }


                } else if (VarMathSymbolTypeEnum.LESS_THAN.equals(greaterThanList.get(0).getOperator())) {
                    if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                        if (Double.parseDouble(indexCodeData.toString()) < Double.parseDouble(greaterThanList.get(0).getSpecialVal())) {
                            dataIsSpecialValueList.add(indexCodeData);
                        }
                    }
                    if (fieldType.equals(VarDataTypeEnum.DATETIME.getDesc()) || fieldType.equals(VarDataTypeEnum.DATE.getDesc())) {
                        if (parseStringToTimestamp(convertDateFormat(indexCodeData.toString())) < parseStringToTimestamp(greaterThanList.get(0).getSpecialVal())) {
                            dataIsSpecialValueList.add(indexCodeData);
                        }
                    }
                }
            }
        }
        if (lessCondition.size() == 0) {
            return dataIsSpecialValueList;
        } else {
            return getAllEqual(indexCodeDataList, lessCondition,dataIsSpecialValueList, fieldType);
        }

    }

    /**
     * 计算有小于或小于等于和大于或大于等于和等于的情况
     * @param fieldTypeList 所有特殊值对象
     * @param calculateContext 计算所需信息
     * @param fieldType  字段类型
     * @param empty 是否有空值
     * @param result 结果
     * @return 数据中的特殊值
     */
    public static List<Object> getLessAndGreater(List<ConfigSpecialMappingVo> fieldTypeList,IndexCalculateContext calculateContext,StatisticsResultVo result,String fieldType,Boolean empty) {
        List<Object> dataIsSpecialValueList = new ArrayList<>();
        //获取>/>=
        List<ConfigSpecialMappingVo> greaterThanList  = filterMinOperatorValues(fieldTypeList,fieldType);
        //获取</<=
        List<ConfigSpecialMappingVo> lessList = filterMaxOperatorValues(fieldTypeList,fieldType);

        String maxValue = lessList.get(0).getSpecialVal();
        String minValue = greaterThanList.get(0).getSpecialVal();
        String minType = greaterThanList.get(0).getOperator().getDesc();
        String maxType = lessList.get(0).getOperator().getDesc();
        List<Object> equalList = getEqualList(fieldType,maxType,maxValue,minType,minValue,fieldTypeList);
        if (Boolean.TRUE.equals(empty)) {
            equalList.add(null);
        }

        //获取数据
        List<Map<String, Object>> dataList = calculateContext.getDataList();
        List<Object> indexCodeDataList = getData(dataList,result);
        boolean minLessMax = false;
        if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
            minLessMax = Double.parseDouble(minValue) < Double.parseDouble(maxValue) ? true :  false;
        } else {
            minLessMax = parseStringToTimestamp(minValue) < parseStringToTimestamp(maxValue) ? true :  false;
        }

        if (minLessMax) {
            if (CollectionUtils.isNotEmpty(equalList)) {
                dataIsSpecialValueList = getIntersection(indexCodeDataList,maxType,maxValue,minType,minValue,dataIsSpecialValueList,fieldType);
                dataIsSpecialValueList = getAllEqual(indexCodeDataList,equalList,dataIsSpecialValueList,fieldType);
            } else {
                dataIsSpecialValueList = getIntersection(indexCodeDataList,maxType,maxValue,minType,minValue,dataIsSpecialValueList,fieldType);
            }

        } else {
            if (CollectionUtils.isNotEmpty(equalList)) {
                dataIsSpecialValueList = getNoIntersection(indexCodeDataList,maxType,maxValue,minType,minValue,dataIsSpecialValueList,fieldType);
                dataIsSpecialValueList = getAllEqual(indexCodeDataList,equalList,dataIsSpecialValueList,fieldType);
            } else {
                dataIsSpecialValueList = getNoIntersection(indexCodeDataList,maxType,maxValue,minType,minValue,dataIsSpecialValueList,fieldType);

            }

        }

        return  dataIsSpecialValueList;

    }



    /**
     * 没有交集的计算
     * @param indexCodeDataList 所有数据
     * @param maxType 最大值类型
     * @param maxValue 最大值
     * @param minType 最小值类型
     * @param minValue 最小值
     * @param fieldType  字段类型
     * @param dataIsSpecialValueList 是特殊值的数据
     * @return 特殊值数量
     */
    public static List<Object> getNoIntersection(List<Object> indexCodeDataList,String maxType, String maxValue,String minType,String minValue,List<Object> dataIsSpecialValueList,String fieldType) {

        if (minType.equals(VarMathSymbolTypeEnum.MORE_THAN.getDesc()) && maxType.equals(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
            for (Object dataValue : indexCodeDataList) {
                if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                    if (dataValue != null) {
                        if (Double.parseDouble(dataValue.toString()) < Double.parseDouble(maxValue) || Double.parseDouble(dataValue.toString()) > Double.parseDouble(minValue)) {
                            dataIsSpecialValueList.add(dataValue);
                        }
                    }
                }
                if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                    if (dataValue != null) {
                        if (parseStringToTimestamp(convertDateFormat(dataValue.toString())) < parseStringToTimestamp(maxValue) || parseStringToTimestamp(convertDateFormat(dataValue.toString())) > parseStringToTimestamp(minValue)) {
                            dataIsSpecialValueList.add(dataValue);
                        }
                    }
                }

            }
        } else if (minType.equals(VarMathSymbolTypeEnum.MORE_THAN.getDesc()) && maxType.equals(VarMathSymbolTypeEnum.LESS_EQUAL.getDesc())) {
            for (Object dataValue : indexCodeDataList) {
                if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                    if (dataValue != null) {
                        if (Double.parseDouble(dataValue.toString()) <= Double.parseDouble(maxValue) || Double.parseDouble(dataValue.toString()) > Double.parseDouble(minValue)) {
                            dataIsSpecialValueList.add(dataValue);
                        }
                    }
                }
                if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                    if (dataValue != null) {
                        if ((parseStringToTimestamp(convertDateFormat(dataValue.toString())) <= parseStringToTimestamp(maxValue) || parseStringToTimestamp(convertDateFormat(dataValue.toString())) > parseStringToTimestamp(minValue))) {
                            dataIsSpecialValueList.add(dataValue);
                        }
                    }
                }

            }
        } else if (minType.equals(VarMathSymbolTypeEnum.GREATER_EQUAL.getDesc()) && maxType.equals(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
            for (Object dataValue : indexCodeDataList) {
                if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                    if (dataValue != null) {
                        if (Double.parseDouble(dataValue.toString()) < Double.parseDouble(maxValue) || Double.parseDouble(dataValue.toString()) >= Double.parseDouble(minValue)) {
                            dataIsSpecialValueList.add(dataValue);
                        }
                    }
                }
                if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                    if (dataValue != null) {
                        if (parseStringToTimestamp(convertDateFormat(dataValue.toString())) < parseStringToTimestamp(maxValue) || parseStringToTimestamp(convertDateFormat(dataValue.toString())) >= parseStringToTimestamp(minValue)) {
                            dataIsSpecialValueList.add(dataValue);
                        }
                    }
                }

            }
        } else if (minType.equals(VarMathSymbolTypeEnum.GREATER_EQUAL.getDesc()) && maxType.equals(VarMathSymbolTypeEnum.LESS_EQUAL.getDesc())) {
            for (Object dataValue : indexCodeDataList) {
                if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                    if (dataValue != null) {
                        if (Double.parseDouble(dataValue.toString()) <= Double.parseDouble(maxValue) || Double.parseDouble(dataValue.toString()) >= Double.parseDouble(minValue)) {
                            dataIsSpecialValueList.add(dataValue);
                        }
                    }
                }
                if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                    if (dataValue != null) {
                        if (parseStringToTimestamp(convertDateFormat(dataValue.toString())) <= parseStringToTimestamp(maxValue) || parseStringToTimestamp(convertDateFormat(dataValue.toString())) >= parseStringToTimestamp(minValue)) {
                            dataIsSpecialValueList.add(dataValue);
                        }
                    }
                }

            }
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "暂不支持该条件");
        }
        return dataIsSpecialValueList;
    }

    /**
     * 计算特殊值在交集内的数量
     * @param indexCodeDataList 所有数据
     * @param maxType 最大值类型
     * @param maxValue 最大值
     * @param minType 最小值类型
     * @param minValue 最小值
     * @param dataIsSpecialValueList 是特殊值的数据
     * @param fieldType  字段类型
     * @return 数量
     */
    public static List<Object> getIntersection(List<Object> indexCodeDataList,String maxType, String maxValue,String minType,String minValue,List<Object> dataIsSpecialValueList,String fieldType) {
        if (minType.equals(VarMathSymbolTypeEnum.MORE_THAN.getDesc()) && maxType.equals(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
            for (Object dataValue : indexCodeDataList) {
                if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                    if (dataValue != null && Double.parseDouble(dataValue.toString()) < Double.parseDouble(maxValue) && Double.parseDouble(dataValue.toString()) > Double.parseDouble(minValue)) {
                        dataIsSpecialValueList.add(dataValue);
                    }
                }
                if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                    if (dataValue != null && parseStringToTimestamp(convertDateFormat(dataValue.toString())) < parseStringToTimestamp(maxValue) && parseStringToTimestamp(convertDateFormat(dataValue.toString())) > parseStringToTimestamp(minValue)) {
                        dataIsSpecialValueList.add(dataValue);
                    }
                }
            }
        } else if (minType.equals(VarMathSymbolTypeEnum.MORE_THAN.getDesc()) && maxType.equals(VarMathSymbolTypeEnum.LESS_EQUAL.getDesc())) {
            for (Object dataValue : indexCodeDataList) {
                if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                    if (dataValue != null && Double.parseDouble(dataValue.toString()) <= Double.parseDouble(maxValue) && Double.parseDouble(dataValue.toString()) > Double.parseDouble(minValue)) {
                        dataIsSpecialValueList.add(dataValue);
                    }
                }
                if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                    if (dataValue != null && parseStringToTimestamp(convertDateFormat(dataValue.toString())) <= parseStringToTimestamp(maxValue) && parseStringToTimestamp(convertDateFormat(dataValue.toString())) > parseStringToTimestamp(minValue)) {
                        dataIsSpecialValueList.add(dataValue);
                    }
                }

            }
        } else if (minType.equals(VarMathSymbolTypeEnum.GREATER_EQUAL.getDesc()) && maxType.equals(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
            for (Object dataValue : indexCodeDataList) {
                if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                    if (dataValue != null && Double.parseDouble(dataValue.toString()) < Double.parseDouble(maxValue) && Double.parseDouble(dataValue.toString()) >= Double.parseDouble(minValue)) {
                        dataIsSpecialValueList.add(dataValue);
                    }
                }
                if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                    if (dataValue != null && parseStringToTimestamp(convertDateFormat(dataValue.toString())) < parseStringToTimestamp(maxValue) && parseStringToTimestamp(convertDateFormat(dataValue.toString())) >= parseStringToTimestamp(minValue)) {
                        dataIsSpecialValueList.add(dataValue);
                    }
                }

            }
        } else if (minType.equals(VarMathSymbolTypeEnum.GREATER_EQUAL.getDesc()) && maxType.equals(VarMathSymbolTypeEnum.LESS_EQUAL.getDesc())) {
            for (Object dataValue : indexCodeDataList) {
                if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                    if (dataValue != null && Double.parseDouble(dataValue.toString()) <= Double.parseDouble(maxValue) && Double.parseDouble(dataValue.toString()) >= Double.parseDouble(minValue)) {
                        dataIsSpecialValueList.add(dataValue);
                    }
                }
                if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                    if (dataValue != null && parseStringToTimestamp(convertDateFormat(dataValue.toString())) <= parseStringToTimestamp(maxValue) && parseStringToTimestamp(convertDateFormat(dataValue.toString())) >= parseStringToTimestamp(minValue)) {
                        dataIsSpecialValueList.add(dataValue);
                    }
                }

            }
        } else {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "暂不支持该条件");
        }
        return dataIsSpecialValueList;
    }

    /**
     *  计算所有类型的等于特殊值
     * @param dataList 数据
     * @param specialValueList 特殊值
     * @param dataIsSpecialValueList 是特殊值的数据
     * @param fieldType 字段类型
     * @return 特殊值数量
     */
    public static List<Object> getAllEqual(List<Object> dataList, List<Object> specialValueList,List<Object> dataIsSpecialValueList,String fieldType) {

        switch (VarDataTypeEnum.getEnumFromDesc(fieldType)) {
            case STRING:
                dataIsSpecialValueList = getStringEqual(dataList, specialValueList,dataIsSpecialValueList);
                break;
            case INTEGER:
                dataIsSpecialValueList = getIntEqual(dataList, specialValueList,dataIsSpecialValueList);
                break;
            case DOUBLE:
                dataIsSpecialValueList = getDoubleEqual(dataList, specialValueList,dataIsSpecialValueList);
                break;
            case DATE:
                dataIsSpecialValueList = getDateEqual(dataList, specialValueList,dataIsSpecialValueList);
                break;
            case DATETIME:
                dataIsSpecialValueList = getDateEqual(dataList, specialValueList,dataIsSpecialValueList);
                break;
            case BOOLEAN:
                dataIsSpecialValueList = getBoolenEqual(dataList, specialValueList,dataIsSpecialValueList);
                break;
            default:
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "不支持的字段类型");
        }
        return  dataIsSpecialValueList;
    }
    /**
     *  计算Int类型的等于特殊值
     * @param indexCodeDataList 数据
     * @param combinedList 特殊值
     * @param dataIsSpecialValueList 是特殊值的数据
     * @return 特殊值数量
     */
    public static List<Object> getIntEqual(List<Object> indexCodeDataList, List<Object> combinedList,List<Object> dataIsSpecialValueList) {
        List<Integer> convertedList = new ArrayList<>();
        for (Object specialValue : combinedList) {
            if (specialValue == null) {
                convertedList.add(null);
            } else {
                convertedList.add(Integer.parseInt(specialValue.toString()));
            }
        }
        for (Object dataValue : indexCodeDataList) {
            if (dataValue != null && convertedList.contains(Integer.parseInt(dataValue.toString()))) {
                dataIsSpecialValueList.add(dataValue);
            } else {
                if (convertedList.contains(dataValue)) {
                    dataIsSpecialValueList.add(dataValue);
                }
            }
        }
        return dataIsSpecialValueList;
    }
    /**
     *  计算String类型的等于特殊值
     * @param indexCodeDataList 数据
     * @param combinedList 特殊值
     * @param dataIsSpecialValueList 是特殊值的数据
     * @return 特殊值数量
     */
    public static List<Object> getStringEqual(List<Object> indexCodeDataList, List<Object> combinedList,List<Object> dataIsSpecialValueList) {

        for (Object dataValue : indexCodeDataList) {
            if (dataValue != null && combinedList.contains(dataValue.toString())) {
                dataIsSpecialValueList.add(dataValue);
            } else {
                if (combinedList.contains(dataValue)) {
                    dataIsSpecialValueList.add(dataValue);
                }
            }
        }
        return dataIsSpecialValueList;
    }
    /**
     *  计算Double类型的等于特殊值
     * @param indexCodeDataList 数据
     * @param combinedList 特殊值
     * @param dataIsSpecialValueList 是特殊值的数据
     * @return 特殊值数量
     */
    public static List<Object> getDoubleEqual(List<Object> indexCodeDataList, List<Object> combinedList,List<Object> dataIsSpecialValueList) {
        List<Double> convertedList = new ArrayList<>();
        for (Object specialValue : combinedList) {
            if (specialValue == null) {
                convertedList.add(null);
            } else {
                convertedList.add(Double.parseDouble(specialValue.toString()));
            }
        }
        for (Object dataValue : indexCodeDataList) {
            if (dataValue != null && convertedList.contains(dataValue)) {
                dataIsSpecialValueList.add(dataValue);
            } else {
                if (convertedList.contains(dataValue)) {
                    dataIsSpecialValueList.add(dataValue);
                }
            }
        }
        return dataIsSpecialValueList;
    }
    /**
     *  计算boolen类型的等于特殊值
     * @param indexCodeDataList 数据
     * @param combinedList 特殊值
     * @param dataIsSpecialValueList 是特殊值的数据
     * @return 特殊值数量
     */
    public static List<Object> getBoolenEqual(List<Object> indexCodeDataList, List<Object> combinedList,List<Object> dataIsSpecialValueList) {

        for (Object dataValue : indexCodeDataList) {
            if (dataValue != null && combinedList.contains(dataValue.toString())) {
                dataIsSpecialValueList.add(dataValue);
            } else {
                if (combinedList.contains(dataValue)) {
                    dataIsSpecialValueList.add(dataValue);
                }
            }
        }
        return dataIsSpecialValueList;
    }

    /**
     *  计算Date类型的等于特殊值
     * @param indexCodeDataList 数据
     * @param combinedList 特殊值
     * @param dataIsSpecialValueList 是特殊值的数据
     * @return 特殊值数量
     */
    public static List<Object> getDateEqual(List<Object> indexCodeDataList, List<Object> combinedList,List<Object> dataIsSpecialValueList) {
        List<Long> convertedList = new ArrayList<>();

        for (Object specialValue : combinedList) {
            if (specialValue == null) {
                convertedList.add(null);
            } else {
                convertedList.add(parseStringToTimestamp(specialValue.toString()));
            }
        }
        for (Object dataValue : indexCodeDataList) {
            if (dataValue != null && convertedList.contains(parseStringToTimestamp(convertDateFormat(dataValue.toString())))) {
                dataIsSpecialValueList.add(dataValue);
            } else {
                if (convertedList.contains(dataValue)) {
                    dataIsSpecialValueList.add(dataValue);
                }
            }
        }
        return dataIsSpecialValueList;
    }

    /**
     * 将数据转换成时间戳格式
     * @param originalList 数据库数据
     * @return 时间戳格式数据
     */
    public static List<Long> convertToTimestampList(List<Object> originalList) {
        List<Long> timestampList = new ArrayList<>();

        for (Object obj : originalList) {
            timestampList.add(parseStringToTimestamp(obj.toString()));
        }


        return timestampList;
    }

    /**
     * 获取数据
     * @param dataList 所有数据
     * @param result 结果
     * @return 数据
     */
    public static List<Object> getData(List<Map<String, Object>> dataList,StatisticsResultVo result) {
        String indexCode = result.getVarCode();
        List<Object> indexCodeDataList = new ArrayList<>();
        for (Map<String, Object> map : dataList) {
            indexCodeDataList.add(map.get(indexCode));
        }
        return indexCodeDataList;
    }

    /**
     * 解析字符串为时间戳
     * @param dateString 时间字符串
     * @return 时间戳
     */
    public static long parseStringToTimestamp(String dateString) {
        // 固定的日期时间模式
        String pattern = "yyyy-MM-dd HH:mm:ss";
        log.info("dateString.length()=" + dateString.length());
        if (dateString.length() == MagicNumbers.TEN) {
            dateString = dateString + " 00:00:00";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = (Date) sdf.parse(dateString);
            return date.getTime();
        } catch (Exception e) {
            // 如果解析失败，可以根据需要处理异常情况
            e.printStackTrace();
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "解析时间字符串失败");
        }
    }


    /**
     * 有大于小于的情况下获取等于的特殊值
     * @param fieldType 字段类型
     * @param maxType 最大值操作类型
     * @param maxValue 最大值
     * @param minType 最小值操作类型
     * @param minValue 最小值
     * @param configSpecialMappingVoList 特殊值列表
     * @return 特殊值list
     */
    public static List<Object> getEqualList(String fieldType,String maxType, String maxValue,String minType,String minValue,List<ConfigSpecialMappingVo> configSpecialMappingVoList) {
        List<Object> getEqualList = new ArrayList<>();
        for (ConfigSpecialMappingVo  configSpecialMappingVo : configSpecialMappingVoList) {
            if (configSpecialMappingVo.getOperator().getDesc().startsWith("=")) {
                if (configSpecialMappingVo.getSpecialVal() == null) {
                    getEqualList.add(configSpecialMappingVo.getSpecialVal());
                }
                //有交集，不在范围里边,没交集，在范围里边
                //如果不在里面直接添加
                //如果等于最小值
                if (fieldType.equals(VarDataTypeEnum.DATE.getDesc()) || fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
                    if (parseStringToTimestamp(minValue) < parseStringToTimestamp(maxValue)) {
                        if (!(parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) > parseStringToTimestamp(minValue) && parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) < parseStringToTimestamp(maxValue))) {
                            getEqualList.add(configSpecialMappingVo.getSpecialVal());
                        }
                    }
                    if (parseStringToTimestamp(minValue) > parseStringToTimestamp(maxValue)) {
                        if (parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) < parseStringToTimestamp(minValue) && parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) > parseStringToTimestamp(maxValue)) {
                            getEqualList.add(configSpecialMappingVo.getSpecialVal());
                        }
                    }

                    if (parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) == parseStringToTimestamp(minValue) && minType.equals(VarMathSymbolTypeEnum.MORE_THAN.getDesc())) {
                        getEqualList.add(configSpecialMappingVo.getSpecialVal());
                    }
                    if (parseStringToTimestamp(configSpecialMappingVo.getSpecialVal()) == parseStringToTimestamp(maxValue) && maxType.equals(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
                        getEqualList.add(configSpecialMappingVo.getSpecialVal());
                    }
                }
                if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc()) || fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
                    if (Double.parseDouble(minValue) < Double.parseDouble(maxValue)) {
                        if (!(Double.parseDouble(configSpecialMappingVo.getSpecialVal()) > Double.parseDouble(minValue) && Double.parseDouble(configSpecialMappingVo.getSpecialVal()) < Double.parseDouble(maxValue))) {
                            getEqualList.add(configSpecialMappingVo.getSpecialVal());
                        }
                    }
                    if (Double.parseDouble(minValue) > Double.parseDouble(maxValue)) {
                        if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) < Double.parseDouble(minValue) && Double.parseDouble(configSpecialMappingVo.getSpecialVal()) > Double.parseDouble(maxValue)) {
                            getEqualList.add(configSpecialMappingVo.getSpecialVal());
                        }
                    }
                    if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) == Double.parseDouble(minValue) && minType.equals(VarMathSymbolTypeEnum.MORE_THAN.getDesc())) {
                        getEqualList.add(configSpecialMappingVo.getSpecialVal());
                    }
                    if (Double.parseDouble(configSpecialMappingVo.getSpecialVal()) == Double.parseDouble(maxValue) && maxType.equals(VarMathSymbolTypeEnum.LESS_THAN.getDesc())) {
                        getEqualList.add(configSpecialMappingVo.getSpecialVal());
                    }
                }
            }

        }
        return getEqualList;
    }

    /**
     * cst转换
     * @param inputDateString
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String convertDateFormat(String inputDateString) {
        String inputPattern = "EEE MMM dd HH:mm:ss zzz yyyy";
        String outputPattern = "yyyy-MM-dd HH:mm:ss";

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            // 设置时区为CST
            TimeZone cstTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
            inputFormat.setTimeZone(cstTimeZone);

            Date date = inputFormat.parse(inputDateString);

            // 格式化为另一种日期格式
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
