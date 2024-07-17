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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 公共函数间引用关系表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessFunctionReferenceService extends IService<VarProcessFunctionReference> {

    /**
     * 获取公共方法被引用关系
     *
     * @param funcid 公共方法id
     * @param spaceId 空间id
     * @return map
     */
    Map<FunctionTypeEnum, List<VarProcessFunction>> findFunctionRef(Long funcid, Long spaceId);

    /**
     * 获取被使用的公共方法 id set
     * @param spaceId 空间id
     * @return set
     */
    Set<Long> findUsedFunctions(Long spaceId);
}
