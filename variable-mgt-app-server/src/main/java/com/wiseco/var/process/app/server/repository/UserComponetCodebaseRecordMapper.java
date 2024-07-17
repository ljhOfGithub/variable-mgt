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
import com.wiseco.var.process.app.server.repository.entity.UserComponetCodebaseRecord;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * <p>
 * 变量空间-变量异常值配置
 * </p>
 *
 * @author kangyankun
 * @since 2022-08-31
 */

public interface UserComponetCodebaseRecordMapper extends BaseMapper<UserComponetCodebaseRecord> {

    /**
     * 更新使用次数
     *
     * @param codeBaseId 代码块
     * @return 使用次数
     */
    Integer updateUseTimes(@Param("codeBaseId") Long codeBaseId);
}