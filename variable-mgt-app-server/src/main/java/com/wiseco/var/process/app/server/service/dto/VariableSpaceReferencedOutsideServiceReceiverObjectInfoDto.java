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
 * 变量空间引用的外部服务接收对象信息 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableSpaceReferencedOutsideServiceReceiverObjectInfoDto {

    /**
     * 外部服务名称
     */
    private String outsideServiceName;

    /**
     * 接收对象英文名
     */
    private String receiverObjectName;

    /**
     * 接收对象中文名
     */
    private String receiverObjectLabel;

    /**
     * 外部数据出参结构
     */
    private String outputParameterBindings;
}
