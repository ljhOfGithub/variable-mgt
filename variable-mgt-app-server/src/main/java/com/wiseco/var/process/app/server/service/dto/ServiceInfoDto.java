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

import com.wiseco.var.process.app.server.enums.VarProcessServiceStateEnum;
import lombok.Data;

@Data
public class ServiceInfoDto {

    private Long id;

    /**
     * 服务编码
     */
    private String code;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private VarProcessServiceStateEnum state;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 流水号
     */
    private String serialNo;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 是否启用了trace,true-启用,false-未启用
     */
    private Boolean enableTrace;
}
