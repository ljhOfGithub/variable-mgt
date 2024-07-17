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

/**
 * 实时服务授权信息 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableServiceAuthorizationInfoDto {

    /**
     * 服务授权记录 ID
     */
    private Long recordId;

    /**
     * 领域 ID
     */
    private Long domainId;

    /**
     * 领域编码
     */
    private String domainCode;

    /**
     * 领域名称
     */
    private String domainName;

    /**
     * 授权时间
     */
    private String authorizeTime;

    /**
     * 是否引入
     * 使用此字段判断授权是否允许被删除
     */
    private Integer referencedFlag;

    /**
     * 首次引入时间
     */
    private String firstReferenceTime;
}
