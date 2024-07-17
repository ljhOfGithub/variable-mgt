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
package com.wiseco.var.process.app.server.controller.vo.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * 字段映射参数
 *
 * @author yangyunsen
 * @since 2022/10/14-13:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "字段映射参数")
public class DataBaseFieldMapDto {
    /**
     * 原表字段
     */
    @Schema(description = "原表字段")
    private String orgFieldName;

    /**
     * 原表别名
     */
    @Schema(description = "原表别名")
    private String orgTableName;

    /**
     * 构建sql结果显示字段名
     *
     * @return String
     */
    public String getLabelField() {
        if (StringUtils.isEmpty(orgTableName)) {
            return orgFieldName;
        } else {
            return orgTableName + "." + orgFieldName;
        }
    }

    /**
     * 构建sql字段别名
     *
     * @return String
     */
    public String getSqlAliasField() {
        if (StringUtils.isEmpty(orgTableName)) {
            return orgFieldName;
        } else {
            return orgTableName + "_" + orgFieldName.replace(".", "_");
        }
    }

}
