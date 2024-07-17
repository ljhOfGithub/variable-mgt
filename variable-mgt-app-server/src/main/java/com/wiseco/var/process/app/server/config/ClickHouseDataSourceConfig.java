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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author wiseco
 */
@Configuration
@CallLogCondition.OnClickhouseCallLogEnabled
public class ClickHouseDataSourceConfig {

    /**
     * clickHouseDataSource
     *
     * @return javax.sql.DataSource
     */
    @Bean(name = "clickHouseDataSource")
    @ConfigurationProperties(prefix = "spring.clickhouse.datasource")
    public DataSource clickHouseDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * clickHouseJdbcTemplate
     * @param dataSource 数据源
     * @return JdbcTemplate对象
     */
    @Bean(name = "clickHouseJdbcTemplate")
    public JdbcTemplate clickHouseJdbcTemplate(@Qualifier("clickHouseDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}

