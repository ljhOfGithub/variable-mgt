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

import com.wiseco.auth.common.DepartmentCriteria;
import com.wiseco.auth.common.DepartmentDTO;
import com.wiseco.auth.common.DepartmentSmallDTO;
import com.wiseco.boot.user.DepartmentClient;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门服务
 * @author wuweikang
 */
@Service
public class DeptService {

    @Autowired
    private DepartmentClient departmentClient;

    /**
     * 获取部门map key:id value:DepartmentDTO
     *
     * @param deptIds 部门id集合
     * @return deptIds 部门id集合
     */
    public Map<Integer, String> findDeptMapByDeptIds(List<Integer> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return new HashMap<>(MagicNumbers.EIGHT);
        }

        List<DepartmentSmallDTO> departmentSmallList = departmentClient.findSmallByIds(deptIds);
        return departmentSmallList.stream().collect(Collectors.toMap(DepartmentSmallDTO::getId, DepartmentSmallDTO::getName, (t1, t2) -> t2));
    }

    /**
     * 获取部门map key:code value:部门名
     *
     * @param deptCodes 部门code集合
     * @return 部门code集合
     */
    public Map<String, String> findDeptMapByDeptCodes(List<String> deptCodes) {
        if (CollectionUtils.isEmpty(deptCodes)) {
            return new HashMap<>(MagicNumbers.EIGHT);
        }

        List<DepartmentSmallDTO> departmentSmallList = departmentClient.findSmallByCodes(deptCodes);
        return departmentSmallList.stream().collect(Collectors.toMap(DepartmentSmallDTO::getCode, DepartmentSmallDTO::getName, (t1, t2) -> t2));
    }

    /**
     * 根据部门code获取部门id
     *
     * @param deptCode 部门code
     * @return 部门id
     */
    public Integer getDeptIdByDeptCode(String deptCode) {
        Integer deptId = null;
        if (StringUtils.isEmpty(deptCode)) {
            return deptId;
        }

        List<DepartmentSmallDTO> departmentSmallList = departmentClient.findSmallByCodes(Collections.singletonList(deptCode));
        if (!CollectionUtils.isEmpty(departmentSmallList)) {
            deptId = departmentSmallList.get(0).getId();
        }
        return deptId;
    }

    /**
     * 根据用户英文名获取部门名称
     *
     * @param userName 用户英文名
     * @return 部门名称
     */
    public String getDeptCodeByUserName(String userName) {
        String deptName = null;
        if (StringUtils.isEmpty(userName)) {
            return deptName;
        }

        DepartmentSmallDTO departmentSmallDTO = departmentClient.findSmallDepartmentByUserName(userName);
        if (departmentSmallDTO != null) {
            deptName = departmentSmallDTO.getName();
        }
        return deptName;
    }

    /**
     * 条件查询部门树
     *
     * @param departmentCriteria 条件
     * @return 部门树
     */
    public List<DepartmentDTO> findDepartmentList(DepartmentCriteria departmentCriteria) {
        return departmentClient.findDepartmentList(departmentCriteria);
    }
}
