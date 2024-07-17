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
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableVar;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量-引用数据模型变量关系表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableVarMapper extends BaseMapper<VarProcessVariableVar> {
    /**
     * 自己改的三表联查
     *
     * @param spaceId 空间id
     * @return 引用数据模型变量关系表
     */
    @Select("select vpv.id, vpvv.var_path, vpvv.parameter_type, vpv.name, vpv.label, vpv.version, vpv.status, vpc.name AS allClass from var_process_variable_var vpvv\n"
            + " INNER JOIN var_process_variable vpv ON vpvv.variable_id = vpv.id "
            + " INNER JOIN var_process_category vpc ON vpc.id = vpv.category_id "
            + " where vpvv.is_self = 1 and vpv.delete_flag = 1 and vpv.var_process_space_id = #{spaceId} order by vpv.identifier desc, vpv.version desc ")
    List<VariableUseVarPathDto> getVarUseList(@Param("spaceId") Long spaceId);

}
