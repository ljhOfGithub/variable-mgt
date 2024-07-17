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
import com.wiseco.var.process.app.server.repository.VarProcessDictDetailsMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDictDetails;
import com.wiseco.var.process.app.server.service.VarProcessDictDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 变量空间-字典详情表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessDictDetailsServiceImpl extends ServiceImpl<VarProcessDictDetailsMapper, VarProcessDictDetails> implements
        VarProcessDictDetailsService {

    @Resource
    private VarProcessDictDetailsMapper dictDetailsMapper;

    @Override
    public void updateParent(Long dictId, String oldCode, String newCode) {
        dictDetailsMapper.updateParent(dictId, oldCode, newCode);
    }
}
