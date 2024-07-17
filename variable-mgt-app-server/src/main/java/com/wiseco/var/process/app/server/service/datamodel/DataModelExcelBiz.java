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
package com.wiseco.var.process.app.server.service.datamodel;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.decision.jsonschema.util.DomainModelTreeUtils;
import com.decision.jsonschema.util.ExcelDomainModelUtils;
import com.decision.jsonschema.util.dto.DomainDataModelTreeDto;
import com.decision.jsonschema.util.enums.VariableModelHeadEnum;
import com.decision.jsonschema.util.model.DomainModelTree;
import com.decision.jsonschema.util.model.ExportExcelJsonSchemaModel;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.constant.MagicStrings;
import com.wiseco.var.process.app.server.controller.vo.input.VariableDataModelJsonImportInputVo;
import com.wiseco.var.process.app.server.controller.vo.output.VariableDataModelExcelImportOutputDto;
import com.wiseco.var.process.app.server.enums.DataTypeEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.enums.IconEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.VarProcessDataModelService;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import com.wisecotech.json.Feature;
import com.wisecotech.json.JSONArray;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DataModelExcelBiz {
    @Resource
    private VarProcessSpaceService varProcessSpaceService;
    @Resource
    private VarProcessDataModelService varProcessDataModelService;
    public static final String REGEX_DATETIME = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

    public static final String REGEX_DATE = "\\d{4}-\\d{2}-\\d{2}";

    /**
     * 导入数据模型Excel
     * @param spaceId 变量空间Id
     * @param dataModelId 数据模型Id
     * @param file 文件对象
     * @return 数据模型Excel导入输出参数
     */
    public VariableDataModelExcelImportOutputDto importDataModelExcel(Long spaceId, Long dataModelId, MultipartFile file) {

        // 查询变量空间
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(spaceId);
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间信息！");
        }
        VarProcessDataModel dataModel = varProcessDataModelService.getOne(Wrappers.<VarProcessDataModel>lambdaQuery()
                .select(VarProcessDataModel::getVarProcessSpaceId, VarProcessDataModel::getObjectSourceType, VarProcessDataModel::getObjectName, VarProcessDataModel::getObjectLabel)
                .eq(VarProcessDataModel::getId, dataModelId));
        if (null == dataModel) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }
        if (!dataModel.getVarProcessSpaceId().equals(spaceId)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }

        // Excel 文件校验
        String fileName = file.getOriginalFilename();
        List<String> sheetNameList = null;
        try {
            sheetNameList = ExcelDomainModelUtils.readExcelSheetName(file.getInputStream(), fileName);
        } catch (IOException e) {
            log.error("读取数据模型excel的sheetName异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "读取数据模型excel的sheetName异常！", e);
        }
        if (CollectionUtils.isEmpty(sheetNameList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_DATA_ERROR, "excel中的sheetName为空！");
        }

        return importDataModelExcelHelper(file, sheetNameList, dataModel);

    }


    /**
     * 导入数据模型和引擎变量 Excel 辅助方法
     * <p>功能:</p>
     * <ul>
     * <li>校验表头</li>
     * <li>转换 Excel 内容为数据模型树形结构 JSON Schema</li>
     * <li>组装出参 DTO</li>
     * </ul>
     *
     * @param file          Excel 文件
     * @param sheetNameList Excel 页名列表
     * @param dataModel 数据模型
     * @return 数据模型 Excel 导入 出参 DTO
     */
    public VariableDataModelExcelImportOutputDto importDataModelExcelHelper(MultipartFile file, List<String> sheetNameList, VarProcessDataModel dataModel) {
        // 文件原始名
        String fileName = file.getOriginalFilename();
        // 导入失败原因列表
        List<Map<String, String>> failReason = new ArrayList<>();
        // 数据模型树形结构 DTO 列表
        List<DomainDataModelTreeDto> domainDataModelTreeOutputDtoList = new ArrayList<>();
        for (String sheetName : sheetNameList) {
            // 遍历工作页, 执行表头校验
            // 读取表头
            List<String> excelHeadList = null;
            try {
                excelHeadList = ExcelDomainModelUtils.readExcelHead(file.getInputStream(), sheetName, fileName);
            } catch (IOException e) {
                log.error("读取决策数据模型excel的表头异常：", e);
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "读取决策数据模型excel的表头异常！", e);
            }
            if (CollectionUtils.isEmpty(excelHeadList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_DATA_ERROR, "sheet页：" + sheetName + "的表头不能为空！");
            }
            // 表头校验
            List<Map<String, String>> currentSheetFailReason = checkExcelHead(excelHeadList);
            if (!currentSheetFailReason.isEmpty()) {
                // 当前页存在导入失败原因: 添加信息到列表, 不读取数据
                failReason.addAll(currentSheetFailReason);
                continue;
            }
            // 读取数据
            List<Map<String, Object>> mapList = null;
            try {
                mapList = ExcelDomainModelUtils.readBySheetNameExcelData(file.getInputStream(), sheetName, fileName);
            } catch (IOException e) {
                log.error("读取决策数据模型excel的数据异常：", e);
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "读取决策数据模型excel的数据异常！", e);
            }
            if (CollectionUtils.isEmpty(mapList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_DATA_ERROR, "sheet页：" + sheetName + "的数据不能为空！");
            }
            sheetName = (String) mapList.get(0).get("根对象");
            // 转换 Excel 内容为数据模型树形结构实体
            DomainModelTree domainModelTree = null;
            try {
                domainModelTree = DomainModelTreeUtils.listConvertDomainModelTree(mapList, sheetName);
            } catch (com.wiseco.decision.common.exception.ServiceException originalException) {
                String originalMessage = originalException.getMessage();
                String substringToRemove = "Internal Server Error:";
                String newString = originalMessage.replace(substringToRemove, "");
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, newString);
            }
            DomainDataModelTreeDto domainDataModelTreeDto = DomainDataModelTreeDto.builder().build();
            domainModelTreeConvertToDomainDataModelTreeDto(domainModelTree, domainDataModelTreeDto);
            //根据数据模型来源类型对数据结构的是否为数组，是否为扩展数据进行设置
            //如果数据模型来源类型为内部逻辑运算，根对象的是否为数组设为0，是否为扩展数据设为1，子节点的是否为扩展数据设为1
            //如果数据模型来源类型为外部传入，根对象的是否为数组设为0，是否为扩展数据设为0，子节点的是否为扩展数据设为0
            if (dataModel.getObjectSourceType().equals(VarProcessDataModelSourceType.INSIDE_LOGIC)) {
                //设置根节点
                domainDataModelTreeDto.setIsArr("0");
                domainDataModelTreeDto.setIsExtend("1");
                //设置子节点
                if (!CollectionUtils.isEmpty(domainDataModelTreeDto.getChildren())) {
                    domainDataModelTreeDtoChildrenSetIsExtend(domainDataModelTreeDto, "1");
                }
            } else {
                domainDataModelTreeDto.setIsArr("0");
                domainDataModelTreeDto.setIsExtend("0");
            }
            if (!domainDataModelTreeDto.getName().equals(dataModel.getObjectName())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_DATA_ERROR, "根对象" + dataModel.getObjectName() + "名称不匹配。");
            } else if (!domainDataModelTreeDto.getDescribe().equals(dataModel.getObjectLabel())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_DATA_ERROR, "根对象" + dataModel.getObjectLabel() + "中文描述不匹配。");
            } else if (!("0".equals(domainDataModelTreeDto.getIsArr()))) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_DATA_ERROR, "根对象" + domainDataModelTreeDto.getIsArr() + "是否数组不匹配。");
            } else {
                log.info("根对象校验通过");
            }
            domainDataModelTreeOutputDtoList.add(domainDataModelTreeDto);
        }
        return getVariableDataModelExcelImportOutputDto(failReason, domainDataModelTreeOutputDtoList);
    }


    /**
     * exportDataModelExcel
     *
     * @param spaceId 变量空间Id
     * @param dataModelId 数据模型Id
     * @param response HttpServletResponse对象
     */
    public void exportDataModelExcel(Long spaceId, Long dataModelId, HttpServletResponse response) {

        // 查询变量空间
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(spaceId);
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间信息！");
        }
        VarProcessDataModel dataModel = varProcessDataModelService.getById(dataModelId);
        if (null == dataModel) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }
        if (!dataModel.getVarProcessSpaceId().equals(spaceId)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }

        DomainModelTree domainModelTree = ExcelDomainModelUtils.jsonSchemaConvertTree(dataModel.getContent());
        List<ExportExcelJsonSchemaModel> exportExcelJsonSchemaModelList = new ArrayList<>();

        ExportExcelJsonSchemaModel inputParams = ExcelDomainModelUtils.setExportExcelVariableModel(domainModelTree, "Sheet1");

        exportExcelJsonSchemaModelList.add(inputParams);
        //excel文件名
        String fileName = dataModel.getObjectLabel() + ".xls";
        try {
            //导出excel数据
            HSSFWorkbook hssfWorkbook = ExcelDomainModelUtils.getExportExcelHssfWorkbook(exportExcelJsonSchemaModelList);
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            fileName = java.net.URLDecoder.decode(fileName, "UTF-8");

            fileName = new String(fileName.getBytes("ISO8859-1"), StandardCharsets.ISO_8859_1);
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLDecoder.decode(fileName, "UTF-8"));
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream outputStream = response.getOutputStream();
            hssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error("导出数据模型Excel文件异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_WRITE_ERROR, "导出数据模型Excel文件异常！", e);
        }
    }

    private VariableDataModelExcelImportOutputDto getVariableDataModelExcelImportOutputDto(List<Map<String, String>> failReason, List<DomainDataModelTreeDto> domainDataModelTreeOutputDtoList) {
        // 组装出参 DTO
        VariableDataModelExcelImportOutputDto outputDto = new VariableDataModelExcelImportOutputDto();
        VariableDataModelExcelImportOutputDto.ExcelImportFeedback importFeedback = new VariableDataModelExcelImportOutputDto.ExcelImportFeedback();
        if (!failReason.isEmpty()) {
            // 表头存在异常信息
            // 定义导入结果失败信息
            Map<String, String> resultMap = new HashMap<>(MagicNumbers.TWO);
            resultMap.put(IconEnum.ERROR.getKey(), IconEnum.ERROR.getCode());
            resultMap.put("text", "表头错误");
            importFeedback.setResult(resultMap);
            importFeedback.setFailReason(failReason);
            // 不添加数据模型树形结构列表
            outputDto.setDataModelTreeList(null);
        } else {
            // 表头异常信息为空
            // 定义导入结果成功信息
            Map<String, String> resultMap = new HashMap<>(MagicNumbers.TWO);
            resultMap.put(IconEnum.SUCCESS.getKey(), IconEnum.SUCCESS.getCode());
            resultMap.put("text", "数据模型导入成功");
            importFeedback.setResult(resultMap);
            importFeedback.setFailReason(null);
            // 添加数据模型树形结构列表
            outputDto.setDataModelTreeList(domainDataModelTreeOutputDtoList);
        }
        outputDto.setImportFeedback(importFeedback);
        return outputDto;
    }

    /**
     * 决策领域树形结构实体子集和是否扩展
     *
     * @param domainDataModelTreeDto 决策领域树形结构实体
     * @param isExtend 是否扩展
     * @return 决策领域树形结构实体子集和是否扩展的结果
     */
    public DomainDataModelTreeDto domainDataModelTreeDtoChildrenSetIsExtend(DomainDataModelTreeDto domainDataModelTreeDto, String isExtend) {
        for (DomainDataModelTreeDto domainDataModelTreeDto1 : domainDataModelTreeDto.getChildren()) {
            domainDataModelTreeDto1.setIsExtend(isExtend);
            if (!CollectionUtils.isEmpty(domainDataModelTreeDto1.getChildren())) {
                domainDataModelTreeDtoChildrenSetIsExtend(domainDataModelTreeDto1, isExtend);

            }
        }
        return domainDataModelTreeDto;
    }

    /**
     * 导入数据模型 Excel 表头校验
     * <p>仅限于输入, 输出, 引擎变量</p>
     *
     * @param excelHeadList Excel 表头元素列表
     * @return 错误详情描述 Map 列表
     * <p>key: icon/text, value: IconEnum.getCode/错误详情信息</p>
     */
    public List<Map<String, String>> checkExcelHead(List<String> excelHeadList) {
        //模型数据表头顺序格式验证
        List<String> sonObjectHeadList = new ArrayList<>();
        if (excelHeadList.size() > 1) {
            for (int i = 1; i < excelHeadList.size(); i++) {
                String headName = excelHeadList.get(i);
                if (null == VariableModelHeadEnum.getMessageEnum(headName)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_DATA_ERROR, headName + "表头不存在！");
                }

                if (VariableModelHeadEnum.SON_OBJECT_HEAD.getMessage().equals(headName)) {
                    sonObjectHeadList.add(excelHeadList.get(i));
                } else {
                    break;
                }
            }
        }
        //所有子对象，可以没有子对象，所以这里需要判断一下是否存在子对象
        StringBuilder sonObjectHeadBuilder = null;
        if (!CollectionUtils.isEmpty(sonObjectHeadList)) {
            sonObjectHeadBuilder = new StringBuilder();
            for (String sonObjectHead : sonObjectHeadList) {
                sonObjectHeadBuilder.append(sonObjectHead);
            }
        }
        //枚举头部变量
        StringBuilder domainModeHeadEnumBuilder = new StringBuilder();
        List<String> standardHeader = new ArrayList<>();
        for (VariableModelHeadEnum variableModelHeadEnum : VariableModelHeadEnum.values()) {
            if (VariableModelHeadEnum.SON_OBJECT_HEAD.getMessage().equals(variableModelHeadEnum.getMessage())) {
                //如果表头存在子对象才会追加子对象
                if (null != sonObjectHeadBuilder) {
                    domainModeHeadEnumBuilder.append(sonObjectHeadBuilder);
                    standardHeader.addAll(sonObjectHeadList);
                }
            } else {
                // 其他类型表头 (只出现1次)
                domainModeHeadEnumBuilder.append(variableModelHeadEnum.getMessage());
                standardHeader.add(variableModelHeadEnum.getMessage());
            }
        }
        //excel中的头部变量
        StringBuilder excelHeadBuilder = new StringBuilder();
        List<String> excelHeader = new ArrayList<>();
        for (String excelHead : excelHeadList) {
            excelHeader.add(excelHead);
            excelHeadBuilder.append(excelHead);
        }
        if (domainModeHeadEnumBuilder.toString().equals(excelHeadBuilder.toString())) {
            // Excel 标准表头内容和实际表头内容相符: 返回空列表
            return Collections.emptyList();
        }
        // 新建错误详情描述列表
        return getErrorDetails(standardHeader, excelHeader);
    }


    private List<Map<String, String>> getErrorDetails(List<String> standardHeader, List<String> excelHeader) {
        List<Map<String, String>> failReasonList = new ArrayList<>();
        // 1. 提示缺少的字段
        List<String> missedHeaderList = standardHeader.stream().filter(h -> !excelHeader.contains(h)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(missedHeaderList)) {
            Map<String, String> causeOfMissingHeader = new HashMap<>(MagicNumbers.TWO);
            causeOfMissingHeader.put(IconEnum.ERROR.getKey(), IconEnum.ERROR.getCode());
            causeOfMissingHeader.put("text", "表头缺少的字段：" + String.join("，", missedHeaderList));
            failReasonList.add(causeOfMissingHeader);

            return failReasonList;
        }
        // 2. 提示多余的字段
        List<String> extraHeaderList = excelHeader.stream().filter(h -> !standardHeader.contains(h)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(extraHeaderList)) {
            Map<String, String> causeOfRedundantHeader = new HashMap<>(MagicNumbers.TWO);
            causeOfRedundantHeader.put(IconEnum.ERROR.getKey(), IconEnum.ERROR.getCode());
            causeOfRedundantHeader.put("text", "表头多余的字段：" + String.join("，", extraHeaderList));
            failReasonList.add(causeOfRedundantHeader);

            return failReasonList;
        }
        // 3. 提示顺序错误
        for (int i = 0; i < standardHeader.size(); i++) {
            if (!standardHeader.get(i).equals(excelHeader.get(i))) {
                // 拼接表头顺序错误信息
                String causeOfHeaderSequenceMessage = String.format("第%s个列的表头应该是：%s，而不是：%s。请修改后重新导入！", i + 1, standardHeader.get(i), excelHeader.get(i));
                Map<String, String> causeOfHeaderSequence = new HashMap<>(MagicNumbers.TWO);
                causeOfHeaderSequence.put(IconEnum.ERROR.getKey(), IconEnum.ERROR.getCode());
                causeOfHeaderSequence.put("text", causeOfHeaderSequenceMessage);
                failReasonList.add(causeOfHeaderSequence);
                // 只分析第一个顺序错误的表头
                break;
            }
        }
        return failReasonList;
    }


    /**
     * 数据模型树转换成决策领域树形结构实体
     * @param domainModelTree 数据模型树
     * @param domainDataModelTreeDto 决策领域树形结构实体
     * @return 决策领域树形结构实体
     */
    public DomainDataModelTreeDto domainModelTreeConvertToDomainDataModelTreeDto(DomainModelTree domainModelTree, DomainDataModelTreeDto domainDataModelTreeDto) {
        BeanUtils.copyProperties(domainModelTree, domainDataModelTreeDto);
        if (!CollectionUtils.isEmpty(domainModelTree.getChildren())) {

            List<DomainDataModelTreeDto> domainDataModelTreeDtos = new ArrayList<>();
            for (DomainModelTree child : domainModelTree.getChildren()) {
                DomainDataModelTreeDto domainDataModelTreeDto1 = new DomainDataModelTreeDto();
                domainModelTreeConvertToDomainDataModelTreeDto(child, domainDataModelTreeDto1);
                domainDataModelTreeDtos.add(domainDataModelTreeDto1);

            }
            domainDataModelTreeDto.setChildren(domainDataModelTreeDtos);

        }
        return domainDataModelTreeDto;
    }

    /**
     * 导入数据模型Json
     *
     * @param inputVo 数据模型Json导入参数
     * @return 数据模型Json导入输出参数
     */
    public VariableDataModelExcelImportOutputDto  importDataModelJson(VariableDataModelJsonImportInputVo inputVo) {

        VariableDataModelExcelImportOutputDto
                outputDto = new VariableDataModelExcelImportOutputDto();
        // 查询变量空间
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(inputVo.getSpaceId());
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间信息！");
        }
        VarProcessDataModel dataModel = varProcessDataModelService.getOne(Wrappers.<VarProcessDataModel>lambdaQuery()
                .select(VarProcessDataModel::getVarProcessSpaceId, VarProcessDataModel::getObjectName)
                .eq(VarProcessDataModel::getId, inputVo.getDataModelId()));
        if (null == dataModel) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }
        if (!dataModel.getVarProcessSpaceId().equals(inputVo.getSpaceId())) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_NOT_FOUND, "未查询到数据模型信息！");
        }

        JSONObject jsonObject;
        try {
            // 处理json数据
            jsonObject = JSONObject.parseObject(inputVo.getJson(), Feature.OrderedField);
        } catch (Exception e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "json数据格式错误");
        }
        //判断JSON根对象数量是否为一和是否与原数据模型的根对象一致
        if (jsonObject.size() != MagicNumbers.ONE) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_ROOT_OBJ_NOT_ONLY, "Json根对象不唯一！");
        } else {
            Iterator<String> keys = jsonObject.keySet().iterator();
            if (keys.hasNext()) {
                String firstKey = keys.next();
                if (!dataModel.getObjectName().equals(firstKey)) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_ROOT_OBJ_NOT_MAT, "Json根对象与数据模型根对象不同！");
                }
            }
        }

        List<DomainDataModelTreeDto> domainDataModelTreeDtoList;
        try {
            domainDataModelTreeDtoList = parseJsonObject(jsonObject);
        } catch (Exception e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MODEL_ROOT_OBJ_NOT_ONLY, "json数据转换错误:" + e.getMessage());
        }
        domainDataModelTreeDtoList.get(MagicNumbers.ZERO).setIsRefRootNode(MagicStrings.ONE);

        VariableDataModelExcelImportOutputDto.ExcelImportFeedback importFeedback = new VariableDataModelExcelImportOutputDto.ExcelImportFeedback();
        Map<String, String> resultMap = new HashMap<>(MagicNumbers.TWO);
        resultMap.put(IconEnum.SUCCESS.getKey(), IconEnum.SUCCESS.getCode());
        resultMap.put("text", "数据模型导入成功");
        importFeedback.setResult(resultMap);
        importFeedback.setFailReason(null);
        outputDto.setImportFeedback(importFeedback);
        outputDto.setDataModelTreeList(domainDataModelTreeDtoList);

        return outputDto;
    }

    /**
     * 将JsonObject转换成树状结构
     *
     * @param jsonObject  Json对象
     * @return  树状结构
     */
    private List<DomainDataModelTreeDto> parseJsonObject(JSONObject jsonObject) {

        List<DomainDataModelTreeDto> result = new ArrayList<>();
        if (!jsonObject.isEmpty()) {
            jsonObject.forEach((key, value) -> {
                DomainDataModelTreeDto tree = new DomainDataModelTreeDto();
                tree.setName(key);
                tree.setLabel(key);
                tree.setDescribe(key);
                tree.setIsExtend(MagicStrings.ZERO);
                tree.setIsArr(value instanceof JSONArray ? MagicStrings.ONE : MagicStrings.ZERO);
                tree.setIsExtend(MagicStrings.ZERO);
                tree.setType(getParamType(value));

                if (value instanceof JSONObject) {
                    tree.setChildren(parseJsonObject((JSONObject) value));
                } else if (value instanceof JSONArray
                        && tree.getType().equals("object")) {
                    tree.setChildren(parseJsonArray((JSONArray) value));
                }
                result.add(tree);
            });
        }
        return result;
    }

    /**
     * 将jsonArray转换成树状结构
     *
     * @param jsonArray Json数组
     * @return 树状结构
     */
    private List<DomainDataModelTreeDto> parseJsonArray(JSONArray jsonArray) {
        List<DomainDataModelTreeDto> tree = new ArrayList<>();
        if (jsonArray.size() == MagicNumbers.ZERO) {
            return tree;
        }
        return parseJsonObject((JSONObject) jsonArray.get(0));
    }

    /**
     * 获取参数类型
     *
     * @param value  参数
     * @return 参数类型
     */
    private String getParamType(Object value) {
        String paramType = "";
        if (value instanceof JSONObject) {
            paramType = "object";
        } else if (value instanceof JSONArray) {
            if (((JSONArray) value).isEmpty()) {
                paramType = "object";
            } else {
                paramType = getParamType(((JSONArray) value).get(0));
            }
        } else if (value instanceof Boolean) {
            paramType = DataTypeEnum.BOOLEAN.getDesc();
        } else if (value instanceof String) {
            if (((String) value).matches(REGEX_DATETIME)) {
                paramType = DataTypeEnum.DATETIME.getDesc();
            } else if (((String) value).matches(REGEX_DATE)) {
                paramType = DataTypeEnum.DATE.getDesc();
            } else {
                paramType = DataTypeEnum.STRING.getDesc();
            }
        } else if (value instanceof Integer || value instanceof Long) {
            paramType = DataTypeEnum.INTEGER.getDesc();
        } else {
            paramType = DataTypeEnum.DOUBLE.getDesc();
        }
        return paramType;
    }

}
