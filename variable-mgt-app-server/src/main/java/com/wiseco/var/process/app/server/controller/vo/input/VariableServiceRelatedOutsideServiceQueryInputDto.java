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

import com.wiseco.boot.data.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 实时服务相关的外部服务查询入参 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "实时服务相关的外部服务查询输入参数")
public class VariableServiceRelatedOutsideServiceQueryInputDto extends PageDTO implements Serializable {

    private static final long serialVersionUID = 8127102848370164405L;

    @Schema(description = "空间 ID")
    private Long spaceId;

    @Schema(description = "用户指定的服务输出变量 ID 列表")
    private List<Long> variableIdList;

    @Schema(description = "外部服务状态", example = "null: 查询全部, 0: 停用, 1: 启用")
    private List<String> outsideServiceState;

    @Schema(description = "外部服务名称/编码搜索关键词")
    private String keywords;
}
