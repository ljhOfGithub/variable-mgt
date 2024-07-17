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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.TagDeleteCheckVo;
import com.wiseco.var.process.app.server.controller.vo.input.TagGroupMoveInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessConfigTagGroupSaveInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessConfigTagInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessConfigTagQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VarProcessConfigTagSaveInputDto;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.VarProcessConfigTagBiz;
import com.wiseco.var.process.app.server.service.dto.VarProcessConfigTagGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 变量标签配置
 *
 * @author wangxianli
 * @since 2022/09/30
 */

@RestController
@RequestMapping("/varProcessConfigTag")
@Slf4j
@Tag(name = "变量标签配置")
@Validated
@LoggableClass(param = "varProcessConfigTag")
public class VarProcessConfigTagController {

    @Autowired
    private VarProcessConfigTagBiz varProcessConfigTagBiz;

    /**
     * getList
     *
     * @param inputDto 输入实体类对象
     * @return 标签列表
     */
    @PostMapping("/list")
    @Operation(summary = "标签列表")
    public APIResult<IPage<VarProcessConfigTagGroupDto>> getList(@Validated @RequestBody VarProcessConfigTagQueryInputDto inputDto) {
        return APIResult.success(varProcessConfigTagBiz.getList(inputDto));
    }

    /**
     * getTagTrees
     *
     * @param inputDto 输入实体类对象
     * @return 标签树
     */
    @PostMapping("/tagTrees")
    @Operation(summary = "获取标签树")
    public APIResult<List<VarProcessConfigTagGroupDto>> getTagTrees(@Validated @RequestBody VarProcessConfigTagQueryInputDto inputDto) {
        return APIResult.success(varProcessConfigTagBiz.getTagTrees(inputDto));
    }

    /**
     * saveGroup
     *
     * @param inputDto 输入实体类对象
     * @return 添加标签组
     */
    @PostMapping("/saveGroup")
    @Operation(summary = "添加标签组")
    @LoggableMethod(value = "在[标签]下添加标签组[%s]",params = "groupName", type = LoggableMethodTypeEnum.CREATE_TAG_GROUP)
    public APIResult saveGroup(@Validated @RequestBody VarProcessConfigTagGroupSaveInputDto inputDto) {
        varProcessConfigTagBiz.saveGroup(inputDto);
        return APIResult.success();
    }

    /**
     * editGroup
     *
     * @param inputDto 输入实体类对象
     * @return 修改标签组
     */
    @PostMapping("/editGroup")
    @Operation(summary = "修改标签组")
    @LoggableMethod(value = "在[标签]下编辑标签组[%s]",params = "groupName", type = LoggableMethodTypeEnum.EDIT_TAG_GROUP)
    public APIResult editGroup(@Validated @RequestBody VarProcessConfigTagGroupSaveInputDto inputDto) {
        varProcessConfigTagBiz.saveGroup(inputDto);
        return APIResult.success();
    }

    /**
     * 上移/下移标签组
     * @param categoryMoveInputVo 入参
     * @return true or false
     */
    @PostMapping("/move")
    @Operation(summary = "上移/下移标签组")
    public APIResult<Boolean> moveTagGroup(@RequestBody @Validated TagGroupMoveInputVo categoryMoveInputVo) {
        return APIResult.success(varProcessConfigTagBiz.moveTagGroup(categoryMoveInputVo.getGroupId(), categoryMoveInputVo.getOpeType()));
    }

    /**
     * deleteGroup
     *
     * @param inputDto 输入实体类对象
     * @return 删除标签组后的结果
     */
    @PostMapping("/deleteGroup")
    @Operation(summary = "删除标签组")
    @LoggableDynamicValue(params = {"var_process_config_tag_group","id"})
    @LoggableMethod(value = "在[标签]下删除标签组[%s]",params = {"id"}, type = LoggableMethodTypeEnum.DELETE_TAG_GROUP)
    public APIResult deleteGroup(@Validated @RequestBody VarProcessConfigTagInputDto inputDto) {
        varProcessConfigTagBiz.deleteGroup(inputDto);
        return APIResult.success();
    }

    /**
     * saveTag
     *
     * @param inputDto 输入实体类对象
     * @return 添加标签后的结果
     */
    @PostMapping("/saveTag")
    @Operation(summary = "添加标签")
    public APIResult saveTag(@Validated @RequestBody VarProcessConfigTagSaveInputDto inputDto) {
        varProcessConfigTagBiz.saveTag(inputDto);
        return APIResult.success();
    }

    /**
     * deleteTag
     *
     * @param inputDto 输入实体类对象
     * @return 删除标签的结果
     */
    @PostMapping("/deleteTag")
    @Operation(summary = "删除标签")
    public APIResult deleteTag(@Validated @RequestBody VarProcessConfigTagInputDto inputDto) {
        varProcessConfigTagBiz.deleteTag(inputDto);
        return APIResult.success();
    }

    /**
     * 删除标签/标签组校验接口
     * @param inputDto 输入实体类对象
     * @return 提示信息
     */
    @PostMapping("/deleteCheck")
    @Operation(summary = "删除标签/标签组校验接口")
    public APIResult<String> deleteCheck(@RequestBody TagDeleteCheckVo inputDto) {
        return APIResult.success(varProcessConfigTagBiz.deleteCheck(inputDto));
    }

}
