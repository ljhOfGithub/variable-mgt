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
package com.wiseco.var.process.app.server.controller.model;

import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.outside.service.rpc.dto.output.OutsideServiceListRestOutputDto;
import com.wiseco.var.process.app.server.annotation.LoggableClass;
import com.wiseco.var.process.app.server.annotation.LoggableDynamicValue;
import com.wiseco.var.process.app.server.annotation.LoggableMethod;
import com.wiseco.var.process.app.server.controller.vo.input.DataModelDomainModelTreeExtendDataInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelAddNewInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelAddNewNextInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelAddSqlReturnVarCheckInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelCopyInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelDeletInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelOperateCheckInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelUpdateInputVo;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelVersionInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModeViewOutputVo;
import com.wiseco.var.process.app.server.enums.LoggableMethodTypeEnum;
import com.wiseco.var.process.app.server.service.datamodel.DataModelPreviewBiz;
import com.wiseco.var.process.app.server.service.datamodel.DataModelSaveBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 变量加工数据模型 控制器
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/13
 */
@RestController
@RequestMapping("/variableDataModel")
@Slf4j
@Tag(name = "数据模型保存")
@LoggableClass(param = "variableDataModel")
public class DataModelSaveController {

    @Resource
    private DataModelSaveBiz dataModelSaveBiz;
    @Resource
    private DataModelPreviewBiz dataModelPreviewBiz;

    /**
     * 添加数据模型
     *
     * @param inputDto 输入实体类对象
     * @return 添加数据模型的结果
     */
    @PostMapping("/addDataModel")
    @LoggableMethod(value = "添加数据模型对象[%s]", params = {"objectName"}, type = LoggableMethodTypeEnum.CREATE)
    @Operation(summary = "添加数据模型")
    public APIResult<Long> addDataModel(@Validated @RequestBody VariableDataModelAddNewInputVo inputDto) {
        return APIResult.success(dataModelSaveBiz.addDataModel(inputDto));
    }

    /**
     * 数据模型添加-保存并下一步
     *
     * @param inputDto 输入
     * @return VariableDataModeViewOutputVo
     */
    @PostMapping("/dataPreview")
    @Operation(summary = "数据预览")
    @LoggableMethod(value = "添加数据模型对象[%s]", params = {"objectName"}, type = LoggableMethodTypeEnum.CREATE)
    public APIResult<VariableDataModeViewOutputVo> dataPreview(@RequestBody VariableDataModelAddNewNextInputVo inputDto) {
        return APIResult.success(dataModelPreviewBiz.savaAndNextPreview(inputDto));
    }

    /**
     * 外部数据服务下拉列表
     *
     * @return com.wiseco.boot.commons.web.APIResult
     */
    @GetMapping("/getOutsideList")
    @Operation(summary = "外部数据服务下拉列表")
    public APIResult<List<OutsideServiceListRestOutputDto>> getOutsideList() {
        return APIResult.success(dataModelSaveBiz.getOutsideList());
    }

    /**
     * 外部数据服务对象名称
     *
     * @param id id
     * @return Map
     */
    @GetMapping("/getOutsideDetailRest/{id}")
    @Operation(summary = "外部数据服务对象名称")
    public APIResult<Map<String, String>> getOutsideDetailRest(@PathVariable("id") Long id) {
        return APIResult.success(dataModelSaveBiz.getOutsideDetailRest(id));
    }

    /**
     * 数据模型添加
     *
     * @param inputVo 输入实体类对象
     * @return sql取数中的识别数据
     */
    @PostMapping("/sqlReturnVar")
    @Operation(summary = "sql取数中的识别数据")
    public APIResult<List<VariableDataModelAddNewNextInputVo.InsideSqlOutputVO>> sqlReturnVar(@RequestBody VariableDataModelAddSqlReturnVarCheckInputVo inputVo) {
        return APIResult.success(dataModelSaveBiz.getSqlReturnVar(inputVo));
    }

