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

import com.wiseco.var.process.app.server.enums.ColRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 变量清单版本详情 - 发布变量清单 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2023/1/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableManifestPublishingVariableDTO {

    /**
     * 变量标识符
     */
    private String identifier;

    /**
     * 变量名
     */
    private String name;

    /**
     * 变量中文名
     */
    private String label;

    /**
     * 变量分类
     */
    private String category;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 用户选择的变量版本号
     */
    private Integer selectedVersion;

    /**
     * 用户选择的变量版本 ID
     */
    private Long selectedVersionVariableId;

    /**
     * 是否输出: 1 for checked, 0 for unchecked
     */
    private Integer outputFlag;

    /**
     * 是否索引字段
     */
    private Boolean isIndex;

    /**
     * 列角色
     */
    private ColRoleEnum colRole;
}
