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
 * @since: 2023-02-21 14:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterBO implements Serializable {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数类型class
     */
    private Class<?> type;

    /**
     * java类型
     */
    private String javaType;

    /**
     * wrl类型
     */
    private String wrlType;

    /**
     * 参数索引
     */
    private Integer idx;

    /**
     * 是否数组：0=否，1=是
     */
    private Integer isArray;

}
