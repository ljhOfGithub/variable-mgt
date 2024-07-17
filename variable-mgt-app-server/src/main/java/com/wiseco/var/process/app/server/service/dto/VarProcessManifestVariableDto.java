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

/**
 * TODO
 *
 * @author: zhouxiuxiu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VarProcessManifestVariableDto implements Serializable {

    private static final long serialVersionUID = 2745409670823379382L;
    /**
     * 变量空间ID
     */
    private Long varProcessSpaceId;

    /**
     * 变量清单ID
     */
    private Long manifestId;

    /**
     * 变量ID
     */
    private Long variableId;

    /**
     * 变量名称
     */
    private String variableName;

    /**
     * 是否输出, 对应前端勾选框
     */
    private Integer outputFlag;

}
