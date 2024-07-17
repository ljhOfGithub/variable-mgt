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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.repository.VarProcessSpaceMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 变量空间表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessSpaceServiceImpl extends ServiceImpl<VarProcessSpaceMapper, VarProcessSpace> implements VarProcessSpaceService {

    @Autowired
    private VarProcessSpaceMapper varProcessSpaceMapper;

    @Override
    public void updateSpaceUserNameById(String userName, Long id) {
        varProcessSpaceMapper.updateSpaceUserNameById(userName, id, new Date());
    }

    @Override
    public VarProcessSpace getBySpaceCode(String spaceCode) {
        return varProcessSpaceMapper.getBySpaceCode(spaceCode);
    }

    @Override
    public Map<Long,String> getIdNameMap() {
        List<VarProcessSpace> varProcessSpaces = varProcessSpaceMapper.selectList(Wrappers.<VarProcessSpace>lambdaQuery().select(VarProcessSpace::getId, VarProcessSpace::getName)
                .eq(VarProcessSpace::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        return varProcessSpaces.stream().collect(Collectors.toMap(VarProcessSpace::getId, VarProcessSpace::getName, (k1, k2) -> k2));
    }
}
