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
package com.wiseco.var.process.app.server.annotation;

import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wiseco
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoggableMethod {

    /**
     * 方法动作
     * @return String
     */
    String value() default "";

    /**
     * 方法参数
     * @return String[]
     */
    String[] params() default {};

    /**
     * 操作类型
     * @return String
     */
    LoggableMethodTypeEnum type();
}
