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
package com.wiseco.var.process.app.server.commons.enums;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 实时服务报文格式
 */
@Getter
@AllArgsConstructor
public enum ServiceMsgFormatEnum {
    /**
     * json
     */
    JSON("json", "JSON报文"),
    /**
     * 转义后的JSON
     */
    ESCAPED_JSON("escapedJson", "JSON字符串"),
    /**
     * xml
     */
    XML("xml", "XML报文");
    private final String code;
    private final String desc;

    /**
     * getMsgFormatEnumList
     *
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     */
    public static List<Map<String, Object>> getMsgFormatEnumList() {
        List<Map<String, Object>> list = Lists.newArrayList();
        for (ServiceMsgFormatEnum value : ServiceMsgFormatEnum.values()) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("code", value.getCode());
            map.put("desc", value.getDesc());
            list.add(map);
        }
        return list;
    }
}
