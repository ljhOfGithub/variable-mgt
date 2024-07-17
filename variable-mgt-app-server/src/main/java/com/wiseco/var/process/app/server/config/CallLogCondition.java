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
package com.wiseco.var.process.app.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CallLogCondition {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    @Documented
    @ConditionalOnProperty(value = "spring.datasourcetype",havingValue = "clickhouse")
    public  @interface OnClickhouseCallLogEnabled {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    @Documented
    @ConditionalOnProperty(value = "spring.datasourcetype",havingValue = "mysql")
    public @interface OnMysqlCallLogEnabled {

    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    @Documented
    @ConditionalOnProperty(value = "spring.datasourcetype",havingValue = "dm")
    public @interface OnDmCallLogEnabled {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    @Documented
    @ConditionalOnProperty(value = "spring.datasourcetype",havingValue = "sqlServer")
    public @interface OnSqlServerCallLogEnabled {

    }
}
