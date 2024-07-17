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
package com.wiseco.var.process.app.server.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Schema(description = "分位数")
public class PercentageMappingVo implements Serializable {
    private static final long SERIAL_VERSION_UID = 8759865846955173800L;

    @JsonProperty("percentage_1")
    private BigDecimal percentage1;
    @JsonProperty("percentage_5")
    private BigDecimal percentage5;
    @JsonProperty("percentage_25")
    private BigDecimal percentage25;
    @JsonProperty("percentage_50")
    private BigDecimal percentage50;
    @JsonProperty("percentage_75")
    private BigDecimal percentage75;
    @JsonProperty("percentage_95")
    private BigDecimal percentage95;

}
