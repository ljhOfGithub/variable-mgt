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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.controller.vo.VariableProduceRecordVo;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableRuleRecord;

import java.util.List;
import java.util.Map;

public interface VariableRuleRecordService extends IService<VarProcessVariableRuleRecord> {
    /**
     * 分页查询生成变量数据
     *
     * @param id id
     * @param planName planName
     * @param page page
     * @return 分页结果
     */
    IPage<VariableProduceRecordVo> findPageList(Long id, String planName, Page page);

    /**
     * getlistMaps
     *
     * @param functionId functionId
     * @return map list
     */
    List<Map<String, Object>> getlistMaps(Long functionId);

    /**
     * 移除规则记录
     *
     * @param id id
     */
    void removeRuleRecord(Long id);

    /**
     * 查询生成变量list
     *
     * @param id id
     * @return list
     */
    List<VarProcessVariableRuleRecord> findList(Long id);

    /**
     * 通过名称跟变量模板去查，看是否重复的
     *
     * @param name name
     * @param identifier identifier
     * @param functionId functionId
     * @return 生成变量数据
     */
    VariableProduceRecordVo selectByName(String name, String identifier, Long functionId);
}
