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
package com.wiseco.var.process.app.server.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@Schema(description = "睿信概览数量与状态")
public class OverViewQuantityAndStatusVo implements Serializable {
    private static final long SERIAL_VERSION_UID = 8759865846955173993L;


    @Schema(description = "所有数量")
    private Integer allNumber;

    @Schema(description = "所有版本数量")
    private Integer allVersionNumber;

    @Schema(description = "启用状态数量")
    private Integer upNumber;

    @Schema(description = "编辑状态数量")
    private Integer editNumber;

    @Schema(description = "停用状态数量")
    private Integer downNumber;



}
