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
package com.wiseco.var.process.app.server.service.manifest;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessManifestFunctionMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 接口-公共函数关系表 服务实现类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-20
 */
@Service
public class VarProcessManifestFunctionService extends ServiceImpl<VarProcessManifestFunctionMapper, VarProcessManifestFunction> {

    @Autowired
    private VarProcessManifestFunctionMapper varProcessManifestFunctionMapper;

    /**
     * getFunctionListByIdentifier
     * @param identifier identifier
     * @return VarProcessManifest List
     */
    public List<VarProcessManifest> getFunctionListByIdentifier(String identifier) {
        return varProcessManifestFunctionMapper.getFunctionListByIdentifier(identifier);
    }

    /**
     * 获取被使用的公共方法 id set
     * @param spaceId 空间id
     * @return set
     */
    public Set<String> findUsedFunctions(Long spaceId) {
        return varProcessManifestFunctionMapper.findUsedFunctions(spaceId);
    }
}
