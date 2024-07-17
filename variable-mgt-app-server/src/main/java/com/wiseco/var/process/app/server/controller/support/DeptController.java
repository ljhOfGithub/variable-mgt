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

import com.wiseco.auth.common.DepartmentCriteria;
import com.wiseco.auth.common.DepartmentDTO;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.service.common.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 获取底座部门列表
 *
 * @since 20231012
 * @author chimeng
 */
@Tag(name = "获取底座部门列表")
@RestController
@RequestMapping("/department")
public class DeptController {

    @Resource
    private DeptService deptService;

    /**
     * 查询部门树
     * @return 部门树
     */
    @Operation(summary = "获取底座部门树状列表")
    @GetMapping("/tree")
    public APIResult<List<DepartmentDTO>> queryDepartmentsWithTree() {
        return APIResult.success(deptService.findDepartmentList(new DepartmentCriteria()));
    }
}
