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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessTestMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTest;
import com.wiseco.var.process.app.server.service.VarProcessTestService;
import com.wiseco.var.process.app.server.service.dto.TestCollectAndResultsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 变量测试数据集 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-09
 */
@Service
public class VarProcessTestServiceImpl extends ServiceImpl<VarProcessTestMapper, VarProcessTest> implements VarProcessTestService {

    @Autowired
    private VarProcessTestMapper varProcessTestMapper;

    @Override
    public IPage<TestCollectAndResultsDto> findPageByVariableIdAndIdentifier(IPage<TestCollectAndResultsDto> page, Long spaceId, Integer testType,
                                                                             Long variableId, String identifier) {
        return varProcessTestMapper.findPageByVariableIdAndIdentifier(page, spaceId, testType, variableId, identifier);
    }

    @Override
    public Integer findMaxSeqNoByIdentifier(Long spaceId, String identifier) {
        Integer maxSeqNo = varProcessTestMapper.findMaxSeqNoByIdentifier(spaceId, identifier);
        Integer zero = Integer.valueOf("0");
        return (maxSeqNo == null) ? zero : maxSeqNo;
    }

    @Override
    public List<Long> getTestedManifests() {
        return varProcessTestMapper.getTestedManifests();
    }
}
