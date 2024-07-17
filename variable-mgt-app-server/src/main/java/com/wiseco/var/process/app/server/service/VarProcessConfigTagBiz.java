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
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.var.process.app.server.commons.util.ObjectUtils;
import com.wiseco.var.process.app.server.controller.vo.input.OpeType;
import com.wiseco.var.process.app.server.controller.vo.input.TagDeleteCheckVo;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessConfigTagGroupSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessConfigTagInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessConfigTagQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessConfigTagSaveInputDto;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTag;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigTagGroup;
import com.wiseco.var.process.app.server.service.common.AuthService;
import com.wiseco.var.process.app.server.service.common.UserService;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigTagDto;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigTagGroupDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 变量标签配置
 *
 * @author wangxianli
 * @since 2022/08/30
 */
@Slf4j
@Service
public class VarProcessConfigTagBiz {

    public static final String LIMIT_1 = " limit 1";
    @Autowired
    private VarProcessConfigTagGroupService varProcessConfigTagGroupService;

    @Autowired
    private VarProcessConfigTagService varProcessConfigTagService;

    @Autowired
    private VarProcessVariableTagService varProcessVariableTagService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    /**
     * getList
     * @param inputDto 输入
     * @return IPage
     */
    public IPage<VarProcessConfigTagGroupDto> getList(VarProcessConfigTagQueryInputDto inputDto) {

        // 分页设置
        Page<VarProcessConfigTagGroup> page = new Page<>(inputDto.getCurrentNo(), inputDto.getSize());
        List<Long> idList = new ArrayList<>();

        if (!StringUtils.isEmpty(inputDto.getKeywords())) {
            List<VarProcessConfigTag> tagList = varProcessConfigTagService.list(
                    new QueryWrapper<VarProcessConfigTag>().lambda()
                            .eq(VarProcessConfigTag::getVarProcessSpaceId, inputDto.getSpaceId())
                            .like(VarProcessConfigTag::getName, inputDto.getKeywords())
            );
            if (!CollectionUtils.isEmpty(tagList)) {
                idList = tagList.stream().map(VarProcessConfigTag::getGroupId).collect(Collectors.toList());

            }
        }

        //数据权限
        RoleDataAuthorityDTO roleDataAuthority = authService.getAllAuthority();
        if (ObjectUtils.allFieldsAreNull(roleDataAuthority)) {
            return new Page<>();
        }
        IPage<VarProcessConfigTagGroupDto> list = varProcessConfigTagGroupService.getList(page, inputDto.getSpaceId(), inputDto.getKeywords(), idList,roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());

        if (CollectionUtils.isEmpty(list.getRecords())) {
            return list;
        }
        List<Long> groupIdList = new ArrayList<>();
        List<String> userName = list.getRecords().stream().flatMap(item -> Stream.of(item.getUpdatedUser())).distinct().collect(Collectors.toList());
        Map<String, String> fullNameMap = userService.findFullNameMapByUserNames(userName);
        for (VarProcessConfigTagGroupDto groupDto : list.getRecords()) {
            groupDto.setTagList(new ArrayList<>());
            groupDto.setUpdatedUser(fullNameMap.get(groupDto.getUpdatedUser()));
            groupIdList.add(groupDto.getGroupId());
        }
        LambdaQueryWrapper<VarProcessConfigTag> queryWrapper = new QueryWrapper<VarProcessConfigTag>().lambda()
                .eq(VarProcessConfigTag::getVarProcessSpaceId, inputDto.getSpaceId())
                .in(VarProcessConfigTag::getGroupId, groupIdList)
                .orderByAsc(VarProcessConfigTag::getSortOrder);
        if (!StringUtils.isEmpty(inputDto.getKeywords())) {
            queryWrapper.like(VarProcessConfigTag::getName, inputDto.getKeywords());
        }
        List<VarProcessConfigTag> tagList = varProcessConfigTagService.list(queryWrapper);
        if (!CollectionUtils.isEmpty(tagList)) {
            Map<Long, List<VarProcessConfigTag>> tagMap = tagList.stream().collect(Collectors.groupingBy(VarProcessConfigTag::getGroupId));
            for (VarProcessConfigTagGroupDto groupDto : list.getRecords()) {
                List<VarProcessConfigTag> varProcessConfigTags = tagMap.get(groupDto.getGroupId());
                List<VarProcessConfigTagDto> tagDtos = new ArrayList<>();
                if (!CollectionUtils.isEmpty(varProcessConfigTags)) {
                    for (VarProcessConfigTag tag : varProcessConfigTags) {
                        VarProcessConfigTagDto tagDto = new VarProcessConfigTagDto();
                        tagDto.setTagId(tag.getId());
                        tagDto.setGroupId(tag.getGroupId());
                        tagDto.setTagName(tag.getName());
                        tagDto.setSortOrder(tag.getSortOrder());
                        tagDtos.add(tagDto);
                    }
                }
                groupDto.setTagList(tagDtos);

            }

        }
        return list;

    }

