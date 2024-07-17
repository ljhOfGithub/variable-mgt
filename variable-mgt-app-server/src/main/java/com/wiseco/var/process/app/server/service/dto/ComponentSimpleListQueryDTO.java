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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: fudengkui
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentSimpleListQueryDTO implements Serializable {

    /**
     * 策略ID
     */
    private Long strategyId;

    /**
     * 组件ID
     */
    private Long oneselfComponentId;

    /**
     * 不包含的组件类型
     */
    private String excludeType;

    /**
     * 包含的组件类型
     */
    private List<String> includeTypes;

    /**
     * 是否删除
     */
    private Integer deleteFlag;

    /**
     * 组件名称或编码
     */
    private String nameOrCode;
}