    /**
     * 数据模型编辑前的校验
     *
     * @param inputVo 数据模型操作校验输入参数
     * @return 是否可以编辑的String，message为null进入编辑，不为空根据message进行提示
     */
    @PostMapping("/dataModelEditCheck")
    @Operation(summary = "数据模型编辑前的校验")
    public APIResult dataModelEditCheck(@Validated @RequestBody VariableDataModelOperateCheckInputVo inputVo) {
        return APIResult.success(dataModelSaveBiz.dataModelEditCheck(inputVo));
    }

    /**
     * 王先圣定义  数据模型删除前的校验
     *
     * @param  inputVo 数据模型操作校验输入参数
     * @return 通过返回message判断是否可以删除
     */
    @PostMapping("/dataModelDeleteCheck")
    @Operation(summary = "数据模型删除前的校验")
    public APIResult dataModelDeleteCheck(@Validated @RequestBody VariableDataModelOperateCheckInputVo inputVo) {
        return APIResult.success(dataModelSaveBiz.dataModelDeleteCheck(inputVo));
    }


    /**
     * 升级数据模型版本
     *
     * @param inputVo 输入实体类对象
     * @return 数据模型Id
     */
    @PostMapping("/upDataModelVersion")
    @Operation(summary = "升级数据模型版本")
    @LoggableDynamicValue(params = {"var_process_data_model","objectName"})
    @LoggableMethod(value = "数据模型对象添加新版本[%s-%s]",params = {"objectName"}, type = LoggableMethodTypeEnum.NEW_VERSION)
    public APIResult<Long> upDataModelVersion(@Validated @RequestBody VariableDataModelVersionInputVo inputVo) {
        return APIResult.success(dataModelSaveBiz.upDataModelVersion(inputVo));
    }

    /**
     * 更新数据模型
     *
     * @param inputVo 输入实体类对象
     * @return 更新数据模型的结果
     */
    @PostMapping("/updateDataModel")
    @Operation(summary = "更新数据模型")
    @LoggableMethod(value = "编辑数据模型对象[%s]", params = "objectName", type = LoggableMethodTypeEnum.EDIT)
    public APIResult updateDataModel(@RequestBody VariableDataModelUpdateInputVo inputVo) {
        return APIResult.success(dataModelSaveBiz.updateDataModel(inputVo));
    }

    /**
     * 复制数据模型
     *
     * @param inputVo 输入实体类对象
     * @return 数据模型Id
     */
    @PostMapping("/copyDataModel")
    @Operation(summary = "复制数据模型")
    @LoggableDynamicValue(params = {"var_process_data_model","dataModelId"})
    @LoggableMethod(value = "复制数据模型对象[%s]为[%s]",params = {"dataModelId","objectName"}, type = LoggableMethodTypeEnum.COPY)
    public APIResult<Long> copyDataModel(@Validated @RequestBody VariableDataModelCopyInputVo inputVo) {
        return APIResult.success(dataModelSaveBiz.copyDataModel(inputVo));
    }

    /**
     * 删除数据模型
     *
     * @param inputVo 输入实体类对象
     * @return 删除数据模型的结果
     */
    @PostMapping("/deleteDataModel")
    @Operation(summary = "删除数据模型")
    @LoggableMethod(value = "删除数据模型对象[%s]", params = "objectName", type = LoggableMethodTypeEnum.DELETE)
    public APIResult deleteDataModel(@Validated @RequestBody VariableDataModelDeletInputVo inputVo) {
        dataModelSaveBiz.deleteDataModel(inputVo);
        return APIResult.success();
    }

    /**
     * 将另一个数据模型树状结构中的扩展数据添加到当前数据模型树中
     * @param inputVo 两个数据模型树
     * @return 新的数据模型树
     */
    @PostMapping  ("/addDomainModelTreeExtendData")
    @Operation(summary = "测试数据模型保存下一步挂载原先的扩展数据")
    public APIResult<DomainDataModelTreeDto> addDomainModelTreeExtendData(@RequestBody DataModelDomainModelTreeExtendDataInputVo inputVo) {

        return APIResult.success(dataModelPreviewBiz.addDomainModelTreeExtendData(inputVo.getTree1(),inputVo.getTree2()));
    }

}
