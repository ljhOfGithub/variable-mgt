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
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestFunction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 接口-公共函数关系表 Mapper 接口
 * </p>
 *
 * @author wangxianli
 * @since 2022-09-20
 */
public interface VarProcessManifestFunctionMapper extends BaseMapper<VarProcessManifestFunction> {

    /**
     * 通过编号获取使用函数的变量清单信息
     *
     * @param identifier 编号
     * @return VarProcessManifest
     */
    @Select("SELECT vpm.id, vpm.state from var_process_manifest vpm INNER JOIN var_process_manifest_function vpmf ON vpm.id = vpmf.manifest_id\n"
            + "WHERE vpm.delete_flag = '1' and vpmf.identifier = #{identifier}")
    List<VarProcessManifest> getFunctionListByIdentifier(@Param("identifier") String identifier);

    /**
     * 获取使用该公共方法的清单list
     *
     * @param funcId 公共方法id
     * @param spaceId 空间id
     * @return 清单list
     */
    @Select("select vpm.id,vpm.var_manifest_name  from var_process_manifest vpm "
            + "inner join var_process_manifest_function vpmf on vpm.id = vpmf.manifest_id \n"
            + "inner join var_process_function vpf on vpf.identifier = vpmf.identifier\n"
            + "where vpf.id = #{funcId} and vpm.delete_flag = 1 and vpm.var_process_space_id = #{spaceId}")
    List<VarProcessManifest> findManifestUsingFunc(@Param("funcId")Long funcId, @Param("spaceId") Long spaceId);

    /**
     * 查询所有被清单使用的公共函数 identifier set
     * @param spaceId 空间id
     * @return set
     */
    @Select("select distinct vpmf.identifier from var_process_manifest_function vpmf where var_process_space_id = #{spaceId}")
    Set<String> findUsedFunctions(@Param("spaceId") Long spaceId);
}
