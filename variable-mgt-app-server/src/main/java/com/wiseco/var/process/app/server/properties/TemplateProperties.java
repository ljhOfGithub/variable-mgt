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
package com.wiseco.var.process.app.server.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author:liaody
 * @since: 2020/4/12 10:50
 */
@Configuration
@PropertySource(value = {"classpath:/config/decision-component-static-template-config.yml"}, factory = YamlSourceFactory.class)
@Data
public class TemplateProperties {

    public static final String VAR_TEMPLATE_STATIC_KEY = "var_template_static";
    public static final String VAR_TEMPLATE_STATIC_EN_KEY = "var_template_static_en";

    @Value("${var_template_static:DefaultValue}")
    private String varTemplateStatic;

    @Value("${var_template_static_en:DefaultValue}")
    private String varTemplateStaticEn;
}
