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
import com.wiseco.var.process.app.server.controller.vo.input.DictListInputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDict;
import com.wiseco.var.process.app.server.service.dto.DictDetailsDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量空间-字典类型表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessDictMapper extends BaseMapper<VarProcessDict> {

    /**
     * 通过空间id获取字典详情
     *
     * @param spaceId 空间id
     * @return 字典列表
     */
    @Select("SELECT d.code as dictCode,d.name as dictName,s.code,s.name from var_process_dict d\n"
            + "INNER JOIN var_process_dict_details s on (d.object_name = s.object_name or (d.object_name is null and s.object_name is null)) and d.id=s.dict_id\n"
            + "WHERE d.delete_flag=1 and s.delete_flag=1 and d.var_process_space_id=#{spaceId}")
    List<DictDetailsDto> getDictDetails(@Param("spaceId") Long spaceId);

    /**
     * 获取字典详情
     *
     * @param inputDto 查询参数
     * @return 字典 List
     */
    List<VarProcessDict> getAllList(@Param("inputDto") DictListInputDto inputDto);

    /**
     * 通过dict的code，获取它的id
     * @param code 字典编码
     * @return 字典的Id
     */
    @Select("SELECT id FROM var_process_dict where code = #{code}")
    Long getIdByCode(@Param("code") String code);
}
