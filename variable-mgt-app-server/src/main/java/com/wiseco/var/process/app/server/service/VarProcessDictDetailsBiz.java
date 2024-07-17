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
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.boot.commons.io.ExcelReadUtils;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.web.util.ResponseHeaderUtils;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.input.DictDetailsInsertInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.DictDetailsParentListInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.DictDetailImportOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.DictDetailsParentListOutputDto;
import com.wiseco.var.process.app.server.enums.DomainDictDetailExcelHeadEnum;
import com.wiseco.var.process.app.server.enums.ExcelDomainRosterDetailTemplateEnum;
import com.wiseco.var.process.app.server.enums.IconEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDictDetails;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.converter.VariableDictDetailsConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: xiewu
 */
@Service
@Slf4j
public class VarProcessDictDetailsBiz {

    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    @Autowired
    private VarProcessDictDetailsService varProcessDictDetailsService;

    @Autowired
    private VariableDictDetailsConverter variableDictDetailsConverter;

    /**
     * insertDictDetails
     * @param inputDto inputDto
     */
    public void insertDictDetails(DictDetailsInsertInputDto inputDto) {
        List<VarProcessDictDetails> codeList = varProcessDictDetailsService.list(new QueryWrapper<VarProcessDictDetails>().lambda().eq(VarProcessDictDetails::getCode, inputDto.getCode()).eq(VarProcessDictDetails::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessDictDetails::getDictId, inputDto.getDictId()).eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        List<VarProcessDictDetails> nameList = varProcessDictDetailsService.list(new QueryWrapper<VarProcessDictDetails>().lambda().eq(VarProcessDictDetails::getName, inputDto.getName()).eq(VarProcessDictDetails::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessDictDetails::getDictId, inputDto.getDictId()).eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (!StringUtils.isEmpty(inputDto.getParentCode())) {
            List<VarProcessDictDetails> parentCodeList = varProcessDictDetailsService.list(new QueryWrapper<VarProcessDictDetails>().lambda().eq(VarProcessDictDetails::getCode, inputDto.getParentCode()).eq(VarProcessDictDetails::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessDictDetails::getDictId, inputDto.getDictId()).eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            if (CollectionUtils.isEmpty(parentCodeList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.CONFIG_DICT_NOT_CONFIG, "父级字典项不存在！");
            }
        }
        VarProcessDictDetails varProcessDictDetails = new VarProcessDictDetails();
        BeanUtils.copyProperties(inputDto, varProcessDictDetails);
        if (null == inputDto.getId()) {
            if (!CollectionUtils.isEmpty(codeList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "字典项编码不能重复！");
            }
            if (!CollectionUtils.isEmpty(nameList)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "字典项名称不能重复！");
            }
            varProcessDictDetails.setVarProcessSpaceId(inputDto.getSpaceId());
            varProcessDictDetails.setCreatedUser(SessionContext.getSessionUser().getUsername());
            varProcessDictDetails.setUpdatedUser(SessionContext.getSessionUser().getUsername());
            varProcessDictDetailsService.save(varProcessDictDetails);
            // 刷新变量空间编辑时间
            refreshVarProcessSpaceUpdateTime(inputDto.getSpaceId());
        } else {
            if (!StringUtils.isEmpty(inputDto.getParentCode()) && inputDto.getParentCode().equals(inputDto.getCode())) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "字典项的上级字典不能为本身！");
            }
            VarProcessDictDetails dbDetails = varProcessDictDetailsService.getById(inputDto.getId());
            if (null == dbDetails) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "字典项id数据不存在！");
            }
            if (!CollectionUtils.isEmpty(codeList)) {
                long count = codeList.stream().filter(f -> !f.getId().equals(dbDetails.getId())).count();
                if (count > 0) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "字典项编码不能重复！");
                }
            }
            if (!CollectionUtils.isEmpty(nameList)) {
                long count = nameList.stream().filter(f -> !f.getId().equals(dbDetails.getId())).count();
                if (count > 0) {
                    throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "字典项名称不能重复！");
                }
            }
