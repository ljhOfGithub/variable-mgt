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

import com.wiseco.var.process.app.server.commons.enums.SortType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 字典项数据入参DTO
 * <p>查询依据: 字典类型编码</p>
 *
 * @author xiewu
 * @author Zhaoxiong Chen
 * @since 2022/4/28
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "字典项查询入参DTO（依据字典类型编码查询）")
public class DictItemQueryInputDto implements Serializable {

    private static final long serialVersionUID = 4607589631032053446L;

    @Schema(description = "变量空间id", example = "1")
    private Long spaceId;

    @Schema(description = "字典类型编码", example = "gender")
    @NotNull(message = "字典类型编码不能为空。")
    private String code;

    @Schema(description = "名称或者编码", example = "1")
    private String nameOrCode;

    @Schema(description = "排序：字段名称")
    private String sortKey;

    @Schema(description = "升序/降序")
    private SortType sortType;
}
