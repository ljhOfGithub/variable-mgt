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
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用来展示{id, name}结构的监控报表
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用来展示{id, name}结构的监控报表")
public class ReportFormItemVo implements Serializable {

    private static final long serialVersionUID = 7329475830759796935L;

    @Schema(description = "监控报表的ID", example = "10001")
    private Long id;

    @Schema(description = "监控报表的名称", example = "监控对象在不同清单的缺失率")
    private String name;
}
