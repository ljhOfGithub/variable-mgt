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
package com.wiseco.var.process.app.server.controller.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.var.process.app.server.controller.vo.SceneListSimpleOutputVO;
import com.wiseco.var.process.app.server.controller.vo.input.SceneListInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.SceneSaveInputVO;
import com.wiseco.var.process.app.server.controller.vo.output.SceneDetailOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.SceneListOutputVO;
import com.wiseco.var.process.app.server.service.VarProcessSceneBiz;
import com.wiseco.var.process.app.server.service.dto.SceneVarRoleDto;
import com.wisecoprod.starterweb.pojo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/varProcessScene")
@Slf4j
@Tag(name = "场景配置")
public class SceneController {

    @Autowired
    private VarProcessSceneBiz varProcessSceneBiz;

    /**
     * 新增场景
     *
     * @param inputDto 入参dto
     * @return 场景id
     */
    @PostMapping("/saveScene")
    @Operation(summary = "新增场景")
    public ApiResult<Long> saveScene(@Validated @RequestBody SceneSaveInputVO inputDto) {
        return ApiResult.success(varProcessSceneBiz.saveScene(inputDto));
    }

    /**
     * 编辑场景
     *
     * @param inputDto 入参dto
     * @return 场景id
     */
    @PostMapping("/editScene")
    @Operation(summary = "编辑场景")
    public ApiResult<Long> editScene(@Validated @RequestBody SceneSaveInputVO inputDto) {
        return ApiResult.success(varProcessSceneBiz.saveScene(inputDto));
    }

    /**
     * 查看详情
     *
     * @param id 场景id
     * @return 出参dto
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "查看详情")
    public ApiResult<SceneDetailOutputVO> detail(@Parameter(description = "场景id") @PathVariable("id") Long id) {
        return ApiResult.success(varProcessSceneBiz.detail(id));
    }

    /**
     * 场景列表
     *
     * @param inputDto 入参dto
     * @return page
     */
    @GetMapping("/list")
    @Operation(summary = "列表")
    public ApiResult<Page<SceneListOutputVO>> list(SceneListInputDto inputDto) {
        return ApiResult.success(varProcessSceneBiz.list(inputDto));
    }

    /**
     * 更新场景状态
     *
     * @param id         场景id
     * @param actionType 操作类型
     * @return 场景id
     */
    @GetMapping("/updateState")
    @Operation(summary = "更新状态")
    public ApiResult<Long> updateState(@Parameter(description = "场景id") @RequestParam("id") Long id, @Parameter(description = "1-启用；2-停用") @RequestParam("actionType") Integer actionType) {
        return ApiResult.success(varProcessSceneBiz.updateState(id, actionType));
    }

    /**
     * 更新状态校验
     *
     * @param id         场景id
     * @param actionType 操作类型
     * @return 场景id
     */
    @GetMapping("/updateStateCheck")
    @Operation(summary = "更新状态校验")
    public ApiResult<Long> updateStateCheck(@Parameter(description = "场景id") @RequestParam("id") Long id, @Parameter(description = "1-启用；2-停用") @RequestParam("actionType") Integer actionType) {
        return ApiResult.success(varProcessSceneBiz.updateStateCheck(id, actionType));
    }

    /**
     * 删除场景
     *
     * @param id 场景id
     * @return 删除成功
     */
    @GetMapping("/deleteScene/{id}")
    @Operation(summary = "删除")
    public ApiResult<String> deleteScene(@Parameter(description = "场景id") @PathVariable Long id) {
        return ApiResult.success(varProcessSceneBiz.deleteScene(id));
    }

    /**
     * 删除场景校验
     *
     * @param id 场景id
     * @return 确认删除？
     */
    @GetMapping("/deleteSceneCheck/{id}")
    @Operation(summary = "删除校验")
    public ApiResult<String> deleteSceneCheck(@Parameter(description = "场景id") @PathVariable Long id) {
        return ApiResult.success(varProcessSceneBiz.deleteSceneCheck(id));
    }

    /**
     * 获取MQ数据源
     *
     * @return list
     */
    @GetMapping("/findDataSources")
    @Operation(summary = "获取MQ数据源")
    public ApiResult<List<String>> findDataSources() {
        return ApiResult.success(varProcessSceneBiz.findDataSources());
    }

    /**
     * 获取变量角色枚举
     *
     * @return list
     */
    @GetMapping("/findVarRoleEnums")
    @Operation(summary = "获取变量角色枚举")
    public ApiResult<List<SceneVarRoleDto>> findVarRoleEnums() {
        return ApiResult.success(varProcessSceneBiz.findVarRoleEnums());
    }

    /**
     * 获取场景list
     * @return list
     */
    @GetMapping("/findEnabledSceneList")
    @Operation(summary = "获取启用的场景list")
    public ApiResult<List<SceneListSimpleOutputVO>> findEnabledSceneList() {
        return ApiResult.success(varProcessSceneBiz.findEnabledSceneList());
    }

    /**
     * 获取事件list
     * @param sceneId 场景id
     * @return list
     */
    @GetMapping("/findEventListOfScene")
    @Operation(summary = "根据场景id查询事件list")
    public ApiResult<List<SceneListSimpleOutputVO.EventOutputDto>> findEventListOfScene(@RequestParam("sceneId") @NotNull(message = "请传入场景id") Long sceneId) {
        return ApiResult.success(varProcessSceneBiz.findEventListOfScene(sceneId));
    }

    /**
     * 根据场景id查询匹配维度
     * @param sceneId 场景id
     * @return list
     */
    @GetMapping("/findMatchDimensionsOfScene")
    @Operation(summary = "根据场景id查询匹配维度")
    public ApiResult<List<SceneListSimpleOutputVO.MatchDimensionOutputDto>> findMatchDimensionsOfScene(@RequestParam("sceneId") @NotNull(message = "请传入场景id") Long sceneId) {
        return ApiResult.success(varProcessSceneBiz.findMatchDimensionsOfScene(sceneId));
    }
}
