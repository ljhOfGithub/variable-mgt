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
package com.wiseco.var.process.app.server.service.engine.runtime.spi;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.decision.engine.var.runtime.api.IOutsideExecutorProvider;
import com.wiseco.outside.service.rpc.feign.OutsideServiceFeign;
import com.wisecotech.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class OutsideExecutorProviderImpl implements IOutsideExecutorProvider {

    private final OutsideServiceFeign outsideServiceFeign;

    /**
     * OutsideExecutorProviderImpl
     *
     * @param outsideServiceFeign 外部服务调用
     */
    public OutsideExecutorProviderImpl(OutsideServiceFeign outsideServiceFeign) {
        this.outsideServiceFeign = outsideServiceFeign;
    }

    @Override
    public JSONObject serviceExecute(JSONObject serviceExecuteDto) {
        APIResult<JSONObject> result = outsideServiceFeign.serviceExecute(serviceExecuteDto);
        return result.getData();
    }
}
