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

import com.wiseco.var.process.app.server.enums.ProcessingMethodEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 变量表
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量详情 DTO")
public class VariableDetailDto implements Serializable {

    private Long id;

    /**
     * 变量空间ID
     */
    private Long varProcessSpaceId;

    /**
     * 父级变量ID
     */
    private Long parentId;

    /**
     * 编号
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
     * 数据类型
     */
    private String dataType;

    /**
     * 变量类型ID
     */
    private Long categoryId;

    /**
     * 描述
     */
    private String description;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 变量状态 1-编辑中，2-启用，3-停用,4-待审核,5-审核拒绝
     */
    private VariableStatusEnum status;

    /**
     * 变量内容
     */
    private String content;

    /**
     * 创建用户
     */
    private String createdUser;

    /**
     * 更新用户
     */
    private String updatedUser;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 更新时间
     */
    private String updatedTime;

    /**
     * 是否使用
     */
    private String isUse;

    /**
     * 创建部门
     */
    private Long deptId;

    /**
     * 创建部门code
     */
    private String deptCode;

    /**
     * 加工方式
     */
    private ProcessingMethodEnum processingMethod;
}
