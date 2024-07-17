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

import java.util.List;

/**
 * 右侧边栏选项卡 DTO
 *
 * @author wangxianli
 * @since 2022/6/9
 */
@Data
@Schema(description = "tab对象")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TabDto {
    @Schema(description = "tab页名称", example = "属性信息")
    private String name;

    @Schema(description = "panel内容", example = "基本信息")
    private List<PanelDto> content;
}
