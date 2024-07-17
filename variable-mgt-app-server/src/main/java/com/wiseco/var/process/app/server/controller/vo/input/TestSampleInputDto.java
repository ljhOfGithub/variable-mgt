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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: wangxianli
 */
@Schema(description = "样本数据分页查询DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestSampleInputDto implements Serializable {

    private static final long serialVersionUID = 3137457580256428647L;

    @Schema(description = "UUID", required = true, example = "1")
    @NotNull(message = "uuid不能为空")
    private String uuid;

    @Schema(description = "页码", required = true, example = "1")
    @NotNull(message = "页码不能为空")
    private Integer page;

    @Schema(description = "分页个数", required = true, example = "10")
    @NotNull(message = "分页个数不能为空")
    private Integer size;

}

