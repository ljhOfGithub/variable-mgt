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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessManifestInternalMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestInternal;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 接口-内部数据关系表 服务实现类
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-22
 */
@Service
public class VarProcessManifestInternalServiceImpl extends ServiceImpl<VarProcessManifestInternalMapper, VarProcessManifestInternal> implements
        VarProcessManifestInternalService {

    @Autowired
    private VarProcessManifestInternalMapper varProcessManifestInternalMapper;

}
