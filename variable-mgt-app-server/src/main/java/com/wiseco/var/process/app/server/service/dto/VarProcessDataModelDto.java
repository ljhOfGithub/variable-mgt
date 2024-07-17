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

import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 变量空间-数据模型
 * </p>
 *
 * @author wangxianli
 * @since 2022-08-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VarProcessDataModelDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 变量空间ID
     */
    private Long varProcessSpaceId;

    /**
     * 对象名称
     */
    private String objectName;

    /**
     * 对象中文名
     */
    private String objectLabel;

    /**
     * 对象来源类型
     */
    private VarProcessDataModelSourceType objectSourceType;

    /**
     * 来源表/外部服务
     */
    private String objectSourceInfo;

    /**
     * 数据模型jsonschema
     */
    private String content;

    /**
     * 原始数据数量
     */
    private Integer sourcePropertyNum;

    /**
     * 扩展数据数量
     */
    private Integer extendPropertyNum;

    /**
     * 版本号,从1开始
     */
    private Integer version;

    /**
     * 创建部门
     */
    private String createdDept;

    /**
     * 创建部门
     */
    private String createdDeptName;

    /**
     * 创建用户
     */
    private String createdUser;

    /**
     * 更新用户
     */
    private String updatedUser;

    /**
     * 数据模型查询条件映射
     */
    private String modelQueryCondition;

}
