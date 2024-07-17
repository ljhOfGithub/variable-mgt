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

import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.commons.constant.CommonConstant;
import com.wiseco.var.process.app.server.enums.PermissionResourceConfigCodeEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicBusinessBucketEnum;
import com.wiseco.var.process.app.server.enums.SysDynamicSpaceTypeEnum;
import com.wiseco.var.process.app.server.repository.entity.SysDynamic;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.SysDynamicService;
import com.wiseco.var.process.app.server.service.SysDynamicServiceBiz;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import com.wiseco.var.process.app.server.service.dto.input.SysDynamicSaveInputDto;
import com.wiseco.var.process.app.server.service.dto.input.VariableDynamicSaveInputDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author wangxianli
 * @since 2022/3/2
 */
@Service
@Slf4j
@RefreshScope
public class SysDynamicServiceBizImpl implements SysDynamicServiceBiz {

    @Value("${dynamic.query.latelyDays}")
    protected Integer dynamicLatelyDays;
    @Value("${dynamic.query.count}")
    protected Integer dynamicCount;
    @Autowired
    private SysDynamicService sysDynamicService;
    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    @Override
    public void saveDynamic(SysDynamicSaveInputDto inputDto) {

        SysDynamic sysDynamic = new SysDynamic();
        if (StringUtils.isNotEmpty(inputDto.getSpaceType())) {
            sysDynamic.setSpaceType(inputDto.getSpaceType());
        } else {
            sysDynamic.setSpaceType(SysDynamicSpaceTypeEnum.DOMAIN.getCode());
        }
        sysDynamic.setSpaceBusinessId(inputDto.getDomainId());
        sysDynamic.setOperateType(inputDto.getOperateType());

        SysDynamicBusinessBucketEnum typeEnum = inputDto.getTypeEnum();

        sysDynamic.setBusinessType(typeEnum.getType().getCode());
        sysDynamic.setBusinessBucket(typeEnum.getCode());

        sysDynamic.setBusinessId(String.valueOf(inputDto.getBusinessId()));

        sysDynamic.setPermissionResourcesId(inputDto.getPermissionResourcesId());
        sysDynamic.setPermissionResourcesCode(typeEnum.getPermission().getCode());

        StringBuilder businessContent = new StringBuilder();

        //场景1：动作  + 类型 + ： + 名称 添加领域：助贷业务ZDYW01
        businessContent.append(inputDto.getOperateType()).append(typeEnum.getDesc());
        if (StringUtils.isNotEmpty(inputDto.getBusinessDesc())) {
            businessContent.append("：").append(inputDto.getBusinessDesc());
        }
        sysDynamic.setBusinessContent(businessContent.toString());

        String userName;
        if (!StringUtils.isEmpty(inputDto.getUserName())) {
            userName = inputDto.getUserName();
        } else {
            try {
                userName = SessionContext.getSessionUser().getUsername();
            } catch (Exception e) {
                userName = CommonConstant.SYSTEM;
            }
        }

        sysDynamic.setCreatedUser(userName);
        if (inputDto.getDynamicStatus() != null) {
            sysDynamic.setStatus(inputDto.getDynamicStatus());
        }

        sysDynamicService.save(sysDynamic);
    }

    /**
     * 保存变量空间动态
     * @param inputDto 输入实体类对象
     */
    @Override
    public void saveDynamicVariable(VariableDynamicSaveInputDto inputDto) {

        SysDynamic sysDynamic = new SysDynamic();
        sysDynamic.setSpaceType(SysDynamicSpaceTypeEnum.VARIABLE.getCode());
        sysDynamic.setSpaceBusinessId(inputDto.getVarSpaceId());
        sysDynamic.setOperateType(inputDto.getOperateType());

        SysDynamicBusinessBucketEnum typeEnum = inputDto.getTypeEnum();

        sysDynamic.setBusinessType(typeEnum.getType().getCode());
        sysDynamic.setBusinessBucket(typeEnum.getCode());
        sysDynamic.setBusinessId(String.valueOf(inputDto.getBusinessId()));

        sysDynamic.setPermissionResourcesId(inputDto.getVarSpaceId());
        sysDynamic.setPermissionResourcesCode(typeEnum.getPermission().getCode());

        //场景：在[变量空间]下动作  + 类型 + ： + 名称  例：在[人行征信]下添加变量：人行职业

        if (typeEnum.getType().equals(PermissionResourceConfigCodeEnum.VARIABLE_MAIN)) {
            StringBuilder businessContent = new StringBuilder();
            businessContent.append(inputDto.getOperateType()).append(typeEnum.getDesc());
            if (!StringUtils.isEmpty(inputDto.getBusinessDesc())) {
                businessContent.append("：").append(inputDto.getBusinessDesc());
            }
            sysDynamic.setBusinessContent(businessContent.toString());
        } else {
            VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputDto.getVarSpaceId());
            sysDynamic.setSpaceName(varProcessSpace.getName());

            if (!StringUtils.isEmpty(inputDto.getBusinessDesc())) {
                sysDynamic.setBusinessContent(inputDto.getBusinessDesc());
            }
        }

        String userName;
        if (!StringUtils.isEmpty(inputDto.getUserName())) {
            userName = inputDto.getUserName();
        } else {
            try {
                userName = SessionContext.getSessionUser().getUsername();
            } catch (Exception e) {
                userName = CommonConstant.SYSTEM;
            }
        }

        sysDynamic.setCreatedUser(userName);

        sysDynamicService.save(sysDynamic);

        if (userName.equals(CommonConstant.SYSTEM)) {
            return;
        }
        //更新变量空间信息:用户和编辑时间
        varProcessSpaceService.updateSpaceUserNameById(userName, inputDto.getVarSpaceId());

    }

}
