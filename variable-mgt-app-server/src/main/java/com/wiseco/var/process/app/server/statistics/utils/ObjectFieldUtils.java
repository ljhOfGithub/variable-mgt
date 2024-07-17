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
package com.wiseco.var.process.app.server.statistics.utils;

import com.wiseco.var.process.app.server.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.enums.VarDataTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Map;

@Slf4j
public class ObjectFieldUtils {
    private static final String SIXTH_FOUR = "64";
    private static final String INT_EIGHT = "int8";
    private static final String LONG = "long";

    /**
     * getFieldType
     *
     * @param dataType rc
     * @return java.lang.String
     */
    public static String getFieldType(String dataType) {
        if (StringUtils.isEmpty(dataType)) {
            return null;
        }
        String fieldType = null;
        String lowerStr = dataType.toLowerCase();
        if (lowerStr.contains(VarDataTypeEnum.INTEGER.getDesc())) {
            if (lowerStr.contains(SIXTH_FOUR)) {
                fieldType = CommonConstant.LONG_STR;
            } else if (lowerStr.contains(INT_EIGHT)) {
                fieldType = VarDataTypeEnum.BOOLEAN.getDesc();
            } else {
                fieldType = VarDataTypeEnum.INTEGER.getDesc();
            }
        } else if (lowerStr.contains(VarDataTypeEnum.STRING.getDesc())) {
            fieldType = VarDataTypeEnum.STRING.getDesc();
        } else if (lowerStr.contains(CommonConstant.FLOAT_STR)) {
            fieldType = VarDataTypeEnum.DOUBLE.getDesc();
        } else if (lowerStr.contains(VarDataTypeEnum.DATETIME.getDesc())) {
            fieldType = VarDataTypeEnum.DATETIME.getDesc();
        } else if (lowerStr.contains(VarDataTypeEnum.DATE.getDesc())) {
            fieldType = VarDataTypeEnum.DATE.getDesc();
        }

        return fieldType;
    }

    /**
     * getFieldVal
     *
     * @param fieldType rc
     * @param indexName rc
     * @param map rc
     * @return java.lang.String
     */
    public static String getFieldVal(String fieldType, String indexName, Map<String, Object> map) {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String val = null;
        if (fieldType.equals(VarDataTypeEnum.INTEGER.getDesc())) {
            val = String.valueOf(map.get(indexName));
        } else if (fieldType.equals(VarDataTypeEnum.STRING.getDesc())) {
            val = (String) map.get(indexName);
        } else if (fieldType.equals(VarDataTypeEnum.DOUBLE.getDesc())) {
            val = String.valueOf(map.get(indexName));
        } else if (fieldType.equals(VarDataTypeEnum.DATE.getDesc())) {
            val = dateFormat.format(map.get(indexName));
        } else if (fieldType.equals(VarDataTypeEnum.DATETIME.getDesc())) {
            val = datetimeFormat.format(map.get(indexName));
        } else if (fieldType.equals(VarDataTypeEnum.BOOLEAN.getDesc())) {
            val = String.valueOf(map.get(indexName));
        } else if (fieldType.equals(LONG)) {
            val = String.valueOf(map.get(indexName));
        }

        return val;
    }

    /**
     * getFieldVal
     *
     * @param val 数值
     * @return java.lang.String
     */
    public static double getDoubleVal(String val) {
        if (NumberUtils.isCreatable(val)) {
            return Double.parseDouble(val);
        } else {
            return 0.0;
        }
    }
}
