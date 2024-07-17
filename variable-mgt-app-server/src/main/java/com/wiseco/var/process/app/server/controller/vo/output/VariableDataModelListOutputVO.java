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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 数据模型列表DTO
 *
 * @author wangxianli
 * @since 2022/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据模型列表 DTO")
public class VariableDataModelListOutputVO implements Serializable {

    private static final long serialVersionUID = 8799865908944973992L;

    @Schema(description = "数据模型ID", example = "")
    private Long id;

    @Schema(description = "对象名称", example = "")
    private String objectName;

    @Schema(description = "对象中文名", example = "")
    private String objectLabel;

    @Schema(description = "对象来源")
    private VarProcessDataModelSourceType sourceType;

    @Schema(description = "数据模型是否使用")
    private Boolean used;

    @Schema(description = "来源表/外部服务")
    private String sourceInfo;

    @Schema(description = "版本", example = "1")
    private Integer version;

    @Schema(description = "原始数据数量", example = "")
    private Integer sourcePropertyNum;

    @Schema(description = "扩展数据数量", example = "string")
    private Integer extendPropertyNum;

    @Schema(description = "创建部门", example = "string")
    private String createdDept;

    @Schema(description = "创建人", example = "张三")
    private String createdUser;

    @Schema(description = "编辑人", example = "张三")
    private String updatedUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2021-10-10 10:00:0")
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后更新时间", example = "2021-10-10 10:00:0")
    private Date updatedTime;

    @Schema(description = "子版本", example = "null")
    private List<VariableDataModelListOutputVO> versionList;

    @Schema(description = "实时服务", example = "null")
    private List<VariableRefServiceManifestOutputDto> serviceList;
}
