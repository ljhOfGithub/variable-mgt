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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.boot.commons.lang.StringUtils;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.decision.common.utils.DateUtil;
import com.wiseco.var.process.app.server.commons.enums.CodeBaseResourceTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.UserComponetCodebaseRecord;
import com.wiseco.var.process.app.server.service.dto.input.UserCodeBaseSearchPageInputDto;
import com.wiseco.var.process.app.server.service.dto.input.UserComponentCodeBaseUpdateInputDto;
import com.wiseco.var.process.app.server.service.dto.input.UserComponetCodeBaseInputDto;
import com.wiseco.var.process.app.server.service.dto.output.UserCodeBaseOutputDto;
import com.wiseco.var.process.app.server.service.dto.output.UserCompnentCodeBasePageOutputDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kangyankun
 * @since 2022/8/30
 */
@Slf4j
@Service
public class UserComponetCodeBaseBiz {

    @Autowired
    private UserComponetCodebaseRecordService userComponetCodebaseRecordService;

    /**
     * save
     * @param inputDto 输入实体类对象
     * @param codeBaseResourceTypeEnum 基于资源类型的编码枚举
     * @return ID
     */
    public Long save(UserComponetCodeBaseInputDto inputDto, CodeBaseResourceTypeEnum codeBaseResourceTypeEnum) {

        List<UserComponetCodebaseRecord> list = userComponetCodebaseRecordService.list(new QueryWrapper<UserComponetCodebaseRecord>().lambda()
                .select(UserComponetCodebaseRecord::getId)
                .eq(UserComponetCodebaseRecord::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                .eq(UserComponetCodebaseRecord::getSourceType, codeBaseResourceTypeEnum.getCode())
                .eq(UserComponetCodebaseRecord::getCodeBlockName, inputDto.getCodeBlockName())
                .eq(UserComponetCodebaseRecord::getUserId, SessionContext.getSessionUser().getUser().getId())
        );
        if (!CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "收藏代码块中文名重复，请修改");
        }
        if (StringUtils.isEmpty(inputDto.getCodeBlockName())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "收藏代码块中文名不能为空");
        }
        UserComponetCodebaseRecord value = new UserComponetCodebaseRecord();
        BeanUtils.copyProperties(inputDto, value);
        value.setUserId(SessionContext.getSessionUser().getUser().getId());
        value.setCreatedUser(SessionContext.getSessionUser().getUsername());
        value.setSourceType(codeBaseResourceTypeEnum.getCode());
        userComponetCodebaseRecordService.save(value);
        return value.getId();
    }

    /**
     * list
     * @return UserCodeBaseOutputDto List
     */
    public List<UserCodeBaseOutputDto> list() {

        //获取用户id
        Integer userId = SessionContext.getSessionUser().getUser().getId();

        List<UserComponetCodebaseRecord> userComponetCodebaseRecordList = userComponetCodebaseRecordService.list(new QueryWrapper<UserComponetCodebaseRecord>().lambda()
                .eq(UserComponetCodebaseRecord::getDeleteFlag, 1)
                .eq(UserComponetCodebaseRecord::getUserId, userId).orderByDesc(UserComponetCodebaseRecord::getCodeBlockUseTimes));

        List<UserCodeBaseOutputDto> outputDtoList = new ArrayList<>();
        for (UserComponetCodebaseRecord userComponetCodebaseRecord : userComponetCodebaseRecordList) {
            UserCodeBaseOutputDto outputDto = new UserCodeBaseOutputDto();
            BeanUtils.copyProperties(userComponetCodebaseRecord, outputDto);
            outputDtoList.add(outputDto);
        }
        return outputDtoList;
    }

    /**
     * 分页查询
     * @param inputDto 输入实体类对象
     * @param codeBaseResourceTypeEnum 基本编码类型的资源枚举
     * @return 代码库分页出参Dto
     */
    public UserCompnentCodeBasePageOutputDto pageList(UserCodeBaseSearchPageInputDto inputDto, CodeBaseResourceTypeEnum codeBaseResourceTypeEnum) {

        // 分页设置
        Page<UserComponetCodebaseRecord> page = new Page<>(inputDto.getCurrentNo(), inputDto.getSize());

        // 查询条件设置
        LambdaQueryWrapper<UserComponetCodebaseRecord> queryWrapper = new LambdaQueryWrapper<>();
        // 按照使用频率倒序
        queryWrapper.orderByDesc(UserComponetCodebaseRecord::getCodeBlockUseTimes);
        queryWrapper.eq(UserComponetCodebaseRecord::getUserId, SessionContext.getSessionUser().getUser().getId());
        queryWrapper.eq(UserComponetCodebaseRecord::getSourceType, codeBaseResourceTypeEnum.getCode());
        queryWrapper.eq(UserComponetCodebaseRecord::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());
        if (!StringUtils.isEmpty(inputDto.getSearchInfo())) {
            // 用户名模糊查询
            queryWrapper.like(UserComponetCodebaseRecord::getCodeBlockName, inputDto.getSearchInfo());
        }
        queryWrapper.orderByDesc(UserComponetCodebaseRecord::getCreatedTime);
        IPage<UserComponetCodebaseRecord> pageList = userComponetCodebaseRecordService.page(page, queryWrapper);

        List<UserCompnentCodeBasePageOutputDto.UserCodeBaseOutputDto> outputDtoList = new ArrayList<>();
        for (UserComponetCodebaseRecord codebaseRecord : pageList.getRecords()) {
            UserCompnentCodeBasePageOutputDto.UserCodeBaseOutputDto outputDto = new UserCompnentCodeBasePageOutputDto.UserCodeBaseOutputDto();
            BeanUtils.copyProperties(codebaseRecord, outputDto);
            outputDto.setCreatedTime(DateUtil.parseDateToStr(codebaseRecord.getCreatedTime(), DateUtil.FORMAT_LONG));
            outputDtoList.add(outputDto);
        }

        return UserCompnentCodeBasePageOutputDto.builder()
                .currentPageNo(page.getCurrent())
                .totalPageNumber(page.getPages())
                .totalRecordNumber(page.getTotal())
                .records(outputDtoList).build();

    }

    /**
     * 更新使用时间
     * @param inputDto 输入实体类对象
     * @return 更新使用时间的结果
     */

    public Boolean updateUseTimes(UserComponentCodeBaseUpdateInputDto inputDto) {
        return userComponetCodebaseRecordService.updateUseTimes(inputDto.getId());
    }

    /**
     * 删除
     * @param inputDto 输入实体类对象
     * @return 删除结果
     */
    public Boolean delete(UserComponentCodeBaseUpdateInputDto inputDto) {
        UserComponetCodebaseRecord userComponetCodebaseRecord = new UserComponetCodebaseRecord();
        userComponetCodebaseRecord.setId(inputDto.getId());
        userComponetCodebaseRecord.setDeleteFlag(0);
        return userComponetCodebaseRecordService.updateById(userComponetCodebaseRecord);
    }

    /**
     * 批删除
     * @param inputDto 输入实体类对象
     * @return 批删除的结果
     */
    public Boolean batchDelete(UserComponentCodeBaseUpdateInputDto inputDto) {
        if (CollectionUtils.isEmpty(inputDto.getIdList())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "删除id信息为空");
        }
        List<UserComponetCodebaseRecord> updateList = new ArrayList<>();
        for (Long id : inputDto.getIdList()) {
            UserComponetCodebaseRecord userComponetCodebaseRecord = new UserComponetCodebaseRecord();
            userComponetCodebaseRecord.setId(id);
            userComponetCodebaseRecord.setDeleteFlag(0);
            updateList.add(userComponetCodebaseRecord);
        }
        return userComponetCodebaseRecordService.updateBatchById(updateList);
    }

}
