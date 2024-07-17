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
package com.wiseco.var.process.app.server.service.support.toolkit.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: fudengkui
 * @since: 2023-03-08 11:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodTemplatePlaceholderBO implements Serializable {

    /**
     * 参数占位符
     */
    private String parameterPlaceholder;

    /**
     * 参数类型描述
     */
    private String parameterTypeDesc;

}
