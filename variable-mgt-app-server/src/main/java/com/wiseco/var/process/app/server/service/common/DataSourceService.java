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
package com.wiseco.var.process.app.server.service.common;

import com.wiseco.auth.common.config.ConfigDatasourceManageOutput;
import com.wiseco.auth.common.config.ConfigDatasourceManageQueryInputParam;
import com.wiseco.boot.config.BusinessConfigClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 远程数据源
 */
@Service
public class DataSourceService {

    @Autowired
    private BusinessConfigClient businessConfigClient;
    /**
     * 获取mq类型数据源
     * @return list
     */
    public List<String> findMqDataSources() {
        ConfigDatasourceManageQueryInputParam configDatasourceManageQueryInputParam = new ConfigDatasourceManageQueryInputParam();
        configDatasourceManageQueryInputParam.setEnabled(true);
        configDatasourceManageQueryInputParam.setDsType("kafka");
        List<ConfigDatasourceManageOutput> configDatasourceManageOutputs = businessConfigClient.queryConfigDatasourceManageAllList(configDatasourceManageQueryInputParam);
        return configDatasourceManageOutputs.stream().map(ConfigDatasourceManageOutput::getDsDef).collect(Collectors.toList());
    }
}
