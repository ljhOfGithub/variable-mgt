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
import com.wiseco.var.process.app.server.commons.enums.ServiceMsgFormatEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 变量服务表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@TableName("var_process_realtime_service")
public class VarProcessRealtimeService extends BaseEntity {

    /**
     * 指标空间ID
     */
    @TableField("space_id")
    private Long spaceId;

    /**
     * 服务编码
     */
    @TableField("service_code")
    private String serviceCode;

    /**
     * 服务名称
     */
    @TableField("service_name")
    private String serviceName;

    /**
     * 类型id
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 开启trace日志: true(开启), false(关闭)
     */
    @TableField("enable_trace")
    private Boolean enableTrace;

    /**
     * 报文格式：JSON/XML
     */
    @TableField("message_format")
    private ServiceMsgFormatEnum messageFormat;

    /**
     * 删除标识 0:已删除 1:可用
     */
    @TableField("delete_flag")
    private Integer deleteFlag;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

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
     * 部门名称
     */
    @TableField("dept_code")
    private String deptCode;
}
