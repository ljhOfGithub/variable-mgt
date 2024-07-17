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
 * @since: 2023-02-21 14:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeBO implements Serializable {

    /**
     * 属性名称
     */
    private String name;

    /**
     * java类型
     */
    private String javaType;

    /**
     * WRL类型
     */
    private String wrlType;

    /**
     * 属性类型是否数组：0=否，1=是
     */
    private Integer typeIsArray;

    /**
     * 修饰符
     */
    private Integer modifier;

    /**
     * 访问：read/write，readonly
     */
    private String access;

    /**
     * 属性来源：1=字段，2=方法
     */
    private Integer sourceType;

}
