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
package com.wiseco.var.process.app.server.service.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: xiewu
 * @since: 2021/12/28 14:25
 */
@Data
@Schema(description = "外部服务mock明细下载模板 入参DTO")
public class ServiceMockDetailExportTemplateExcelInputDto {

    @Schema(description = "外部服务Id", example = "1")
    private Long outsideServiceId;
}
