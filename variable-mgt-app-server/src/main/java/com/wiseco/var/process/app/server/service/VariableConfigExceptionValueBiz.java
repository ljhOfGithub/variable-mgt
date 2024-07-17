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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.GenerateIdUtil;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessCategoryCheckInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigExceptionValueInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableConfigExceptionValueQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableConfigExceptionValueOutputDto;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigExcept;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigExceptionDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigExceptionQueryDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 异常值设置服务
 *
 * @author kangyk
 * @since 2022/08/30
 */
@Slf4j
@Service
public class VariableConfigExceptionValueBiz {

    @Autowired
    private VarProcessConfigExceptionService varProcessConfigExceptionValueService;

    /**
     * 保存或更新配置的异常值
     * @param inputDto 输入实体类对象
     * @return 保存或更新配置的异常值的结果
     */
    public Long saveOrUpdateConfigExceptionValue(VariableConfigExceptionValueInputDto inputDto) {
        //重复数据校验
        Long zero = Long.valueOf(String.valueOf(MagicNumbers.ZERO));
        List<VarProcessConfigExcept> varProcessConfigExceptList = varProcessConfigExceptionValueService.list(new QueryWrapper<VarProcessConfigExcept>().lambda()
                .eq(VarProcessConfigExcept::getDataType, inputDto.getDataType())
                .eq(VarProcessConfigExcept::getExceptionValue, inputDto.getExceptionValue())
                .eq(VarProcessConfigExcept::getDeleteFlag, 1)
                .ne(VarProcessConfigExcept::getId, ObjectUtils.isEmpty(inputDto.getId()) ? zero : inputDto.getId())
                .eq(VarProcessConfigExcept::getVarProcessSpaceId, inputDto.getVarProcessSpaceId()));

        if (!CollectionUtils.isEmpty(varProcessConfigExceptList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"该值已存在，不允许重复");
        }

        VarProcessConfigExcept value = new VarProcessConfigExcept();
        BeanUtils.copyProperties(inputDto, value);
        value.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        value.setUpdatedTime(new Date());
        Long minusOne = Long.valueOf(String.valueOf("-1"));
        if (ObjectUtils.isNotEmpty(inputDto.getId())) {
            return !varProcessConfigExceptionValueService.updateById(value) ? minusOne : inputDto.getId();
        }
        BeanUtils.copyProperties(inputDto, value);
        if (ObjectUtils.isNotEmpty(inputDto.getId())) {
            return !varProcessConfigExceptionValueService.updateById(value) ? minusOne : inputDto.getId();
        }
        value.setCreatedUser(SessionContext.getSessionUser().getUsername());
        value.setExceptionValueCode(GenerateIdUtil.generateId());
        varProcessConfigExceptionValueService.save(value);
        return value.getId();
    }

    /**
     * 分页获取配置异常值的list
     * @param inputDto 输入实体类对象
     * @return 配置异常值的list
     */
    public IPage<VariableConfigExceptionValueOutputDto> getConfigExceptionValueList(VariableConfigExceptionValueQueryInputDto inputDto) {

        // 分页设置
        Page<VariableConfigExceptionValueOutputDto> page = new Page<>(inputDto.getCurrentNo(), inputDto.getSize());

        VarProcessConfigExceptionQueryDto varProcessConfigExceptionQueryDto = new VarProcessConfigExceptionQueryDto();

        BeanUtils.copyProperties(inputDto, varProcessConfigExceptionQueryDto);

        varProcessConfigExceptionQueryDto.setDeleteFlag(1);

        varProcessConfigExceptionQueryDto.setVarProcessSpaceId(inputDto.getVarProcessSpaceId().intValue());

        IPage<VarProcessConfigExceptionDto> pageList = varProcessConfigExceptionValueService.getConfigExceptionValueList(page,
                varProcessConfigExceptionQueryDto);

        // 获取用户名姓名 Map
        // Map<String, String> userFullNameMap = null;
        // 新建出参 DTO 列表, 填充并添加类别
        List<VariableConfigExceptionValueOutputDto> outputDtoList = new ArrayList<>();
        List<VarProcessConfigExceptionDto> varProcessConfigDefaultValueList = pageList.getRecords();
        for (VarProcessConfigExceptionDto varProcessConfigExceptionDto : varProcessConfigDefaultValueList) {
            VariableConfigExceptionValueOutputDto outputDto = new VariableConfigExceptionValueOutputDto();
            BeanUtils.copyProperties(varProcessConfigExceptionDto, outputDto);
            // 修改输出参数 "最后编辑人" 为用户姓名
            outputDto.setUpdatedUser(outputDto.getUpdatedUser());
            outputDto.setExceptionValue(varProcessConfigExceptionDto.getExceptionValue());
            outputDto.setExceptionType(varProcessConfigExceptionDto.getExceptionType());
            outputDto.setUpdatedTime(DateUtil.parseDateToStr(varProcessConfigExceptionDto.getUpdatedTime(), DateUtil.FORMAT_LONG));
            outputDtoList.add(outputDto);
        }

        page.setTotal(pageList.getTotal());
        page.setPages(pageList.getPages());
        page.setRecords(outputDtoList);

        return page;
    }

    /**
     * 删除
     * @param inputDto 输入实体类对象
     * @return 删除的结果
     */
    public Boolean delete(VariableConfigExceptionValueInputDto inputDto) {
        //判断是否有子类
        VarProcessConfigExcept value = new VarProcessConfigExcept();
        value.setId(inputDto.getId());
        value.setDeleteFlag(0);
        return varProcessConfigExceptionValueService.updateById(value);
    }

    /**
     * 检查删除的异常值
     * @param inputDto 输入实体类对象
     */
    public void checkDeleteExceptionValue(VarProcessCategoryCheckInputDto inputDto) {

        VarProcessConfigExcept varProcessConfigExcept = varProcessConfigExceptionValueService.getById(inputDto.getId());
        if (ObjectUtils.isEmpty(varProcessConfigExcept)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"删除异常值参数错误");
        }
        if (varProcessConfigExcept.getExceptionType().equals(1)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"内置异常，不可删除");
        }

    }

}
