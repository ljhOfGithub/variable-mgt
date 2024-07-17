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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wangxianli
 */
@Schema(description = "字典 DTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictDetailsDto implements Serializable {
    private static final long serialVersionUID = 8668690652316747092L;

    @Schema(description = "字典类别编码", example = "1")
    private String dictCode;

    @Schema(description = "字典类别名称", example = "1")
    private String dictName;

    @Schema(description = "字典项编码", example = "1")
    private String code;

    @Schema(description = "字典项名称", example = "1")
    private String name;

}

