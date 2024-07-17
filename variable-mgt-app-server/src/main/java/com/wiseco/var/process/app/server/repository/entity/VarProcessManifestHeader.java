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
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wiseco.var.process.app.server.enums.ColRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("var_process_manifest_header")
public class VarProcessManifestHeader implements Serializable {
    private static final long    serialVersionUID = 33421623489624896L;

    @TableId("id")
    private Long                  id;

    @TableField("manifest_id")
    private Long                  manifestId;

    @TableField("variable_code")
    private String                variableCode;

    @TableField("variable_type")
    private String                variableType;

    @TableField("order_no")
    private Integer                orderNo;

    @TableField("is_index")
    private Boolean                isIndex;

    @TableField("col_role")
    private ColRoleEnum colRole;
}
