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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author: xiewu
 */
@Data
@Schema(description = "基本信息属性面板内容信息")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    @Schema(description = "标签", example = "版本号")
    String label;

    @Schema(description = "内容", example = "V3.3")
    String value;

    @Schema(description = "链接", example = "http://")
    String url;
}
