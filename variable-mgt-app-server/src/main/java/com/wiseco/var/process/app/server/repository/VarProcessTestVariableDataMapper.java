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
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestVariableData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VarProcessTestVariableDataMapper extends BaseMapper<VarProcessTestVariableData> {

    /**
     * 根据数据集 ID 查找当前数据集最大的序列号
     *
     * @param testId 数据集 ID
     * @return 当前数据集最大的序列号
     */
    @Select("select max(data_id) from var_process_test_variable_data where test_id = #{testId}")
    Integer findMaxDataIdByTestId(@Param("testId") Long testId);
}
