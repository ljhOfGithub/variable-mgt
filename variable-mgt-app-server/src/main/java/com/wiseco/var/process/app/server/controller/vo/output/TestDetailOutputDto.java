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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: wangxianli
 */
@Schema(description = "测试数据明细响应DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestDetailOutputDto implements Serializable {
    private static final long serialVersionUID = 8668690652316747092L;

    @Schema(description = "UUID,在线自动生成和导入模板使用", example = "1")
    private String uuid;

    @Schema(description = "测试明细表头", example = "无")
    private Map<String, Object> tableHeaderField;

    @Schema(description = "测试明细", example = "null")
    private List<String> dataList;

    @Schema(description = "总记录数", example = "0")
    private Integer totalNums;

}
