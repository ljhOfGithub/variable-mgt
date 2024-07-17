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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 策略-java工具类关系表
 * </p>
 *
 * @author fudengkui
 * @since 2023-03-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("str_com_java_toolkit")
public class StrComJavaToolkit extends BaseEntity {

    /**
     * 策略ID
     */
    @TableField("strategy_id")
    private Long strategyId;

    /**
     * 组件ID
     */
    @TableField("component_id")
    private Long componentId;

    /**
     * jar编号
     */
    @TableField("jar_identifier")
    private String jarIdentifier;

    /**
     * class编号
     */
    @TableField("class_identifier")
    private String classIdentifier;

    /**
     * method编号
     */
    @TableField("method_identifier")
    private String methodIdentifier;

    /**
     * attribute编号
     */
    @TableField("attribute_identifier")
    private String attributeIdentifier;

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

}
