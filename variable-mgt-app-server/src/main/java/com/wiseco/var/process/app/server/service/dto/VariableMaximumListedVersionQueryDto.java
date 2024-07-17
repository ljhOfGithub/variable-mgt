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

import com.wiseco.var.process.app.server.service.dto.common.AuthFilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 所有变量最大已上架版本记录查询 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2023/1/3
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VariableMaximumListedVersionQueryDto extends AuthFilterDTO {

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 变量名称/中文名模糊搜索关键字
     */
    private String keywords;

    /**
     * 数据类型搜索关键字
     */
    private List<Long> categoryIds;

    /**
     * 变量数据类型搜索关键字
     */
    private String varDataType;

    /**
     * 变量标签搜索关键字
     */
    private List<String> tagNames;

    /**
     * 部门code
     */
    private String deptCode;

    /**
     * 排除的 (用户已选择的) 变量标识列表
     */
    private List<String> excludedIdentifierList;

    /**
     * 根据sortedkey关键字对列表进行排序
     */
    private String sortedKey;

    /**
     * 排序方式：正序asc/倒序desc
     */
    private String sortMethod;

}
