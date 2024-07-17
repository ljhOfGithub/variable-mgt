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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "变量分类查询 DTO")
public class VarProcessCategoryQueryDto implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    /**
     * 变量空间ID
     */
    private Integer varProcessSpaceId;

    /**
     * 变量分类名称
     */
    private String name;

    /**
     * 父级id
     */
    private Integer parentId;

    /**
     * 删除状态
     */
    private Integer deleteFlag;

}
