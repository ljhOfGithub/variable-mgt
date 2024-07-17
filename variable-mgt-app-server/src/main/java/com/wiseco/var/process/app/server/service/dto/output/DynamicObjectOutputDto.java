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
package com.wiseco.var.process.app.server.service.dto.output;

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 决策领域树形结构实体
 *
 * @author: xiewu
 * @since: 2021/10/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamicObjectOutputDto implements Serializable {

    private static final long serialVersionUID = -1L;
    /**
     * items
     **/
    @Schema(description = "数据信息", example = "")
    private List<DomainDataModelTreeDto> items;
    /**
     * 是否可选object
     **/
    @Schema(description = "是否可选object", example = "1")
    @JsonProperty("object_could_choose")
    private int objectCouldChoose;
    /**
     * 类型
     **/
    @Schema(description = "类型", example = "data")
    private String type;

}
