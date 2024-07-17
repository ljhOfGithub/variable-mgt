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
package com.wiseco.var.process.app.server;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableDiscoveryClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableFeignClients(basePackages = "com.wiseco")
@MapperScan({"com.wiseco.var.process.app.server.repository"})
@SpringBootApplication(scanBasePackages = {
        "com.wiseco.var.process.app.server",
        "com.wiseco.var.process.engine.compiler",
        "com.wiseco.data",
        "com.wiseco.decision.nacos",
        "com.wiseco.log",
        "com.wiseco.outside",
        "com.wiseco.config",
        "com.wiseco.boot",
        "com.wiseco.decision.engine.var",
        "com.wiseco.decision.engine.java",
        "com.wiseco.var.service.rpc.feign"
},exclude = {DruidDataSourceAutoConfigure.class})
@Slf4j
@Configuration(proxyBeanMethods = false)
public class VarProcessApp {

    /**
     * 项目启动入口
     * @param args 人工添加的入参
     */
    public static void main(String[] args) {
        SpringApplication.run(VarProcessApp.class, args);
        log.info("VarProcessApp start successfully");
    }

}
