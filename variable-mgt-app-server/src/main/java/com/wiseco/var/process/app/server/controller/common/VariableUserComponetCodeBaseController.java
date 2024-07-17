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
package com.wiseco.var.process.app.server.controller.common;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.commons.enums.CodeBaseResourceTypeEnum;
import com.wiseco.var.process.app.server.service.UserComponetCodeBaseBiz;
import com.wiseco.var.process.app.server.service.dto.input.UserCodeBaseSearchPageInputDto;
import com.wiseco.var.process.app.server.service.dto.input.UserComponentCodeBaseUpdateInputDto;
import com.wiseco.var.process.app.server.service.dto.input.UserComponetCodeBaseInputDto;
import com.wiseco.var.process.app.server.service.dto.output.UserCodeBaseOutputDto;
import com.wiseco.var.process.app.server.service.dto.output.UserCompnentCodeBasePageOutputDto;
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
 * 用户代码库设置服务
 *
 * @author kangyk
 * @since 2022/08/30
 */

@RestController
@RequestMapping("/variable/userComponetCodeBase")
@Slf4j
@Tag(name = "用户代码库设置服务")
@Validated
public class VariableUserComponetCodeBaseController {

    @Autowired
    private UserComponetCodeBaseBiz userComponetCodeBaseBiz;

    /**
     * list
     *
     * @return 用户代码库列表
     */
    @PostMapping("/list")
    @Operation(summary = "用户代码库列表")
    public APIResult<List<UserCodeBaseOutputDto>> list() {
        return APIResult.success(userComponetCodeBaseBiz.list());
    }

    /**
     * pageList
     *
     * @param inputDto 输入实体类对象
     * @return 用户代码库列表
     */
    @PostMapping("/pageList")
    @Operation(summary = "用户代码库列表")
    public APIResult<UserCompnentCodeBasePageOutputDto> pageList(@RequestBody UserCodeBaseSearchPageInputDto inputDto) {
        return APIResult.success(userComponetCodeBaseBiz.pageList(inputDto, CodeBaseResourceTypeEnum.VARIABLE));
    }

    /**
     * save
     *
     * @param inputDto 输入实体类对象
     * @return 保存用户代码块后的结果
     */
    @PostMapping("/save")
    @Operation(summary = "保存用户代码块")
    public APIResult<Long> save(@RequestBody UserComponetCodeBaseInputDto inputDto) {
        return APIResult.success(userComponetCodeBaseBiz.save(inputDto, CodeBaseResourceTypeEnum.VARIABLE));
    }

    /**
     * updateUseTimes
     *
     * @param inputDto 输入实体类对象
     * @return 更新用户使用次数后的结果
     */
    @PostMapping("/updateUseTimes")
    @Operation(summary = "更新用户使用次数")
    public APIResult<Boolean> updateUseTimes(@RequestBody UserComponentCodeBaseUpdateInputDto inputDto) {
        return APIResult.success(userComponetCodeBaseBiz.updateUseTimes(inputDto));
    }

    /**
     * delete
     *
     * @param inputDto 输入实体类对象
     * @return 删除用户代码库后的结果
     */
    @PostMapping("/delete")
    @Operation(summary = "删除用户代码库")
    public APIResult<Boolean> delete(@RequestBody UserComponentCodeBaseUpdateInputDto inputDto) {
        return APIResult.success(userComponetCodeBaseBiz.delete(inputDto));
    }

    /**
     * batchDelete
     *
     * @param inputDto 输入实体类对象
     * @return 删除用户代码库后的结果
     */
    @PostMapping("/batch/delete")
    @Operation(summary = "删除用户代码库")
    public APIResult<Boolean> batchDelete(@RequestBody UserComponentCodeBaseUpdateInputDto inputDto) {
        return APIResult.success(userComponetCodeBaseBiz.batchDelete(inputDto));
    }

}
