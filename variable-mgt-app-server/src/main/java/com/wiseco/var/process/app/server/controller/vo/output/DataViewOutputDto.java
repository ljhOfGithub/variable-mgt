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

import com.wiseco.var.process.app.server.controller.vo.input.DataViewInputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author wzc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据查询出参")
public class DataViewOutputDto implements Serializable {

    private static final long serialVersionUID = -2071597727619749746L;

    @Schema(description = "数据查询表头")
    private Collection<String> headers;
    @Schema(description = "数据查询数据和分页信息")
    private DataViewPage data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataViewPage {

        private List<Map<String, Object>> records;

        private DataViewInputDto.RowPage rowPage;

        private DataViewInputDto.ColumnPage columnPage;

    }

}
