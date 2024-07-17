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
package com.wiseco.var.process.app.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingDataModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VarProcessBatchBacktrackingDataModelMapper extends BaseMapper<VarProcessBatchBacktrackingDataModel> {

    /**
     * 通过批量回溯id获取批量回溯外数关系
     *
     * @param backtrackingId 批量回溯id
     * @return VarProcessBatchBacktrackingDataModel
     */
    @Select("SELECT * FROM var_process_batch_backtracking_data_model WHERE backtracking_id = #{backtrackingId}")
    List<VarProcessBatchBacktrackingDataModel> findListByBacktrackingId(Long backtrackingId);
}
