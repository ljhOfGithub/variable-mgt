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
package com.wiseco.var.process.app.server.service.engine;

import com.wiseco.decision.engine.var.transform.component.data.VarCompileData;
import com.wiseco.var.process.app.server.enums.TestVariableTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;

/**
 * IComponentDataBuildService接口
 * 
 * @author chimeng
 * @since 2023/8/7
 */
public interface IComponentDataBuildService {
    /**
     * 构建组件数据
     * 
     * @param data 编译数据
     * @param type 测试类型
     * @param space 变量空间
     * @param componentId 组件id
     */
    void buildComponentData(VarCompileData data, TestVariableTypeEnum type, VarProcessSpace space, Long componentId);
}
