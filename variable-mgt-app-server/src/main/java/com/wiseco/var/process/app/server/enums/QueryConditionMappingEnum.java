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
package com.wiseco.var.process.app.server.enums;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: liusiyu
 * @date: 2024/1/15
 * @Time: 19:18
 */
@Getter
@AllArgsConstructor
public enum QueryConditionMappingEnum {
    /**
     * 搜索条件映射
     */
    CUST_NO("custNo", "客户编号"),
    CUST_NAME("custName", "姓名"),
    CERT_TYPE("certType", "证件类型"),
    CERT_NO("certNo", "证件号码"),
    MOBILE("mobile", "手机号码"),
    PRODUCT_CODE("productCode", "产品编码"),
    CHANNEL_CODE("channelCode", "渠道编码"),
    BIZ_TYPE("bizType", "业务场景");

    private final String code;
    private final String name;

    /**
     * getByCode
     *
     * @param code
     * @return com.wiseco.var.process.app.server.enums.QueryConditionMappingEnum
     */
    public static QueryConditionMappingEnum getByCode(String code) {
        QueryConditionMappingEnum[] values = QueryConditionMappingEnum.values();
        for (QueryConditionMappingEnum value : values) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * getMapList
     *
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    public static List<Map<String, Object>> getMapList() {
        return Arrays.stream(QueryConditionMappingEnum.values()).map(item -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", item.getCode());
            map.put("name", item.getName());
            return map;
        }).collect(Collectors.toList());
    }

}

