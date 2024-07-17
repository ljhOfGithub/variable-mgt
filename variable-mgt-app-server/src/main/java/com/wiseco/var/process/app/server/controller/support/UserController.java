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
package com.wiseco.var.process.app.server.controller.support;

import com.wiseco.auth.common.DepartmentSmallDTO;
import com.wiseco.auth.common.UserSmallDTO;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.boot.user.DepartmentClient;
import com.wiseco.boot.user.UserClient;
import com.wiseco.var.process.app.server.controller.vo.UserListQueryInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.UserListQueryOutputVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取底座部门列表
 *
 * @author chimeng
 * @since 20231012
 */
@Tag(name = "用户共用接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserClient userClient;

    @Resource
    private DepartmentClient departmentClient;

    /**
     * 获取用户列表
     *
     * @param userListQueryInputVo userListQueryInputVo
     * @return 用户列表
     */
    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public APIResult<List<UserListQueryOutputVo>> list(UserListQueryInputVo userListQueryInputVo) {
        List<UserSmallDTO> users;
        //获取用户列表
        if (userListQueryInputVo.getDeptId() != null) {
            users = userClient.getUsersByDeptId(userListQueryInputVo.getDeptId());
        } else {
            users = userClient.getUsers();
        }


        List<UserListQueryOutputVo> result = users.stream().map(item -> {
            UserListQueryOutputVo outputVo = UserListQueryOutputVo.builder().userId(item.getId()).username(item.getUsername()).fullName(item.getFullname()).build();
            DepartmentSmallDTO smallDepartment = departmentClient.findSmallDepartmentByUserName(item.getUsername());
            if (smallDepartment != null) {
                outputVo.setDeptName(smallDepartment.getName());
            }
            return outputVo;
        }).collect(Collectors.toList());


        //根据姓名过滤
        String name = userListQueryInputVo.getName();
        if (name != null) {
            result = result.stream().filter(item -> item.getFullName().contains(name) || item.getUsername().contains(name)).collect(Collectors.toList());
        }
        return APIResult.success(result);
    }
}