//            if (!dbDetails.getName().equals(varProcessDictDetails.getName()) || !dbDetails.getCode().equals(varProcessDictDetails.getCode()) || !dbDetails.getParentCode().equals(varProcessDictDetails.getParentCode()) || !dbDetails.getState().equals(varProcessDictDetails.getState())) {
            if (!dbDetails.getName().equals(varProcessDictDetails.getName()) || !dbDetails.getCode().equals(varProcessDictDetails.getCode()) || !dbDetails.getState().equals(varProcessDictDetails.getState()) ||  !varProcessDictDetails.getParentCode().equals(dbDetails.getParentCode())) {

                // 字典项名称, 编码, 层级或状态有变化
                varProcessDictDetails.setUpdatedUser(SessionContext.getSessionUser().getUsername());
                varProcessDictDetails.setUpdatedTime(new Date());
                varProcessDictDetailsService.updateById(varProcessDictDetails);
                if (!dbDetails.getCode().equals(varProcessDictDetails.getCode())) {
                    varProcessDictDetailsService.updateParent(varProcessDictDetails.getDictId(), dbDetails.getCode(), varProcessDictDetails.getCode());
                }
                // 刷新变量空间编辑时间
                refreshVarProcessSpaceUpdateTime(inputDto.getSpaceId());
            }
        }
    }

    /**
     * findDictDetailsByParent
     * @param inputDto 输入
     * @return List
     */
    public List<DictDetailsParentListOutputDto> findDictDetailsByParent(DictDetailsParentListInputDto inputDto) {
        List<VarProcessDictDetails> varProcessDictDetailsList = varProcessDictDetailsService.list(new QueryWrapper<VarProcessDictDetails>().lambda().eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(VarProcessDictDetails::getVarProcessSpaceId, inputDto.getSpaceId()).eq(VarProcessDictDetails::getDictId, inputDto.getDictId()));
        if (CollectionUtils.isEmpty(varProcessDictDetailsList)) {
            return null;
        }

        List<DictDetailsParentListOutputDto> list = new ArrayList<>();
        //寻找要排除的下级节点编码
        Map<String, String> parentCodeMap = new HashMap<>(MagicNumbers.SIXTEEN);
        if (!StringUtils.isEmpty(inputDto.getCode())) {
            parentCodeMap.put(inputDto.getCode(), inputDto.getCode());
            Map<String, List<VarProcessDictDetails>> parentCodeAllMap = varProcessDictDetailsList.stream().filter(f -> !StringUtils.isEmpty(f.getParentCode())).collect(Collectors.groupingBy(VarProcessDictDetails::getParentCode));
            List<String> parentCodeList = new ArrayList<>();
            parentCodeList.add(inputDto.getCode());
            variableDictDetailsConverter.findVarProcessDictDetailsByCodeAllLowerCode(parentCodeAllMap, parentCodeMap, parentCodeList);
        }
        for (VarProcessDictDetails varProcessDictDetails : varProcessDictDetailsList) {
            //排除自身编码和它的所有下级编码
            if (!StringUtils.isEmpty(inputDto.getCode()) && null != parentCodeMap.get(varProcessDictDetails.getCode())) {
                continue;
            }
            DictDetailsParentListOutputDto outputDto = new DictDetailsParentListOutputDto();
            BeanUtils.copyProperties(varProcessDictDetails, outputDto);
            list.add(outputDto);
        }
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list;
    }

    /**
     * deleteDictDetailsById
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictDetailsById(Long id) {
        VarProcessDictDetails varProcessDictDetails = varProcessDictDetailsService.getOne(new QueryWrapper<VarProcessDictDetails>().lambda().eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(VarProcessDictDetails::getId, id));
        if (null == varProcessDictDetails) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "字典项id数据不存在！");
        }

        //查询这个字典类型下的所有字典项
        List<VarProcessDictDetails> varProcessDictDetailsList = varProcessDictDetailsService.list(new QueryWrapper<VarProcessDictDetails>().lambda().eq(VarProcessDictDetails::getVarProcessSpaceId, varProcessDictDetails.getVarProcessSpaceId()).eq(VarProcessDictDetails::getDictId, varProcessDictDetails.getDictId()).eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));

        //删除字典项本身
        varProcessDictDetailsService.update(new UpdateWrapper<VarProcessDictDetails>().lambda().eq(VarProcessDictDetails::getId, id).eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).eq(VarProcessDictDetails::getId, id).set(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()));

        //过滤掉自己本身
        long count = varProcessDictDetailsList.stream().filter(f -> !f.getCode().equals(varProcessDictDetails.getCode())).count();
        //除了自己还有其它字典
        if (count > 1) {
            //给空父级编码赋值，作为分组key
            for (VarProcessDictDetails dictDetails : varProcessDictDetailsList) {
                if (StringUtils.isEmpty(dictDetails.getParentCode())) {
                    dictDetails.setParentCode("");
                }
            }
            Map<String, List<VarProcessDictDetails>> codeListMap = varProcessDictDetailsList.stream().collect(Collectors.groupingBy(VarProcessDictDetails::getParentCode));
            List<Long> varProcessDictDetailsSonIdList = new ArrayList<>();
            variableDictDetailsConverter.findVarProcessDictDetailsSon(codeListMap, varProcessDictDetails.getCode(), varProcessDictDetailsSonIdList);
            //删除自己
            varProcessDictDetailsSonIdList.remove(id);
            if (!CollectionUtils.isEmpty(varProcessDictDetailsSonIdList)) {
                //删除自定项的子集
                varProcessDictDetailsService.update(new UpdateWrapper<VarProcessDictDetails>().lambda().eq(VarProcessDictDetails::getVarProcessSpaceId, varProcessDictDetails.getVarProcessSpaceId()).eq(VarProcessDictDetails::getDictId, varProcessDictDetails.getDictId()).in(VarProcessDictDetails::getId, varProcessDictDetailsSonIdList).eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()).set(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.DELETED.getCode()));
            }
        }
        // 刷新变量空间编辑时间
        refreshVarProcessSpaceUpdateTime(varProcessDictDetails.getVarProcessSpaceId());
    }

    /**
     * importDictDetailsExcel
     * @param file file
     * @param spaceId spaceId
     * @return DictDetailImportOutputDto
     */
    public DictDetailImportOutputDto importDictDetailsExcel(MultipartFile file, Long spaceId) {
        VarProcessSpace varProcessSpace = varProcessSpaceService.getById(spaceId);
        if (null == varProcessSpace) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_SPACE_NOT_FOUND, "空间id不存在！");
        }

        String fileName;
        List<String> sheetNameList;
        try {
            fileName = file.getOriginalFilename();
            assert fileName != null;
            if (!fileName.matches(ExcelReadUtils.EXCEL2003EXPRESSION) && !fileName.matches(ExcelReadUtils.EXCEL2007EXPRESSION)) {
                DictDetailImportOutputDto outputDto = new DictDetailImportOutputDto();
                Map<String, String> resultMap = new HashMap<>(MagicNumbers.TWO);
                resultMap.put(IconEnum.ERROR.getKey(), IconEnum.ERROR.getCode());
                resultMap.put("text", "文件格式错误，无法导入");
                outputDto.setResult(resultMap);
                return outputDto;
            }
            sheetNameList = ExcelReadUtils.readExcelSheetName(file.getInputStream(), fileName);
        } catch (IOException e) {
            log.error("读取字典项数据excel的sheetName异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "读取字典项数据excel的sheetName异常！");
        }
        if (CollectionUtils.isEmpty(sheetNameList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "excel中的sheetName为空！");
        }

        try {
            fileName = file.getOriginalFilename();
            sheetNameList = ExcelReadUtils.readExcelSheetName(file.getInputStream(), fileName);
        } catch (IOException e) {
            log.error("读取字典项数据excel的sheetName异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "读取字典项数据excel的sheetName异常！");
        }

        //读取表头
        List<String> excelHeadList;
        try {
            excelHeadList = ExcelReadUtils.readExcelHead(file.getInputStream(), sheetNameList.get(0), fileName);
        } catch (IOException e) {
            log.error("读取字典项数据excel的表头异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "读取字典项数据excel的表头异常！");
        }
        if (CollectionUtils.isEmpty(excelHeadList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "表头不能为空！");
        }
        StringBuilder headExcelList = new StringBuilder();
        for (String value : excelHeadList) {
            if (null == DomainDictDetailExcelHeadEnum.getMessageEnum(value)) {
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, value + "表头不存在！");
            }
            headExcelList.append(value);
        }

        //验证表头顺序
        StringBuilder headExcelEnum = new StringBuilder();
        for (DomainDictDetailExcelHeadEnum value : DomainDictDetailExcelHeadEnum.values()) {
            headExcelEnum.append(value.getMessage());
        }
        if (!headExcelList.toString().contentEquals(headExcelEnum)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "表头顺序错误，必须是：" + headExcelEnum + "！");
        }

        //读取数据
        List<List<Object>> excelList;
        try {
            excelList = ExcelReadUtils.readExcelToObjectList(file.getInputStream(), sheetNameList.get(0), fileName);
        } catch (IOException e) {
            log.error("读字典项数据excel的数据异常：", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, "读字典项数据excel的数据异常！");
        }
        if (CollectionUtils.isEmpty(excelList)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_CHECK_FAIL, "字典项数据数据不能为空！");
        }
        return variableDictDetailsConverter.getExcelConvertObject(excelList, spaceId);
    }

    /**
     * exportDictDetailsTemplate
     * @param response response
     */
    public void exportDictDetailsTemplate(HttpServletResponse response) {
        String fileName = ExcelDomainRosterDetailTemplateEnum.DOMAIN_DICT_DETAILS_TEMPLATE_PATH.getFileName();
        String path = ExcelDomainRosterDetailTemplateEnum.DOMAIN_DICT_DETAILS_TEMPLATE_PATH.getPath();
        try {
            ResponseHeaderUtils.writeOutputStream(response, fileName, path);
        } catch (IOException e) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_ERROR, "导出字典项模板输入输出IO流读写异常！");
        }
    }

    /**
     * 刷新变量空间编辑时间
     *
     * @param varProcessSpaceId 变量空间 ID
     */
    private void refreshVarProcessSpaceUpdateTime(Long varProcessSpaceId) {
        varProcessSpaceService.update(Wrappers.<VarProcessSpace>lambdaUpdate().eq(VarProcessSpace::getId, varProcessSpaceId).set(VarProcessSpace::getUpdatedTime, new Date()));
    }
}
