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
package com.wiseco.var.process.app.server.service.converter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.decision.common.business.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.vo.output.DictDetailImportFailOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.DictDetailImportOutputDto;
import com.wiseco.var.process.app.server.enums.DomainDictDetailExcelHeadEnum;
import com.wiseco.var.process.app.server.enums.IconEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDict;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDictDetails;
import com.wiseco.var.process.app.server.repository.entity.VarProcessSpace;
import com.wiseco.var.process.app.server.service.VarProcessDictDetailsService;
import com.wiseco.var.process.app.server.service.VarProcessDictService;
import com.wiseco.var.process.app.server.service.VarProcessSpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: xiewu
 */
@Component
@Slf4j
public class VariableDictDetailsConverter {

    @Autowired
    private VarProcessDictService varProcessDictService;

    @Autowired
    private VarProcessDictDetailsService varProcessDictDetailsService;

    @Autowired
    private VarProcessSpaceService varProcessSpaceService;

    /**
     * 找到最后一个连续的行号
     *
     * @param list 集合
     * @param index 索引
     * @return int
     */
    public static int findContRow(List<Integer> list, int index) {
        int indexNext = index + 1;
        if (indexNext >= list.size()) {
            return index;
        }

        Integer rowNext = list.get(indexNext);
        Integer row = list.get(index);
        //如果下一个行号减一等于当前行号，说明是一个连续的行号，继续寻找是否还有连续的行号
        if (row.equals(rowNext - 1)) {
            return findContRow(list, indexNext);
        } else {
            return index;
        }
    }

    /**
     * 转换成对象且记录有错误的行号
     *
     * @param excelList excelList
     * @param spaceId spaceId
     * @return DictDetailImportOutputDto
     */
    public DictDetailImportOutputDto getExcelConvertObject(List<List<Object>> excelList, Long spaceId) {
        DictDetailImportOutputDto varProcessDictDetailImportOutputDto = new DictDetailImportOutputDto();
        DictDetailImportFailOutputDto failOutputDto = new DictDetailImportFailOutputDto();
        for (int i = 0; i < excelList.size(); i++) {
            List<Object> objectList = excelList.get(i);
            //行号不包含表头
            failOutputDto.setRowNumber(i + 1);
            VarProcessDictDetails varProcessDictDetails = new VarProcessDictDetails();
            varProcessDictDetails.setVarProcessSpaceId(spaceId);
            boolean checkFlag = getVarProcessDictDetail(failOutputDto, objectList, varProcessDictDetails);

            //如果没有错误，则入库
            if (checkFlag) {
                varProcessDictDetails.setCreatedUser(SessionContext.getSessionUser().getUsername());
                varProcessDictDetailsService.save(varProcessDictDetails);
            }
        }

        //设置失败原因
        List<Map<String, String>> failReason = new ArrayList<>();
        jointRowFailMessage(failOutputDto.getCodeOrNameFailRow(), failReason, "字典编码或者名称重复", IconEnum.WARNING);
        jointRowFailMessage(failOutputDto.getTypeCodeFailRow(), failReason, "字典类型编码为空或不存在", IconEnum.ERROR);
        jointRowFailMessage(failOutputDto.getCodeFailRow(), failReason, "字典编码为空或有误", IconEnum.ERROR);
        jointRowFailMessage(failOutputDto.getNameFailRow(), failReason, "字典名称为空", IconEnum.ERROR);
        jointRowFailMessage(failOutputDto.getParentCodeFailRow(), failReason, "上级字典编码不存在", IconEnum.ERROR);

        if (!CollectionUtils.isEmpty(failReason)) {
            varProcessDictDetailImportOutputDto.setFailReason(failReason);
        }
        int total = excelList.size();
        int failTotal = failOutputDto.failTotal();
        int successTotal = total - failTotal;
        Map<String, String> resultMap = new HashMap<>(MagicNumbers.TWO);
        if (failTotal > 0) {
            resultMap.put(IconEnum.WARNING.getKey(), IconEnum.WARNING.getCode());
            resultMap.put("text", "文件总数" + total + "行，导入成功" + successTotal + "行，导入失败" + failTotal + "行");
        } else {
            resultMap.put(IconEnum.SUCCESS.getKey(), IconEnum.SUCCESS.getCode());
            resultMap.put("text", "文件总数" + total + "行，导入成功" + successTotal + "行");
        }
        if (0 != successTotal) {
            // 存在导入成功记录: 刷新变量空间编辑时间
            varProcessSpaceService.update(Wrappers.<VarProcessSpace>lambdaUpdate()
                    .eq(VarProcessSpace::getId, spaceId)
                    .set(VarProcessSpace::getUpdatedTime, new Date()));
        }
        varProcessDictDetailImportOutputDto.setResult(resultMap);
        return varProcessDictDetailImportOutputDto;
    }

