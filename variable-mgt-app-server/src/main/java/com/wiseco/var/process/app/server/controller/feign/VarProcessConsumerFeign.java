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
package com.wiseco.var.process.app.server.controller.feign;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.controller.feign.dto.CreateTabDto;
import com.wiseco.var.process.app.server.controller.feign.dto.DownLoadDataDto;
import com.wiseco.var.process.app.server.controller.feign.fallback.VarProcessConsumerFeignFallbackFactory;
import com.wiseco.var.process.app.server.controller.vo.input.DataViewInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableResultListQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.DataViewOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TableFieldVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableResultDatagramQueryOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableResultListQueryOutputDto;
import com.wiseco.var.process.app.server.service.dto.PagedQueryResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "variable-service-consumer",path = "/rpc/varProcess", fallbackFactory = VarProcessConsumerFeignFallbackFactory.class)
public interface VarProcessConsumerFeign {

    /**
     * 实时服务调用记录列表查询
     * @param inputDto 输入实体类对象
     * @return 调用记录列表
     */
    @PostMapping("/RestCallRecordList")
    APIResult<PagedQueryResult<VariableResultListQueryOutputDto>> findRestCallRecordList(@RequestBody VariableResultListQueryInputDto inputDto);

    /**
     * 实时服务调用记录报文查询
     * @param engineSerialNo 引擎生成流水号
     * @return 报文
     */
    @GetMapping("/RestCallRecordMessage")
    APIResult<VariableResultDatagramQueryOutputDto> getRestCallRecordMessage(@RequestParam(value = "engineSerialNo") String engineSerialNo);

    /**
     * 清单提交时动态建表-表名：var_process_manifest_清单id
     *
     * @param createTabDto 表名+字段列表
     * @return 表名
     */
    @PostMapping("/createVarTable")
    APIResult<String> createVarTable(@RequestBody CreateTabDto createTabDto);

    /**
     * 结果查询-数据查看
     * @param dataViewInputDto 输入实体类对象
     * @return 数据查询出参
     */
    @PostMapping("/dataView")
    @Operation(summary = "数据查询")
    APIResult<DataViewOutputDto> getDataView(@RequestBody DataViewInputDto dataViewInputDto);

    /**
     * 获取被调用过的实时服务列表
     *
     * @return map key：serviceId；value：serviceName
     */
    @GetMapping("/getRestServices")
    Map<String, String> getRestServices();

    /**
     * 获取字段对应的类型
     *
     * @param callList 清单id
     * @return List
     */
    @GetMapping("/filedType/{callList}")
    List<TableFieldVO> getFiledType(@PathVariable("callList") String callList);

    /**
     * 获取查询条件/显示列
     * @param manifestId 清单id
     * @param condition 1-查询条件；2-显示列
     * @return 字段名称+类型
     */
    @GetMapping("/findTabField")
    List<TableFieldVO> findTabField(@RequestParam("manifestId") Long manifestId,@RequestParam("condition") Integer condition);

    /**
     * 获取下载数据
     * @param dataViewInputDto 输入实体类对象
     * @return 下载数据
     */
    @PostMapping("/ResultView/fetchDownLoadData")
    @Operation(summary = "获取下载数据")
    DownLoadDataDto fetchDownLoadData(@RequestBody DataViewInputDto dataViewInputDto);
}
