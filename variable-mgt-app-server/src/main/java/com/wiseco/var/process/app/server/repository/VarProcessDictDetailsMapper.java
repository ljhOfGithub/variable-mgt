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
import com.wiseco.var.process.app.server.repository.entity.VarProcessDictDetails;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 变量空间-字典详情表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessDictDetailsMapper extends BaseMapper<VarProcessDictDetails> {

    /**
     * 更新字典项信息
     *
     * @param dictId  字典id
     * @param oldCode 字典项旧编码
     * @param newCode 字典项新编码
     */
    void updateParent(@Param("dictId") Long dictId, @Param("oldCode") String oldCode, @Param("newCode") String newCode);
}