    private boolean getVarProcessDictDetail(DictDetailImportFailOutputDto failOutputDto, List<Object> objectList,
                                            VarProcessDictDetails varProcessDictDetails) {
        //如果读取的数据少于头部长度则使用空值填充防止出现异常
        int length = DomainDictDetailExcelHeadEnum.values().length;
        if (objectList.size() < length) {
            for (int i = objectList.size() - 1; i < length; i++) {
                objectList.add(null);
            }
        }
        boolean checkFlag = true;
        for (int k = 0; k < objectList.size(); k++) {
            Object value = objectList.get(k);
            if (k == 0) {
                //字典类型编码
                checkFlag = setVarProcessDictDetailsTypeCode(failOutputDto, varProcessDictDetails, value);
            } else if (k == 1) {
                //字典编码
                checkFlag = setVarProcessDictDetailsCode(failOutputDto, varProcessDictDetails, value);
            } else if (k == MagicNumbers.TWO) {
                //字典名称
                checkFlag = setVarProcessDictDetailsName(failOutputDto, varProcessDictDetails, value);
            } else if (k == MagicNumbers.THREE) {
                //上级字典编码
                if (!ObjectUtils.isEmpty(value)) {
                    checkFlag = setVarProcessDictDetailsParentCode(failOutputDto, varProcessDictDetails, value);
                }
            }
            //如果等于false说明出现了校验错误，直接退出for循环，不用再校验后面的值了，防止错误数量计算重叠
            if (!checkFlag) {
                break;
            }
        }
        return checkFlag;
    }

