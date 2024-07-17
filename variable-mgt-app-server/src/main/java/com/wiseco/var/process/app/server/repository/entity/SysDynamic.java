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
package com.wiseco.var.process.app.server.repository.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 系统动态表
 * </p>
 *
 * @author wangxianli
 * @since 2022-03-02
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dynamic")
@NoArgsConstructor
@AllArgsConstructor
public class SysDynamic extends BaseEntity {
    private static final long serialVersionUID = -2959056706266972023L;
    /**
     * 空间类别：领域、外部服务..
     */
    @TableField("space_type")
    private String spaceType;

    /**
     * 空间业务ID：领域ID、外部服务
     */
    @TableField("space_business_id")
    private Long spaceBusinessId;

    /**
     * 空间名称
     */
    @TableField("space_name")
    private String spaceName;

    /**
     * 操作类型：提交测试、创建、编辑、添加、
     */
    @TableField("operate_type")
    private String operateType;

    /**
     * 策略ID
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 业务细分类型
     */
    @TableField("business_bucket")
    private String businessBucket;

    /**
     * 业务Id：结合业务类型，用于跳转URL的拼接
     */
    @TableField("business_id")
    private String businessId;

    /**
     * 权限业务ID，同permission_role_resources的resources_id
     */
    @TableField("permission_resources_id")
    private Long permissionResourcesId;

    /**
     * 权限标识，同permission_role_resources的resources_code
     */
    @TableField("permission_resources_code")
    private String permissionResourcesCode;

    /**
     * 决策服务/公共决策模块
     */
    @TableField("decision_name")
    private String decisionName;

    /**
     * 细分
     */
    @TableField("bucket_name")
    private String bucketName;

    /**
     * 策略
     */
    @TableField("strategy_name")
    private String strategyName;

    /**
     * 内容描述
     */
    @TableField("business_content")
    private String businessContent;

    /**
     * 创建用户
     */
    @TableField("created_user")
    private String createdUser;

    /**
     * 更新用户
     */
    @TableField("updated_user")
    private String updatedUser;

    /**
     * 处理状态：0-失败，1-成功
     */
    @TableField("status")
    private Integer status;

}
