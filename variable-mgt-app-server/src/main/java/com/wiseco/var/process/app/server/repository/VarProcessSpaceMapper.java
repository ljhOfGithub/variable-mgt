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
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

/**
 * <p>
 * 变量空间表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessSpaceMapper extends BaseMapper<VarProcessSpace> {

    /**
     * 根据Id更新用户名和时间
     *
     * @param userName    用户名
     * @param id          ID值
     * @param updatedTime 更新时间
     */
    @Update("update var_process_space set updated_user = #{userName}, updated_time = #{updatedTime} where id = #{id}")
    void updateSpaceUserNameById(@Param("userName") String userName, @Param("id") Long id, @Param("updatedTime") Date updatedTime);

    /**
     * 根据变量空间编码查询变量空间信息
     *
     * @param spaceCode 空间编码
     * @return 变量空间信息
     */
    @Select("select * from var_process_space where delete_flag=1 and code=#{spaceCode}")
    VarProcessSpace getBySpaceCode(@Param("spaceCode") String spaceCode);
}