    private boolean setVarProcessDictDetailsTypeCode(DictDetailImportFailOutputDto failOutputDto,
                                                     VarProcessDictDetails varProcessDictDetails, Object value) {
        if (ObjectUtils.isEmpty(value)) {
            failOutputDto.getTypeCodeFailRow().add(failOutputDto.getRowNumber());
            return false;
        } else {
            String typeCode = String.valueOf(value);
            List<VarProcessDict> varProcessDictList = varProcessDictService.list(new QueryWrapper<VarProcessDict>().lambda()
                    .eq(VarProcessDict::getCode, typeCode)
                    .eq(VarProcessDict::getVarProcessSpaceId, varProcessDictDetails.getVarProcessSpaceId())
                    .eq(VarProcessDict::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                    .select(VarProcessDict::getId));
            if (CollectionUtils.isEmpty(varProcessDictList)) {
                log.error("第" + failOutputDto.getRowNumber() + "字典类型编码不存在！");
                failOutputDto.getTypeCodeFailRow().add(failOutputDto.getRowNumber());
                return false;
            } else {
                varProcessDictDetails.setDictId(varProcessDictList.get(0).getId());
            }
        }
        return true;
    }

    private boolean setVarProcessDictDetailsCode(DictDetailImportFailOutputDto failOutputDto,
                                                 VarProcessDictDetails varProcessDictDetails, Object value) {
        if (ObjectUtils.isEmpty(value)) {
            failOutputDto.getCodeFailRow().add(failOutputDto.getRowNumber());
            return false;
        } else {
            String code = String.valueOf(value);
            long count = varProcessDictDetailsService.count(new QueryWrapper<VarProcessDictDetails>().lambda()
                    .eq(VarProcessDictDetails::getCode, code)
                    .eq(VarProcessDictDetails::getDictId, varProcessDictDetails.getDictId())
                    .eq(VarProcessDictDetails::getVarProcessSpaceId, varProcessDictDetails.getVarProcessSpaceId())
                    .eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            if (count > 0) {
                log.error("第" + failOutputDto.getRowNumber() + "字典编码重复！");
                failOutputDto.getCodeOrNameFailRow().add(failOutputDto.getRowNumber());
                return false;
            }
            varProcessDictDetails.setCode(code);
        }
        return true;
    }

    private boolean setVarProcessDictDetailsName(DictDetailImportFailOutputDto failOutputDto,
                                                 VarProcessDictDetails varProcessDictDetails, Object value) {
        if (ObjectUtils.isEmpty(value)) {
            failOutputDto.getNameFailRow().add(failOutputDto.getRowNumber());
            return false;
        } else {
            String name = String.valueOf(value);
            long count = varProcessDictDetailsService.count(new QueryWrapper<VarProcessDictDetails>().lambda()
                    .eq(VarProcessDictDetails::getName, name)
                    .eq(VarProcessDictDetails::getDictId, varProcessDictDetails.getDictId())
                    .eq(VarProcessDictDetails::getVarProcessSpaceId, varProcessDictDetails.getVarProcessSpaceId())
                    .eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
            if (count > 0) {
                log.error("第" + failOutputDto.getRowNumber() + "字典名称重复！");
                failOutputDto.getCodeOrNameFailRow().add(failOutputDto.getRowNumber());
                return false;
            }
            varProcessDictDetails.setName(name);
        }
        return true;
    }

    private boolean setVarProcessDictDetailsParentCode(DictDetailImportFailOutputDto failOutputDto,
                                                       VarProcessDictDetails varProcessDictDetails, Object value) {
        String parentCode = String.valueOf(value);
        VarProcessDictDetails varProcessDictDetailsParent = varProcessDictDetailsService.getOne(new QueryWrapper<VarProcessDictDetails>().lambda()
                .eq(VarProcessDictDetails::getCode, parentCode)
                .eq(VarProcessDictDetails::getDictId, varProcessDictDetails.getDictId())
                .eq(VarProcessDictDetails::getVarProcessSpaceId, varProcessDictDetails.getVarProcessSpaceId())
                .eq(VarProcessDictDetails::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
        if (null == varProcessDictDetailsParent) {
            log.error("第" + failOutputDto.getRowNumber() + "上级字典编码不存在！");
            failOutputDto.getParentCodeFailRow().add(failOutputDto.getRowNumber());
            return false;
        } else if (parentCode.equals(varProcessDictDetails.getCode())) {
            log.error("第" + failOutputDto.getRowNumber() + "上级字典编码不能等于自己本身！");
            failOutputDto.getParentCodeFailRow().add(failOutputDto.getRowNumber());
            return false;
        }
        varProcessDictDetails.setParentCode(parentCode);
        return true;
    }

    /**
     * 寻找子集数据id
     *
     * @param codeListMap codeListMap
     * @param parentCode  parentCode
     * @param varProcessDictDetailsSonIdList varProcessDictDetailsSonIdList
     */
    public void findVarProcessDictDetailsSon(Map<String, List<VarProcessDictDetails>> codeListMap, String parentCode, List<Long> varProcessDictDetailsSonIdList) {
        //等于null说明已经组装完成
        if (CollectionUtils.isEmpty(codeListMap)) {
            return;
        }
        //这里是为了判断listNewOutputDtoList在map中是否有子集，如果有继续走，否则结束，为了避免数据错乱的情况下发生栈溢出
        List<VarProcessDictDetails> varProcessDictDetailsList = codeListMap.get(parentCode);
        if (!CollectionUtils.isEmpty(varProcessDictDetailsList)) {
            codeListMap.remove(parentCode);
            List<Long> idList = varProcessDictDetailsList.stream().map(m -> m.getId()).collect(Collectors.toList());
            varProcessDictDetailsSonIdList.addAll(idList);
            for (VarProcessDictDetails varProcessDictDetails : varProcessDictDetailsList) {
                findVarProcessDictDetailsSon(codeListMap, varProcessDictDetails.getCode(), varProcessDictDetailsSonIdList);
            }
        }
    }

    /**
     * 根据字典项编码寻找它的所有下级
     *
     * @param parentCodeAllMap parentCodeAllMap
     * @param parentCodeMap    parentCodeMap
     * @param parentCodeList   parentCodeList
     */
    public void findVarProcessDictDetailsByCodeAllLowerCode(Map<String, List<VarProcessDictDetails>> parentCodeAllMap,
                                                            Map<String, String> parentCodeMap, List<String> parentCodeList) {
        if (CollectionUtils.isEmpty(parentCodeList)) {
            return;
        }
        List<String> parentCodeListNew = new ArrayList<>();
        for (String parentCode : parentCodeList) {
            List<VarProcessDictDetails> varProcessDictDetailsList = parentCodeAllMap.get(parentCode);
            if (!CollectionUtils.isEmpty(varProcessDictDetailsList)) {
                for (VarProcessDictDetails varProcessDictDetails : varProcessDictDetailsList) {
                    parentCodeMap.put(varProcessDictDetails.getCode(), varProcessDictDetails.getCode());
                    parentCodeListNew.add(varProcessDictDetails.getCode());
                }
            }
        }
        parentCodeList.clear();
        if (!CollectionUtils.isEmpty(parentCodeListNew)) {
            findVarProcessDictDetailsByCodeAllLowerCode(parentCodeAllMap, parentCodeMap, parentCodeListNew);
        }
    }

    /**
     * 拼接excel哪些行出现了错误
     *
     * @param list list
     * @param failReason failReason
     * @param failDesc failDesc
     * @param iconEnum iconEnum
     */
    public void jointRowFailMessage(List<Integer> list, List failReason, String failDesc, IconEnum iconEnum) {
        if (!CollectionUtils.isEmpty(list)) {
            StringBuilder stringBuilder = new StringBuilder();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                Integer row = list.get(i);
                //找到最后一个连续的行号
                int contRow = findContRow(list, i);
                //没有连续的行号
                if (contRow == i) {
                    stringBuilder.append(row + (i == size - 1 ? "" : ","));
                } else {
                    i = contRow;

                    stringBuilder.append(row + "-" + list.get(contRow) + (i == size - 1 ? "" : ","));
                }
            }
            Map<String, String> map = new LinkedHashMap<>();
            map.put(iconEnum.getKey(), iconEnum.getCode());
            map.put("text", failDesc + "：" + stringBuilder);
            failReason.add(map);
        }
    }

}
