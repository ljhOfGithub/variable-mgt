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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author: xiewu
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "字典类别列表接口 出参DTO")
public class DictTreeDto {

    @Schema(description = "主键id", example = "1")
    private Long id;

    @Schema(description = "编码", example = "channel")
    private String code;

    @Schema(description = "名称", example = "资产渠道")
    private String name;

    @Schema(description = "状态 0:停用 1:启用", example = "1")
    private Integer state;

    @Schema(description = "创建人", example = "admin")
    private String createdUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2021-10-10 10:00:0")
    private Date createdTime;

    @Schema(description = "上次编辑人", example = "admin")
    private String updatedUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后编辑时间", example = "2021-10-10 10:00:0")
    private Date updatedTime;

    @Schema(description = "操作：分两种字典类型,", example = "[{value:'editDomainDict',label:'编辑'},{value:'deleteDomainDict',label:'删除'},{value:'saveDomainDict',label:'添加字典项'}]")
    private List<OperationButton> operationButton;

    @Schema(description = "子对象表示字典项：字典项的操作为：[{value:'editDomainDictDetails',label:'编辑'}{value:'deleteDomainDictDetails',label:'删除'}]", example = "")
    private List<DictTreeDto> children;

    @Schema(description = "字典类型id", example = "1")
    private Long dictId;

    @Schema(description = "字典类型名称", example = "资产渠道")
    private String dictName;

    @Schema(description = "字典项父编码", example = "jt360")
    private String parentCode;

    @Schema(description = "路径值", example = "a.b.c")
    private String value;
}
