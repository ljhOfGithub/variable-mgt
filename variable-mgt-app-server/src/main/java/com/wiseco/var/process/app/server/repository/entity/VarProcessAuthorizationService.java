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

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("var_process_authorization_service")
public class VarProcessAuthorizationService extends BaseEntity {

    /**
     * 授权id
     */
    @TableField("authorization_id")
    private Long        authorizationId;

    /**
     * 服务编码
     */
    @TableField("service_code")
    private String      serviceCode;

    /**
     * 创建人
     */
    @TableField("created_user")
    private String      createdUser;

    /**
     * 修改人
     */
    @TableField("updated_user")
    private String      updatedUser;
}
