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
package com.wiseco.var.process.app.server.service.backtracking;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingDataModelMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingDataModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xupei
 */
@Service
public class BacktrackingDataModelService extends ServiceImpl<VarProcessBatchBacktrackingDataModelMapper, VarProcessBatchBacktrackingDataModel> {

    @Resource
    private VarProcessBatchBacktrackingDataModelMapper backtrackingDataModelMapper;

    /**
     * findListByBacktrackingId
     *
     * @param backtrackingId 批量回溯ID
     * @return java.util.List
     */
    public List<VarProcessBatchBacktrackingDataModel> findListByBacktrackingId(Long backtrackingId) {
        return backtrackingDataModelMapper.findListByBacktrackingId(backtrackingId);
    }
}
