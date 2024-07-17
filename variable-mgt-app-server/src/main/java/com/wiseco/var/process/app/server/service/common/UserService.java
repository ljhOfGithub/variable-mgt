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

import com.google.common.collect.Lists;
import com.wiseco.auth.common.UserSmallDTO;
import com.wiseco.boot.user.UserClient;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务
 * @author wuweikang
 */
@Service
public class UserService {

    @Autowired
    private UserClient userClient;

    /**
     * 每10条 循环调用底座服务
     *
     * @param userNames 用户英文名称集合
     * @return 用户英文名 ： 用户中文名
     */
    public Map<String, String> findFullNameMapByUserNames(List<String> userNames) {
        if (CollectionUtils.isEmpty(userNames)) {
            return new HashMap<>(MagicNumbers.EIGHT);
        }
        //去重
        List<String> distinctUserNames = userNames.stream().distinct().collect(Collectors.toList());
        Map<String, String> fullNameMap = new HashMap<>(distinctUserNames.size());
        List<List<String>> userNameListGroup = Lists.partition(distinctUserNames, MagicNumbers.TEN);

        userNameListGroup.forEach(item -> {
            fullNameMap.putAll(userClient.getNameMapByUserNames(item));
        });

        return fullNameMap;
    }

    /**
     * 根据用户英文名获取中文名称
     *
     * @param userName 用户英文名称集合
     * @return 用户中文名
     */
    public String getFullNameByUserName(String userName) {
        String fullName = "";
        if (StringUtils.isEmpty(userName)) {
            return fullName;
        }

        UserSmallDTO user = userClient.getUser(userName);
        if (user != null) {
            fullName = user.getFullname();
        }
        return fullName;
    }

    /**
     * 根据部门id查找部门下的用户
     *
     * @param deptId 部门id
     * @return 用户集合
     */
    public List<UserSmallDTO> findUserSmallByDeptId(Integer deptId) {
        if (deptId == null) {
            return new ArrayList<>();
        }

        return userClient.getUsersByDeptId(deptId);
    }


    /**
     * 根据用户id集合查找部门下的用户
     *
     * @param userIds 用户id集合
     * @return 用户集合
     */
    public List<UserSmallDTO> findUserSmallByUserIds(List<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return userClient.getUsers(userIds);
    }
}
