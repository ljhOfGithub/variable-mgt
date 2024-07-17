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

import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionHandleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 公共函数详情实体类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "公共函数详情 DTO")
public class FunctionDetailDto implements Serializable {

    private Long id;

    /**
     * 变量空间ID
     */
    private Long varProcessSpaceId;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 编号
     */
    private String identifier;

    /**
     * 公共函数名
     */
    private String name;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 数据类型
     */
    private String functionDataType;

    /**
     * 预处理对象
     */
    private String prepObjectName;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态 编辑中，启用，停用
     */
    private FlowStatusEnum status;

    /**
     * 变量内容
     */
    private String content;

    /**
     * 创建部门编码
     */
    private String createdDeptCode;

    /**
     * 创建部门
     */
    private String createdDept;

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
     * 模板分类
     */
    private Long categoryId;

    /**
     * 处理方式
     */
    private FunctionHandleTypeEnum handleType;

    /**
     * 变量模版词条内容
     */
    private String functionEntryContent;

    /**
     * 是否生成模板
     */
    private Boolean variableCreated;

    /**
     * 是否使用
     */
    private Boolean isUse;

    /**
     * 是否测试
     */
    private Boolean isTest;
}
