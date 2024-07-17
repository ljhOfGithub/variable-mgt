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
import com.wiseco.var.process.app.server.repository.VarProcessManifestCycleMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestCycle;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestLifecycleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 变量清单-生命周期表 服务实现类
 * </p>
 *
 * @author liaody
 * @since 2022-08-24
 */
@Service
public class VarProcessManifestLifecycleServiceImpl extends ServiceImpl<VarProcessManifestCycleMapper, VarProcessManifestCycle> implements
        VarProcessManifestLifecycleService {

}
