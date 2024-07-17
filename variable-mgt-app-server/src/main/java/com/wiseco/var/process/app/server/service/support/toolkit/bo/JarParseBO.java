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
 * @since: 2023-02-21 9:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JarParseBO implements Serializable {

    /**
     * 方法
     */
    List<MethodBO> methods;
    /**
     * 属性
     */
    List<AttributeBO> attributes;
    /**
     * 类名
     */
    private String classSimpleName;
    /**
     * 全路径类名称
     */
    private String classCanonicalName;
    /**
     * class对象
     */
    private Class clazz;
    /**
     * class类型：1=class，2=abstract class，3=interface，4=enum
     */
    private Integer classType;
    /**
     * class业务类型：1=class有属性也有方法，2=class有属性无方法，3=class无属性有方法，4=class无属性无方法
     */
    private Integer classBizType;
    /**
     * 修饰符
     */
    private Integer modifier;

}
