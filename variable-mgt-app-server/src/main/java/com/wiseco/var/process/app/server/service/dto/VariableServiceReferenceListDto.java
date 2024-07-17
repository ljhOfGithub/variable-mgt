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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 决策领域/策略-实时服务引用列表 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableServiceReferenceListDto {

    /**
     * 变量空间 ID
     */
    private Long variableSpaceId;

    /**
     * 实时服务 ID
     */
    private Long variableServiceId;

    /**
     * 实时服务名称
     */
    private String name;

    /**
     * 实时服务编码
     */
    private String code;

    /**
     * 实时服务类型
     */
    private String type;

    /**
     * 引入状态
     */
    private Integer refState;

    /**
     * 首次引入时间
     */
    private Date createdTime;
}
