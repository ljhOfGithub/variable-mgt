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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.controller.vo.input.DictListInputDto;
import com.wiseco.var.process.app.server.repository.VarProcessDictMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDict;
import com.wiseco.var.process.app.server.service.VarProcessDictService;
import com.wiseco.var.process.app.server.service.dto.DictDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 变量空间-字典类型表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessDictServiceImpl extends ServiceImpl<VarProcessDictMapper, VarProcessDict> implements VarProcessDictService {
    @Autowired
    private VarProcessDictMapper varProcessDictMapper;

    @Override
    public List<DictDetailsDto> getDictDetails(Long spaceId) {
        return varProcessDictMapper.getDictDetails(spaceId);
    }

    @Override
    public List<VarProcessDict> getAllList(DictListInputDto inputDto) {
        return varProcessDictMapper.getAllList(inputDto);
    }

    /**
     * 通过code，获取dict的id
     * @param code 编码
     * @return dict的id
     */
    public Long getIdByCode(String code) {
        return varProcessDictMapper.getIdByCode(code);
    }
}
