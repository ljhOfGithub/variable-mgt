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

import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;

/**
 * @author wiseco
 */
@Configuration
public class MySqlDataSourceConfig implements ResourceLoaderAware {
    private final Logger logger = LoggerFactory.getLogger(MySqlDataSourceConfig.class);

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.xml";

    private static final String RESOURCE_PATTERN = DEFAULT_RESOURCE_PATTERN;

    @Value("${mybatis.mapper.path:mapper}")
    private String resourcePath;

    @Value("${mybatis-plus.configuration.log-impl:org.apache.ibatis.logging.slf4j.Slf4jImpl}")
    private String logImpl;

    private ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();

    /**
     * mySqlDataSource
     * 睿信配置数据库
     * @return javax.sql.DataSource
     */
    @Primary
    @Bean("configDatasource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mySqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * internalJdbcTemplate
     * @param dataSource 数据源
     * @return JdbcTemplate对象
     */
    @Bean(name = "configDatasourceTemplate")
    public JdbcTemplate internalJdbcTemplate(@Qualifier("configDatasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * sqlSessionFactory
     *
     * @param dataSource
     * @param identifierGenerator
     * @param mybatisPlusInterceptor
     * @param databaseIdProvider
     * @return org.apache.ibatis.session.SqlSessionFactory
     */
    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource,
                                               IdentifierGenerator identifierGenerator,
                                               MybatisPlusInterceptor mybatisPlusInterceptor,
                                               DatabaseIdProvider databaseIdProvider) {
        try {
            MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            bean.setVfs(SpringBootVFS.class);
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + ClassUtils.convertClassNameToResourcePath(resourcePath)
                    + '/' + RESOURCE_PATTERN;
            logger.debug("============>>>>  primaryDataSource ： mapper search path:{}", packageSearchPath);
            Resource[] resources = resourceLoader.getResources(packageSearchPath);
            for (Resource res : resources) {
                logger.debug("============>>>>  primaryDataSource ：loaded mapper file:{}", res.getFilename());
            }
            bean.setMapperLocations(resources);
            bean.setDatabaseIdProvider(databaseIdProvider);
            //默认的属性
            MybatisConfiguration config = new MybatisConfiguration();
            config.addInterceptor(mybatisPlusInterceptor);
            config.setMapUnderscoreToCamelCase(true);
            // 设置JdbcType.NULL，不然连接oracle数据库时，参数传入为null的时候，并且sql没有指定JdbcType，mybatis将无法识别是什么类型
            config.setJdbcTypeForNull(JdbcType.NULL);
            config.setLogImpl((Class<? extends Log>) Class.forName(logImpl));
            GlobalConfig globalConfig = new GlobalConfig();
            globalConfig.setBanner(false);
            globalConfig.setIdentifierGenerator(identifierGenerator);

            bean.setConfiguration(config);
            bean.setGlobalConfig(globalConfig);
            return bean.getObject();
        } catch (Exception e) {
            throw new BeanCreationException("SqlSessionFactory create error:", e);
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        logger.debug("=>setResourceLoader");
        if (resourceLoader instanceof ResourcePatternResolver) {
            this.resourceLoader = (ResourcePatternResolver) resourceLoader;
        }
    }

}
