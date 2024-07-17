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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author liaody
 */
@Data
@Schema(description = "数据模型版本比较 入参DTO")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DataVersionCompareInputVO {
    @Schema(description = "数据模型id", example = "[30174001,30174002]")
    private List<Long> dataModelIdList;

    @Schema(description = "是否显示差异：0-全部，1-仅差异", example = "0")
    private Integer isDiff;
}
