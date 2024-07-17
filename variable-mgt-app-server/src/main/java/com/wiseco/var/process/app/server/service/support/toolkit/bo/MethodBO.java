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
import java.util.List;

/**
 * @author: fudengkui
 * @since: 2023-02-21 14:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodBO implements Serializable {

    /**
     * 方法名称
     */
    private String name;

    /**
     * 返回值java类型
     */
    private String returnValueJavaType;

    /**
     * 返回值wrl类型
     */
    private String returnValueWrlType;

    /**
     * 方法返回值是否数组：0=否，1=是
     */
    private Integer returnValueIsArray;

    /**
     * 类名称
     */
    private String classCanonicalName;

    /**
     * 修饰符
     */
    private Integer modifier;

    /**
     * 方法参数
     */
    private List<ParameterBO> parameters;

}
