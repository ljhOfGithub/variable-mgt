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

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.enums.AnalysisIndexEnum;
import com.wiseco.var.process.app.server.controller.vo.StatisticsResultVo;
import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import com.wiseco.var.process.app.server.statistics.context.IndexCalculateContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 唯一值数量计算
 */
@Component
public class UniqueNumIndexCalculateStrategy implements IndexCalculateStrategy {

    private static final String COMMA = ",";

    private static final String NULL = "null";

    @Override
    public void calculateVar(StatisticsResultVo result, IndexCalculateContext calculateContext) {
        String fieldType = result.getDataType();
        if (fieldType == null || !fieldType.equals(VarDataTypeEnum.STRING.getDesc())) {
            return;
        }

        String indexCode = result.getVarCode();

        List<Map<String, Object>> dataList = calculateContext.getDataList();

        List<Object> valList = dataList.stream().map(e -> e.get(indexCode)).collect(Collectors.toList());
        Map<String, Integer> map = new HashMap<>(MagicNumbers.SIXTEEN);
        StringBuilder buffer = new StringBuilder();
        Integer countVal = 0;
        for (Object o : valList) {
            if (o == null) {
                continue;
            }
            if (NULL.equals(o)) {
                continue;
            }
            if (StringUtils.isEmpty((String) o)) {
                continue;
            }

            if (map.get(o) == null) {
                map.put((String) o, 1);
                buffer.append((String) o).append(",");
                countVal++;
            }
        }
        if (StringUtils.isNotEmpty(buffer) && buffer.indexOf(COMMA) >= 0) {
            String val = buffer.toString();
            result.setUniqueVal(val.substring(0, val.length() - 1));
        }

        result.setUniqueNum(countVal);

    }

    @Override
    public String getIndexName() {
        return AnalysisIndexEnum.UNIQUE_NUM.getCode();
    }
}
