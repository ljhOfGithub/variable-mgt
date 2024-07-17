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
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableOutside;
import com.wiseco.var.process.app.server.service.dto.VariableOutsideServiceDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 变量引用-外部服务及接收对象关系表 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessVariableOutsideMapper extends BaseMapper<VarProcessVariableOutside> {

    /**
     * 获取外部服务清单
     *
     * @param spaceId     空间id
     * @param variableIds 变量ids
     * @return 外部服务及接收对象关系表
     */
    List<VariableOutsideServiceDto> getOutsideServiceList(@Param("spaceId") Long spaceId, @Param("variableIds") List<Long> variableIds);

}