    /**
     * getTagTrees
     * @param inputDto 输入
     * @return List
     */
    public List<VarProcessConfigTagGroupDto> getTagTrees(VarProcessConfigTagQueryInputDto inputDto) {
        RoleDataAuthorityDTO roleDataAuthority = authService.getRoleDataAuthority();
        if (ObjectUtils.allFieldsAreNull(roleDataAuthority)) {
            return new ArrayList<>();
        }
        List<VarProcessConfigTagGroupDto> tagTrees = varProcessConfigTagGroupService.getTagTrees(inputDto.getSpaceId(), inputDto.getKeywords(),roleDataAuthority.getDeptCodes(),roleDataAuthority.getUserNames());
        for (VarProcessConfigTagGroupDto groupDto : tagTrees) {
            if (groupDto.getTagList().size() == 1 && StringUtils.isEmpty(groupDto.getTagList().get(0).getTagName())) {
                groupDto.setTagList(new ArrayList<>());
            }
        }
        return tagTrees;

    }

    /**
     * saveGroup
     * @param inputDto inputDto
     */
    public void saveGroup(VarProcessConfigTagGroupSaveInputDto inputDto) {

        VarProcessConfigTagGroup tagGroup = new VarProcessConfigTagGroup();
        tagGroup.setGroupName(inputDto.getGroupName().trim());
        if (inputDto.getGroupId() != null) {
            //编辑
            List<VarProcessConfigTagGroup> list = varProcessConfigTagGroupService.list(
                    new QueryWrapper<VarProcessConfigTagGroup>().lambda()
                            .eq(VarProcessConfigTagGroup::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessConfigTagGroup::getId, inputDto.getGroupId())
            );
            if (CollectionUtils.isEmpty(list)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.CONFIG_TAG_GROUP_NOT_CONFIG,"未查询到标签组");
            }

            List<VarProcessConfigTagGroup> groupList = varProcessConfigTagGroupService.list(
                    new QueryWrapper<VarProcessConfigTagGroup>().lambda()
                            .eq(VarProcessConfigTagGroup::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessConfigTagGroup::getGroupName, tagGroup.getGroupName())
                            .ne(VarProcessConfigTagGroup::getId, inputDto.getGroupId())
            );
            if (!CollectionUtils.isEmpty(groupList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"标签组名称已存在");
            }

            tagGroup.setId(inputDto.getGroupId());
        } else {
            //新建
            List<VarProcessConfigTagGroup> groupList = varProcessConfigTagGroupService.list(
                    new QueryWrapper<VarProcessConfigTagGroup>().lambda()
                            .eq(VarProcessConfigTagGroup::getVarProcessSpaceId, inputDto.getSpaceId())
                            .eq(VarProcessConfigTagGroup::getGroupName, tagGroup.getGroupName())
            );
            if (!CollectionUtils.isEmpty(groupList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"标签组名称已存在");
            }
            tagGroup.setCreatedUser(SessionContext.getSessionUser().getUsername());
            tagGroup.setDeptCode(SessionContext.getSessionUser().getUser().getDepartment().getCode());
            Integer maxNo = varProcessConfigTagGroupService.getMaxOrderNo();
            tagGroup.setOrderNo(maxNo == null ? 0 : maxNo + 1);
        }
        tagGroup.setVarProcessSpaceId(inputDto.getSpaceId());
        tagGroup.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        tagGroup.setUpdatedTime(new Date());
        varProcessConfigTagGroupService.saveOrUpdate(tagGroup);
    }

    /**
     * deleteGroup
     * @param inputDto inputDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(VarProcessConfigTagInputDto inputDto) {
        deleteTagGroupCheck(inputDto);

        //删除标签
        varProcessConfigTagService.remove(
                new QueryWrapper<VarProcessConfigTag>().lambda()
                        .eq(VarProcessConfigTag::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessConfigTag::getGroupId, inputDto.getId())
        );

        //删除标签组
        varProcessConfigTagGroupService.remove(
                new QueryWrapper<VarProcessConfigTagGroup>().lambda()
                        .eq(VarProcessConfigTagGroup::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessConfigTagGroup::getId, inputDto.getId())
        );

    }

    private void deleteTagGroupCheck(VarProcessConfigTagInputDto inputDto) {
        List<VarProcessConfigTagGroup> list = varProcessConfigTagGroupService.list(
                new QueryWrapper<VarProcessConfigTagGroup>().lambda()
                        .eq(VarProcessConfigTagGroup::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessConfigTagGroup::getId, inputDto.getId())
        );
        if (CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.CONFIG_TAG_GROUP_NOT_CONFIG,"未查询到标签组信息");
        }
        //查询是否被使用
        int countTagGroup = varProcessVariableTagService.countTagGroup(inputDto.getSpaceId(), inputDto.getId());
        if (countTagGroup > 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"标签组已被使用，不允许删除。");
        }
    }

    /**
     * saveTag
     * @param inputDto inputDto
     */
    public void saveTag(VarProcessConfigTagSaveInputDto inputDto) {
        VarProcessConfigTag tag = new VarProcessConfigTag();
        tag.setName(inputDto.getTagName().trim());
        tag.setSortOrder(inputDto.getSortOrder());
        tag.setGroupId(inputDto.getGroupId());
        tag.setVarProcessSpaceId(inputDto.getSpaceId());
        List<VarProcessConfigTag> groupList = varProcessConfigTagService.list(
                new QueryWrapper<VarProcessConfigTag>().lambda()
                        .eq(VarProcessConfigTag::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessConfigTag::getName, tag.getName())
        );
        if (!CollectionUtils.isEmpty(groupList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"标签名称已存在");
        }
        tag.setCreatedUser(SessionContext.getSessionUser().getUsername());
        tag.setUpdatedUser(SessionContext.getSessionUser().getUsername());
        tag.setUpdatedTime(new Date());
        varProcessConfigTagService.saveOrUpdate(tag);
    }

    /**
     * deleteTag
     * @param inputDto inputDto
     */
    public void deleteTag(VarProcessConfigTagInputDto inputDto) {
        deleteTagCheck(inputDto);

        //删除标签
        varProcessConfigTagService.remove(
                new QueryWrapper<VarProcessConfigTag>().lambda()
                        .eq(VarProcessConfigTag::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessConfigTag::getId, inputDto.getId())
        );

    }

    private void deleteTagCheck(VarProcessConfigTagInputDto inputDto) {
        List<VarProcessConfigTag> list = varProcessConfigTagService.list(
                new QueryWrapper<VarProcessConfigTag>().lambda()
                        .eq(VarProcessConfigTag::getVarProcessSpaceId, inputDto.getSpaceId())
                        .eq(VarProcessConfigTag::getId, inputDto.getId())
        );
        if (CollectionUtils.isEmpty(list)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.CONFIG_TAG_GROUP_NOT_CONFIG,"未查询到标签信息");
        }
        //查询是否被使用
        int countTagGroup = varProcessVariableTagService.countTag(inputDto.getSpaceId(), list.get(0).getName());
        if (countTagGroup > 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL,"标签已被使用，不允许删除。");
        }
    }

    /**
     * 删除校验
     *
     * @param inputDto 入参
     * @return 提示信息
     */
    public String deleteCheck(TagDeleteCheckVo inputDto) {
        VarProcessConfigTagInputDto tagDto = VarProcessConfigTagInputDto.builder().id(inputDto.getId()).spaceId(inputDto.getSpaceId()).build();
        if (inputDto.getTag()) {
            deleteTagCheck(tagDto);
        } else {
            deleteTagGroupCheck(tagDto);
        }
        return "确认删除？";
    }

    /**
     * 上移/下移标签组
     * @param groupId 标签组id
     * @param opeType 操作类型
     * @return true or false
     */
    public Boolean moveTagGroup(Long groupId, OpeType opeType) {
        VarProcessConfigTagGroup originalGroup = varProcessConfigTagGroupService.getById(groupId);

        LambdaQueryWrapper<VarProcessConfigTagGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VarProcessConfigTagGroup::getVarProcessSpaceId, originalGroup.getVarProcessSpaceId());

        Integer orderNo = originalGroup.getOrderNo();
        if (OpeType.UP.equals(opeType)) {
            queryWrapper.lt(VarProcessConfigTagGroup::getOrderNo,orderNo).orderByDesc(VarProcessConfigTagGroup::getOrderNo);
        } else {
            queryWrapper.gt(VarProcessConfigTagGroup::getOrderNo,orderNo).orderByAsc(VarProcessConfigTagGroup::getOrderNo);
        }

        Optional<VarProcessConfigTagGroup> option = varProcessConfigTagGroupService.list(queryWrapper).stream().findFirst();
        option.ifPresent(targetGroup -> {
            Integer targetNo = targetGroup.getOrderNo();
            targetGroup.setOrderNo(orderNo);
            originalGroup.setOrderNo(targetNo);
            varProcessConfigTagGroupService.updateBatchById(Arrays.asList(originalGroup,targetGroup));
        });

        return true;
    }
}
