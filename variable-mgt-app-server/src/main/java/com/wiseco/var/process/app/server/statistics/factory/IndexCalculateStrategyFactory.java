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
package com.wiseco.var.process.app.server.statistics.factory;

import com.wiseco.var.process.app.server.statistics.IndexCalculateStrategy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IndexCalculateStrategyFactory implements ApplicationContextAware {

    private static final Map<String, IndexCalculateStrategy> SERVICE = new ConcurrentHashMap<>(16);

    /**
     * 获取计算策略
     * @param indexName 索引名称
     * @return IndexCalculateStrategy实现类
     */
    public IndexCalculateStrategy getCalculateStrategy(String indexName) {
        return SERVICE.get(indexName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, IndexCalculateStrategy> beansMap = applicationContext.getBeansOfType(IndexCalculateStrategy.class);
        for (Map.Entry<String, IndexCalculateStrategy> entry : beansMap.entrySet()) {
            SERVICE.put(entry.getValue().getIndexName(), entry.getValue());
        }
    }
}
