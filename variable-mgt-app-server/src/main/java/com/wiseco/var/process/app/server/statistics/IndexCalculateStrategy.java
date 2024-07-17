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

import com.wiseco.var.process.app.server.controller.vo.ConfigSpecialMappingVo;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IndexCalculateStrategy {

    /**
     * 统计分析计算
     * 
     * @param result 统计结果
     * @param calculateContext 统计分析计算
     */
    void calculateVar(StatisticsResultVo result, IndexCalculateContext calculateContext);

    /**
     * 获取统计分析的名称
     * 
     * @return 统计分析名称
     */
    String getIndexName();

    /**
     * 获取数值类型数据
     * @param dataTable 表数据
     * @param varCode 字段Code
     * @param defaultValue 缺失值
     * @return 数值类型数据
     */
    default List<Double> getDoubleValues(List<Map<String, Object>> dataTable, String varCode, String defaultValue) {
        final List<Object> dataList = dataTable.stream().map(data -> data.get(varCode)).collect(Collectors.toList());
        return dataList.stream().map(data -> data == null ? NumberUtils.createDouble(defaultValue) : NumberUtils.createDouble(data.toString())).collect(Collectors.toList());
    }

    /**
     * 获取字符串类型数据
     * @param dataTable 表数据
     * @param varCode 字段Code
     * @param defaultValue 缺失值
     * @return 字符串类型数据
     */
    default List<String> getStringValues(List<Map<String, Object>> dataTable, String varCode, String defaultValue) {
        if (!dataTable.get(0).containsKey(varCode)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "字段" + varCode + "不存在");
        }
        final List<Object> dataList = dataTable.stream().map(data -> data.get(varCode)).collect(Collectors.toList());
        return dataList.stream().map(data -> data == null ? defaultValue : data.toString()).collect(Collectors.toList());
    }

    /**
     * 获取缺失值
     * @param dataTypeConfigMap 缺失值
     * @param fieldType 数据类型
     * @return 取缺失值
     */
    default String getDefaultValue(Map<String, VarProcessConfigDefault> dataTypeConfigMap, String fieldType) {
        VarProcessConfigDefault defaultValues = dataTypeConfigMap == null ? null : dataTypeConfigMap.get(fieldType);
        Assert.notNull(defaultValues, "defaultValues is null");
        return defaultValues.getDefaultValue();
    }

    /**
     * 获取特殊值
     * @param specialMappingVoList 入参
     * @param fieldType 数据类型
     * @return 特殊值
     */
    default List<String> getSpecialStringValues(List<ConfigSpecialMappingVo> specialMappingVoList, String fieldType) {
        if (specialMappingVoList == null) {
            return new ArrayList<>();
        }
        return specialMappingVoList.stream().filter(e -> e.getDataType() != null && e.getDataType().getDesc().equals(fieldType)).map(ConfigSpecialMappingVo::getSpecialVal).collect(Collectors.toList());
    }

}
