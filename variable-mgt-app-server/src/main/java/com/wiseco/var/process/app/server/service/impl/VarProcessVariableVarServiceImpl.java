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
import com.wiseco.var.process.app.server.enums.DataModelRefTypeEnum;
import com.wiseco.var.process.app.server.enums.DataVariableBasicTypeEnum;
import com.wiseco.var.process.app.server.enums.PositionVarEnum;
import com.wiseco.var.process.app.server.repository.VarProcessVariableVarMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableVar;
import com.wiseco.var.process.app.server.service.VarProcessVariableVarService;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 变量-引用数据模型变量关系表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Service
public class VarProcessVariableVarServiceImpl extends ServiceImpl<VarProcessVariableVarMapper, VarProcessVariableVar> implements
        VarProcessVariableVarService {

    @Autowired
    private VarProcessVariableVarMapper varProcessVariableVarMapper;

    @Override
    public List<VariableUseVarPathDto> getVarUseList(Long spaceId) {
        return varProcessVariableVarMapper.getVarUseList(spaceId);
    }


    /**
     * 获取所有变量
     * @param queryList 查询列表
     * @param varFullPath 变量的全路径
     * @return 所有变量
     */
    @Override
    public List<VariableUseVarPathDto> getVarUse(List<VariableUseVarPathDto> queryList, String varFullPath) {
        Set<String> tmpSet = new HashSet<>();
        List<VariableUseVarPathDto> useVarList = new ArrayList<>();
        for (VariableUseVarPathDto dto : queryList) {
            //如果varFullPath 和 getVarPath相等 变量路径
            if (dto.getVarPath().equals(varFullPath)) {
                //数据模型引用类型枚举 DataModelRefTypeEnum  本地参数直接
                String tmpStr = dto.getId() + DataModelRefTypeEnum.DIRECT.getDesc();
                //如果set里边有tmpStr这个值跳出本次循环
                if (tmpSet.contains(tmpStr)) {
                    continue;
                }
                tmpSet.add(tmpStr);
                //ParameterType："参数/本地变量数据类型
                dto.setParameterType(DataModelRefTypeEnum.DIRECT.getDesc());
                useVarList.add(dto);
            } else {
                //如果varFullPath 和 getVarPath不相等
                if (StringUtils.isEmpty(dto.getParameterType()) || DataVariableBasicTypeEnum.getNameEnum(dto.getParameterType()) != null) {
                    continue;
                }
                String[] paramSplit = dto.getVarPath().split("\\.");
                String targetVarPath = dto.getVarPath().replaceFirst(paramSplit[0] + "." + paramSplit[1], dto.getParameterType());
                if (!targetVarPath.equals(varFullPath)) {
                    continue;
                }
                String useType;
                if (dto.getVarPath().startsWith(PositionVarEnum.PARAMETERS.getName())) {
                    useType = DataModelRefTypeEnum.PARAM.getDesc();
                } else {
                    useType = DataModelRefTypeEnum.LOCAL.getDesc();
                }
                String tmpStr = dto.getId() + useType;
                if (tmpSet.contains(tmpStr)) {
                    continue;
                }
                tmpSet.add(tmpStr);
                dto.setParameterType(useType);
                useVarList.add(dto);
            }
        }
        return useVarList;
    }
}
