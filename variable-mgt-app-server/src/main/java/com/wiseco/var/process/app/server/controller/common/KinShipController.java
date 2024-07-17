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
import com.wiseco.var.process.app.server.controller.vo.output.KinShipOutputVo;
import com.wiseco.var.process.app.server.enums.KinShipTypeEnum;
import com.wiseco.var.process.app.server.service.VarProcessKinShipBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangxiansheng
 */
@RestController
@RequestMapping("/kinShip")
@Slf4j
@Tag(name = "血缘关系")
public class KinShipController {

    @Resource
    private VarProcessKinShipBiz varProcessKinShipBiz;


    /**
     * 血缘关系
     *
     * @param spaceId  空间ID
     * @param varFullPath 对象名称
     * @param id id(所有的id)
     * @param type  类型
     * @return list
     */
    @GetMapping("/kinShipList")
    @Operation(summary = "血缘关系列表")
    public APIResult<List<KinShipOutputVo>> getKinShipList(@RequestParam("spaceId") @Parameter(description = "变量空间ID") Long spaceId,
                                                            @RequestParam("varFullPath") @Parameter(description = "数据模型变量路径") String varFullPath,
                                                            @RequestParam("id") @Parameter(description = "ID") Long id,
                                                            @RequestParam("type") @Parameter(description = "类型") KinShipTypeEnum type) {
        return APIResult.success(varProcessKinShipBiz.getKinShipList(spaceId,varFullPath,id,type));
    }

    /**
     * 往前追溯引用的数据模型变量
     * @param spaceId 空间id
     * @param id 变量id
     * @return list
     */
    @GetMapping("/getUsedVars")
    @Operation(summary = "往前追溯引用的数据模型变量")
    public APIResult<List<KinShipOutputVo>> getUsedVars(@RequestParam("spaceId") @Parameter(description = "空间id") Long spaceId,
                                                        @RequestParam("id") @Parameter(description = "id") Long id) {
        return APIResult.success(varProcessKinShipBiz.getUsedVars(spaceId,id));
    }

}
