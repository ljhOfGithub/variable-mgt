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

import com.wiseco.model.common.entity.PageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceListCriteria extends PageDto implements Serializable {

    private static final long serialVersionUID = -290298936965823914L;

    @Schema(description = "服务编码list", example = "10000")
    private List<String>           codeList;

    @Schema(description = "服务编码notInList", example = "10000")
    private List<String>           notInCodeList;

    @Schema(description = "服务名称或编码", example = "10000")
    String keyWord;
}
