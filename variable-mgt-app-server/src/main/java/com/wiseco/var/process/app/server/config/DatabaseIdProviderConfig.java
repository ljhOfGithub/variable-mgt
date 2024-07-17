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

import com.wiseco.boot.data.core.DatabaseTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@Slf4j
public class DatabaseIdProviderConfig {

    /**
     * databaseIdProvider
     *
     * @return org.apache.ibatis.mapping.DatabaseIdProvider
     */
    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.put(DatabaseTypeEnum.ORACLE.getProductName(), "oracle");
        properties.put(DatabaseTypeEnum.MYSQL.getProductName(), "mysql");
        properties.put(DatabaseTypeEnum.DM.getProductName(), "dm");
        properties.put(DatabaseTypeEnum.SQLSERVER.getProductName(), "sqlServer");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }
}
