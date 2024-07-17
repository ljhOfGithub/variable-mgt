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
package com.wiseco.var.process.app.server.controller.vo.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mingao
 * @since 2023/9/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableFieldVO {
    /**
     * 字段名称
     */
    @JsonProperty("field_name")
    private String fieldName;
    /**
     * 字段类型
     */
    @JsonProperty("field_type")
    private String fieldType;

}
