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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.auth.common.DepartmentSmallDTO;
import com.wiseco.boot.cache.CacheClient;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.web.util.ExcelExportUtils;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.CacheKeyPrefixConstant;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.GenerateIdUtil;
import com.wiseco.var.process.app.server.controller.vo.input.VariableQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.ImportVariableOutputDTO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableListOutputDto;
import com.wiseco.var.process.app.server.enums.CategoryTypeEnum;
import com.wiseco.var.process.app.server.enums.ImportVariableErrorEnum;
import com.wiseco.var.process.app.server.enums.VariableActionTypeEnum;
import com.wiseco.var.process.app.server.enums.VariableExcelHeadEnum;
import com.wiseco.var.process.app.server.enums.VariableStatusEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCategory;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableLifecycle;
import com.wisecotech.json.JSON;
import com.wisecotech.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VariableImportExportBiz {
    @Autowired
    VarProcessParamService varProcessParamService;
    @Autowired
    VarProcessContentService varProcessContentService;
    @Autowired
    private VarProcessVariableService varProcessVariableService;
    @Autowired
    private VarProcessVariableLifecycleService varProcessVariableLifecycleService;
    @Autowired
    private VarProcessCategoryService varProcessCategoryService;
    @Autowired
    private VariableContentBiz variableContentBiz;
    @Autowired
    private VarProcessSpaceService varProcessSpaceService;
    @Autowired(required = false)
    private CacheClient remoteCacheClient;

    /**
     * 继续导入
     * @param identifier identifier
     * @return List
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Long> proceedImportVariable(String identifier) {
        String json = (String) remoteCacheClient.get(CacheKeyPrefixConstant.VAR_IMPORT + identifier);
        if (StringUtils.isEmpty(json)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT, "无效的identifier:" + identifier);
        }
        List<VarProcessVariable> variableList = JSONObject.parseArray(json, VarProcessVariable.class);
        List<Long> ids = insert(variableList);
        remoteCacheClient.evict(CacheKeyPrefixConstant.VAR_IMPORT + identifier);
        return ids;
    }

    /**
     * 导入变量
     *
     * @param spaceId spaceId
     * @param file file
     * @return ImportVariableOutputDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public ImportVariableOutputDTO importVariable(Long spaceId, MultipartFile file) {
        // 查询变量空间
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(spaceId);
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "未查询到变量空间信息！");
        }
        Workbook workbook;
        try {
            //获取表对象
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            log.error("文件解析异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "文件解析异常！", e);
        }
        //只取第一个sheet页
        Sheet sheet = workbook.getSheetAt(0);
        int firstRowNum = sheet.getFirstRowNum();
        if (firstRowNum != 0) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, sheet.getSheetName() + "未识别到表头信息！");
        }
        return importVariable(sheet, spaceId);
    }

    private ImportVariableOutputDTO importVariable(Sheet sheet, Long spaceId) {
        //存储解析成功的数据
        List<VarProcessVariable> insertList = new ArrayList<>();
        //存储解析失败的数据和对应的行号
        Map<String, List<String>> failReasonMap = new HashMap<>(MagicNumbers.EIGHT);
        //存储校验未通过的行号
        ArrayList<Integer> failRowList = new ArrayList<>();
        parseExcelContent(insertList, failReasonMap, failRowList, sheet, spaceId);
        //把错误信息转换成VO需要的数据结构
        List<ImportVariableOutputDTO.ErrorInfo> failReason = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : failReasonMap.entrySet()) {
            StringBuilder stringBuilder = new StringBuilder();
            if (entry.getKey().equals(ImportVariableErrorEnum.VARIABLE_NAME_NONE.getErrorName()) || entry.getKey().equals(ImportVariableErrorEnum.VARIABLE_CODE_NONE.getErrorName()) || entry.getKey().equals(ImportVariableErrorEnum.VARIABLE_CLASS_NONE.getErrorName()) || entry.getKey().equals(ImportVariableErrorEnum.DATE_TYPE_NONE.getErrorName())) {
                stringBuilder.append("行号");
            }
            List<String> distinctList = entry.getValue().stream().distinct().collect(Collectors.toList());
            for (String detail : distinctList) {
                stringBuilder.append(detail).append("、");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            ImportVariableOutputDTO.ErrorInfo errorInfo = new ImportVariableOutputDTO.ErrorInfo();
            errorInfo.setDesc(entry.getKey());
            errorInfo.setDetails(stringBuilder.toString());
            failReason.add(errorInfo);
        }
        //校验失败直接返回失败详情
        ImportVariableOutputDTO importVariableOutputDTO = new ImportVariableOutputDTO();
        int totalRows = sheet.getLastRowNum();
        importVariableOutputDTO.setTotalRows(totalRows);
        if (!failRowList.isEmpty()) {
            //表头也通过校验，需要+1
            importVariableOutputDTO.setValidRows(totalRows - failRowList.size());
            importVariableOutputDTO.setInValidRows(failRowList.size());
            importVariableOutputDTO.setFailReason(failReason);
            if (importVariableOutputDTO.getValidRows() != 0) {
                String generateId = GenerateIdUtil.generateId();
                importVariableOutputDTO.setIdentifier(generateId);
                remoteCacheClient.put(CacheKeyPrefixConstant.VAR_IMPORT + generateId, JSON.toJSONString(insertList));
            }
            return importVariableOutputDTO;
        } else {
            importVariableOutputDTO.setValidRows(totalRows);
            importVariableOutputDTO.setInValidRows(0);
        }
        //数据落库
        List<Long> ids = insert(insertList);
        importVariableOutputDTO.setVariableDetailIds(ids);
        return importVariableOutputDTO;
    }

    /**
     * 解析excel内容
     * @param insertList insertList
     * @param failReasonMap failReasonMap
     * @param failRowList failRowList
     * @param sheet sheet
     * @param spaceId spaceId
     */
    private void parseExcelContent(List<VarProcessVariable> insertList, Map<String, List<String>> failReasonMap, ArrayList<Integer> failRowList, Sheet sheet, Long spaceId) {
        Map<String, Integer> headNameIndexMap = getRowHeadIndex(sheet.getRow(0));
        //查找变量名称和变量编码在excel中出现的次数
        HashMap<String, Integer> variableNameMap = new HashMap<>(MagicNumbers.EIGHT);
        HashMap<String, Integer> variableCodeMap = new HashMap<>(MagicNumbers.EIGHT);
        getNameAndCodeAppearCount(sheet, headNameIndexMap, variableNameMap, variableCodeMap);
        //查找数据库中存在的查询变量名称、变量编码、变量分类
        List<String> nameListByDb = varProcessVariableService.list(new QueryWrapper<VarProcessVariable>().lambda().select(VarProcessVariable::getLabel).eq(VarProcessVariable::getVarProcessSpaceId, spaceId).eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())).stream().map(VarProcessVariable::getLabel).collect(Collectors.toList());
        List<String> codeListByDb = varProcessVariableService.list(new QueryWrapper<VarProcessVariable>().lambda().select(VarProcessVariable::getName).eq(VarProcessVariable::getVarProcessSpaceId, spaceId).eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())).stream().map(VarProcessVariable::getName).collect(Collectors.toList());
        Map<String, Long>  categoryMap = varProcessCategoryService.list(new QueryWrapper<VarProcessCategory>().lambda().select(VarProcessCategory::getId, VarProcessCategory::getName).eq(VarProcessCategory::getVarProcessSpaceId, spaceId).eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(VarProcessCategory::getCategoryType, CategoryTypeEnum.VARIABLE)).stream().collect((Collectors.toMap(VarProcessCategory::getName, VarProcessCategory::getId, (k1, k2) -> k1)));
        List<String> legalfateTypeList = Arrays.asList("string", "int", "double", "date", "datetime", "boolean", "String");
        Iterator<Row> rowIterator = sheet.rowIterator();
        boolean isFirstRow = true;
        //该循环用于校验excel的合规性
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }
            boolean flag = true;
            int rowNo = row.getRowNum() + 1;
            String variableNameCell = new DataFormatter().formatCellValue(row.getCell(headNameIndexMap.get(VariableExcelHeadEnum.VARIABLE_NAME.getHeadName())));
            if (StringUtils.isEmpty(variableNameCell)) {
                addErrorInfo(failReasonMap, ImportVariableErrorEnum.VARIABLE_NAME_NONE.getErrorName(), String.valueOf(rowNo));
                flag = false;
            } else {
                if (variableNameMap.get(variableNameCell) > 1 || nameListByDb.contains(variableNameCell)) {
                    addErrorInfo(failReasonMap, ImportVariableErrorEnum.VARIABLE_NAME_REPEAT.getErrorName(), variableNameCell + "变量");
                    flag = false;
                }
            }
            //变量编码
            String variableCodeCell = new DataFormatter().formatCellValue(row.getCell(headNameIndexMap.get(VariableExcelHeadEnum.VARIABLE_CODE.getHeadName())));
            if (StringUtils.isEmpty(variableCodeCell)) {
                addErrorInfo(failReasonMap, ImportVariableErrorEnum.VARIABLE_CODE_NONE.getErrorName(), String.valueOf(rowNo));
                flag = false;
            } else {
                if (variableCodeMap.get(variableCodeCell) > 1 || codeListByDb.contains(variableCodeCell)) {
                    addErrorInfo(failReasonMap, ImportVariableErrorEnum.VARIABLE_CODE_REPEAT.getErrorName(), variableCodeCell);
                    flag = false;
                }
            }
            //变量分类
            String variableClassCell = new DataFormatter().formatCellValue(row.getCell(headNameIndexMap.get(VariableExcelHeadEnum.VARIABLE_CLASS.getHeadName())));
            if (StringUtils.isEmpty(variableClassCell)) {
                addErrorInfo(failReasonMap, ImportVariableErrorEnum.VARIABLE_CLASS_NONE.getErrorName(), String.valueOf(rowNo));
                flag = false;
            } else {
                if (!categoryMap.containsKey(variableClassCell)) {
                    addErrorInfo(failReasonMap, ImportVariableErrorEnum.VARIABLE_CLASS_NOT_EXIST.getErrorName(), variableClassCell);
                    flag = false;
                }
            }
            //变量类型
            String dateTypeCell = new DataFormatter().formatCellValue(row.getCell(headNameIndexMap.get(VariableExcelHeadEnum.DATE_TYPE.getHeadName())));
            if (StringUtils.isEmpty(dateTypeCell)) {
                addErrorInfo(failReasonMap, ImportVariableErrorEnum.DATE_TYPE_NONE.getErrorName(), String.valueOf(rowNo));
                flag = false;
            } else {
                if (!legalfateTypeList.contains(dateTypeCell)) {
                    addErrorInfo(failReasonMap, ImportVariableErrorEnum.DATE_TYPE_NOT_RIGHTFUL.getErrorName(), dateTypeCell);
                    flag = false;
                }
            }
            if (flag) {
                String description = new DataFormatter().formatCellValue(row.getCell(headNameIndexMap.get(VariableExcelHeadEnum.DESCRIPTION.getHeadName())));
                VarProcessVariable varProcessVariable = VarProcessVariable.builder().varProcessSpaceId(spaceId).name(variableCodeCell).label(variableNameCell).categoryId(categoryMap.get(variableClassCell)).dataType(dateTypeCell).description(description).identifier(GenerateIdUtil.generateId()).version(NumberUtils.INTEGER_ONE).createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build();
                DepartmentSmallDTO department = SessionContext.getSessionUser().getUser().getDepartment();
                if (department != null) {
                    varProcessVariable.setDeptCode(department.getCode());
                }
                insertList.add(varProcessVariable);
            } else {
                failRowList.add(rowNo);
            }
        }
    }

    /**
     * getNameAndCodeAppearCount
     * @param sheet sheet
     * @param headNameIndexMap headNameIndexMap
     * @param variableNameMap variableNameMap
     * @param variableCodeMap variableCodeMap
     */
    private static void getNameAndCodeAppearCount(Sheet sheet, Map<String, Integer> headNameIndexMap, HashMap<String, Integer> variableNameMap, HashMap<String, Integer> variableCodeMap) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        boolean isFirstRow = true;
        //本次循环用于记录变量名称和变量代码出现的行
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }
            //变量名称
            String variableNameCell = new DataFormatter().formatCellValue(row.getCell(headNameIndexMap.get(VariableExcelHeadEnum.VARIABLE_NAME.getHeadName())));
            if (!StringUtils.isEmpty(variableNameCell)) {
                if (variableNameMap.containsKey(variableNameCell)) {
                    variableNameMap.put(variableNameCell, variableNameMap.get(variableNameCell) + 1);
                } else {
                    variableNameMap.put(variableNameCell, 1);
                }
            }
            String variableCodeCell = new DataFormatter().formatCellValue(row.getCell(headNameIndexMap.get(VariableExcelHeadEnum.VARIABLE_CODE.getHeadName())));
            if (!StringUtils.isEmpty(variableCodeCell)) {
                if (variableCodeMap.containsKey(variableCodeCell)) {
                    variableCodeMap.put(variableCodeCell, variableCodeMap.get(variableCodeCell) + 1);
                } else {
                    variableCodeMap.put(variableCodeCell, 1);
                }
            }
        }
    }

    /**
     * 查找已经存在的变量
     * @param nameListByDb  nameListByDb
     * @param codeListByDb  codeListByDb
     * @param categoryMap  categoryMap
     * @param spaceId     spaceId
     */
    private void findExistentVariable(List<String> nameListByDb, List<String> codeListByDb, Map<String, Long> categoryMap, Long spaceId) {
        nameListByDb = varProcessVariableService.list(new QueryWrapper<VarProcessVariable>().lambda().select(VarProcessVariable::getLabel).eq(VarProcessVariable::getVarProcessSpaceId, spaceId).eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())).stream().map(VarProcessVariable::getLabel).collect(Collectors.toList());
        log.info("nameListByDb: " + nameListByDb);
        //数据库已经存在的变量代码
        codeListByDb = varProcessVariableService.list(new QueryWrapper<VarProcessVariable>().lambda().select(VarProcessVariable::getName).eq(VarProcessVariable::getVarProcessSpaceId, spaceId).eq(VarProcessVariable::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())).stream().map(VarProcessVariable::getName).collect(Collectors.toList());
        log.info("codeListByDb: " + codeListByDb);
        //key：类别 value:类别Id
        categoryMap = varProcessCategoryService.list(new QueryWrapper<VarProcessCategory>().lambda().select(VarProcessCategory::getId, VarProcessCategory::getName).eq(VarProcessCategory::getVarProcessSpaceId, spaceId).eq(VarProcessCategory::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(VarProcessCategory::getCategoryType, CategoryTypeEnum.VARIABLE)).stream().collect((Collectors.toMap(VarProcessCategory::getName, VarProcessCategory::getId, (k1, k2) -> k1)));
        log.info("categoryMap: " + categoryMap);
    }

    private List<Long> insert(List<VarProcessVariable> insertList) {
        varProcessVariableService.saveBatch(insertList);
        List<Long> idList = insertList.stream().map(VarProcessVariable::getId).collect(Collectors.toList());
        //批量插入生命周期
        List<VarProcessVariableLifecycle> varProcessVariableLifecycleList = idList.stream().map(id -> VarProcessVariableLifecycle.builder().variableId(id).actionType(VariableActionTypeEnum.ADD.getCode()).status(VariableActionTypeEnum.ADD.getStatusEnum()).createdUser(SessionContext.getSessionUser().getUsername()).updatedUser(SessionContext.getSessionUser().getUsername()).build()).collect(Collectors.toList());
        varProcessVariableLifecycleService.saveBatch(varProcessVariableLifecycleList);
        return idList;
    }

    /**
     * 添加错误行号
     * @param failMap failMap
     * @param errorDesc errorDesc
     * @param errorDetails errorDetails
     */
    private void addErrorInfo(Map<String, List<String>> failMap, String errorDesc, String errorDetails) {
        List<String> list;
        if (failMap.containsKey(errorDesc)) {
            list = failMap.get(errorDesc);
            if (!list.contains(errorDesc)) {
                list.add(errorDetails);
            }
            failMap.put(errorDesc, list);
        } else {
            list = new ArrayList<>();
            list.add(errorDetails);
            failMap.put(errorDesc, list);
        }
    }

    /**
     * 获取需要的表头及对应索引位置
     * @param hearRow hearRow
     * @return headNameIndexMap
     */
    private Map<String, Integer> getRowHeadIndex(Row hearRow) {
        Map<String, Integer> headMap = new HashMap<>(MagicNumbers.EIGHT);
        Iterator<Cell> cellIterator = hearRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (!cell.getCellType().equals(CellType.STRING)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "表头只能是String类型！");
            }
            String trim = new DataFormatter().formatCellValue(cell).trim();
            headMap.put(trim, cell.getColumnIndex());
        }
        Map<String, Integer> headNameIndexMap = headMap.entrySet().stream().filter(map -> VariableExcelHeadEnum.get(map.getKey()) != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        //表头校验
        for (VariableExcelHeadEnum variableExcelHeadEnum : VariableExcelHeadEnum.values()) {
            if (!headNameIndexMap.containsKey(variableExcelHeadEnum.getHeadName())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "表头缺失字段：" + variableExcelHeadEnum.getHeadName());
            }
        }
        return headNameIndexMap;
    }

    /**
     * 导出变量
     * @param inputDto inputDto
     * @param response response
     */
    public void exportVariable(VariableQueryInputDto inputDto, HttpServletResponse response) {
        IPage<VariableListOutputDto> variableList = variableContentBiz.getVariableList(inputDto);
        //key：索引 value:属性名
        HashMap<Integer, String> headNameMap = new HashMap<>(MagicNumbers.EIGHT);
        headNameMap.put(0, "变量名称");
        headNameMap.put(1, "变量编码");
        headNameMap.put(MagicNumbers.TWO, "变量分类");
        headNameMap.put(MagicNumbers.THREE, "数据类型");
        headNameMap.put(MagicNumbers.FOUR, "版本号");
        headNameMap.put(MagicNumbers.FIVE, "状态");
        headNameMap.put(MagicNumbers.SIX, "描述");
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            //创建表头
            XSSFRow headRow = sheet.createRow(0);
            for (int j = 0; j < headNameMap.size(); j++) {
                XSSFCell cell = headRow.createCell(j);
                cell.setCellValue(headNameMap.get(j));
            }
            //插入数据
            List<VariableListOutputDto> variableListOutputDtoList = variableList.getRecords();
            int rowNo = 1;
            for (VariableListOutputDto variableListOutputDto : variableListOutputDtoList) {
                XSSFRow row = sheet.createRow(rowNo);
                insertExcel(variableListOutputDto, row);
                rowNo++;
                if (!CollectionUtils.isEmpty(variableListOutputDto.getChildren())) {
                    for (VariableListOutputDto children : variableListOutputDto.getChildren()) {
                        row = sheet.createRow(rowNo);
                        insertExcel(children, row);
                        rowNo++;
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ExcelExportUtils.setResponseHeader(response, sdf.format(new Date()) + ".xlsx");
            OutputStream outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            log.error("导出数据模型Excel文件异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "导出数据模型Excel文件异常", e);
        }
    }

    private void insertExcel(VariableListOutputDto variableListOutputDto, XSSFRow row) {
        XSSFCell cell = row.createCell(MagicNumbers.ZERO);
        cell.setCellValue(variableListOutputDto.getLabel());
        cell = row.createCell(MagicNumbers.ONE);
        cell.setCellValue(variableListOutputDto.getName());
        cell = row.createCell(MagicNumbers.TWO);
        cell.setCellValue(variableListOutputDto.getCategoryName());
        cell = row.createCell(MagicNumbers.THREE);
        cell.setCellValue(variableListOutputDto.getDataType());
        cell = row.createCell(MagicNumbers.FOUR);
        cell.setCellValue(variableListOutputDto.getVersion());
        cell = row.createCell(MagicNumbers.FIVE);
        VariableStatusEnum status = variableListOutputDto.getStatus();
        if (status != null) {
            cell.setCellValue(status.getDesc());
        }
        cell = row.createCell(MagicNumbers.SIX);
        cell.setCellValue(variableListOutputDto.getDescription());
    }
}
