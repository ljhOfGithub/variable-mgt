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
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.var.process.app.server.controller.vo.VariableProduceRecordVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableRuleRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author chenzhuang
 */
public interface VarProcessVariableRuleRecordMapper extends BaseMapper<VarProcessVariableRuleRecord> {

    /**
     * 分页查询变量数据
     *
     * @param id       生成指记录id
     * @param planName 方案名
     * @param page     分页对象
     * @return 生成变量数据
     */
    IPage<VariableProduceRecordVo> findPageList(@Param("id") Long id, @Param("planName") String planName, IPage page);

    /**
     * 根据函数Id查询数据
     *
     * @param functionId 函数Id
     * @return 变量数据
     */
    List<Map<String, Object>> getListMaps(@Param("functionId") Long functionId);

    /**
     * 根据变量名字、变量编号查询变量数据
     *
     * @param name       变量名字
     * @param identifier 变量编号
     * @param functionId 函数Id
     * @return 变量数据
     */
    VariableProduceRecordVo selectByName(@Param("name") String name, @Param("identifier") String identifier, @Param("functionId") Long functionId);
}
