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
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionReference;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * <p>
 * 公共函数间引用关系表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessFunctionReferenceMapper extends BaseMapper<VarProcessFunctionReference> {

    /**
     * 获取被使用的公共方法 id set
     * @param spaceId 空间id
     * @return set
     */
    @Select("select distinct vpfr.function_id  from var_process_function_reference vpfr where var_process_space_id = #{spaceId}")
    Set<Long> findUsedFunctions(@Param("spaceId") Long spaceId);
}
