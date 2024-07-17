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
package com.wiseco.var.process.app.server.commons.test.dto;

import com.wiseco.var.process.app.server.controller.vo.output.StackMessageOutputDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wangxianli
 * @since 2022/06/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "测试执行返回DTO")
public class TestExecuteOutputDto extends StackMessageOutputDto {

    @Schema(description = "变量Id", example = "1")
    private Long resultId;

}
