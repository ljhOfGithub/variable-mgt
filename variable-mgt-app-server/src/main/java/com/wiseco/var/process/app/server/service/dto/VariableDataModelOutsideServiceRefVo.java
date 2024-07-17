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
 * 数据模型是否引用外数服务Vo
 *
 * @author Wangxiansheng
 * @since 2023/8/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableDataModelOutsideServiceRefVo {

    /**
     * 数据模型ID
     */
    private Long dataModelId;

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
