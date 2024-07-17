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
package com.wiseco.var.process.app.server.service.common;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.wiseco.auth.common.RoleDataAuthorityDTO;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.user.PermissionClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.wiseco.var.process.app.server.commons.constant.CommonConstant.ALL_PERMISSION;

/**
 * @author wuweikang
 */
@Service
@Slf4j
public class AuthService {

    @Autowired
    private PermissionClient permissionClient;

    @Value(value = "${spring.application.name:variable_mgt_code}")
    private String appCode;

    /**
     * 获取用户数据权限
     *
     * @return 用户数据权限
     */
    public RoleDataAuthorityDTO getRoleDataAuthority() {
        return getRoleDataAuthority(SessionContext.getSessionUser().getUsername());
    }

    /**
     * 获取用户数据权限
     *
     * @param userName 用户名
     * @return 用户数据权限
     */
    public RoleDataAuthorityDTO getRoleDataAuthority(String userName) {
        RoleDataAuthorityDTO roleDataAuthority = permissionClient.getRoleDataAuthority(appCode, userName);
        //当部门code不为空的情况下，只以部门作为筛选条件
        if (!CollectionUtils.isEmpty(roleDataAuthority.getDeptCodes())) {
            roleDataAuthority.setUserNames(null);
        }
        log.info("用户名：{}，用户数据权限信息：type->{},deptCodes->{},userNames->{}", userName, roleDataAuthority.getType(), roleDataAuthority.getDeptCodes(), roleDataAuthority.getUserNames());
        return roleDataAuthority;
    }

    /**
     * 获取全部权限
     * @return RoleDataAuthorityDTO
     */
    public RoleDataAuthorityDTO getAllAuthority() {
        RoleDataAuthorityDTO roleDataAuthorityDTO = new RoleDataAuthorityDTO();
        roleDataAuthorityDTO.setType(ALL_PERMISSION);
        return roleDataAuthorityDTO;
    }
}
