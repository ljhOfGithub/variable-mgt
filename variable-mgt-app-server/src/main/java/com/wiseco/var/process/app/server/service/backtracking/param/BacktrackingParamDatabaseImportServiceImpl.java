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
package com.wiseco.var.process.app.server.service.backtracking.param;

import com.wiseco.var.process.app.server.controller.vo.DataModelTreeVo;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.OutsideParamImportServiceTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.dto.innerdata.TableJoinDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库方式
 * 从配置信息获取数据库中导入参数
 *
 * @author wuweikang
 */
@Service
@Slf4j
public class BacktrackingParamDatabaseImportServiceImpl implements BacktrackingParamImportService {
    @Resource
    private DbOperateService dbOperateService;

    @Override
    public OutsideParamImportServiceTypeEnum getType() {
        return OutsideParamImportServiceTypeEnum.DATABASE;
    }

    /**
     * importData
     *
     * @param dataGetTypeInfo dataGetTypeInfo
     * @param from            from
     * @param size            size
     * @return java.util.List
     */
    @Override
    public List<String> importDataByDb(BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo, int from, int size) {
        final BacktrackingSaveInputVO.DataBase dataBase = dataGetTypeInfo.getDataBase();
        log.info("通过本地数据库获取外部传入参数，取数信息:{}", dataBase);
        final List<BacktrackingSaveInputVO.TableJoinInfo> tableJoinInfoList = dataBase.getTableJoinInfoList();
        switch (dataBase.getInputFileType()) {
            case STRUCTURED:
                return getStructuredData(tableJoinInfoList, dataBase.getDataModelTree(), from, size);
            case JSON:
                return getJsonData(tableJoinInfoList, dataBase.getSelectColumns(), from, size);
            default:
                return Collections.emptyList();
        }
    }


    /**
     * getJsonData
     *
     * @param tableJoinInfoList 入参
     * @param selectColumns     入参
     * @param from from
     * @param size size
     * @return java.util.List<java.lang.String>
     */
    private List<String> getJsonData(List<BacktrackingSaveInputVO.TableJoinInfo> tableJoinInfoList, List<String> selectColumns, int from, int size) {
        //key:数据库字段（带别名） value:值
        List<Map<String, Object>> data = getData(tableJoinInfoList, from, size);
        return getJsonListOfWholeMapping(data, selectColumns);
    }

    /**
     * getStructuredData
     *
     * @param tableJoinInfoList 入参
     * @param dataModelTree     入参
     * @param from from
     * @param size size
     * @return java.util.List
     */
    private List<String> getStructuredData(List<BacktrackingSaveInputVO.TableJoinInfo> tableJoinInfoList, List<DataModelTreeVo> dataModelTree, int from, int size) {
        //key:数据库字段（带别名） value:值
        List<Map<String, Object>> data = getData(tableJoinInfoList, from, size);
        return getJsonListOfAttributeMapping(data,dataModelTree);
    }

    /**
     * getData
     *
     * @param tableJoinInfoList tableJoinInfoList
     * @param from  from
     * @param size size
     * @return java.util.List
     */
    private List<Map<String, Object>> getData(List<BacktrackingSaveInputVO.TableJoinInfo> tableJoinInfoList, int from, int size) {
        List<TableJoinDto> tableJoinList = tableJoinInfoList.stream().map(this::convert).collect(Collectors.toList());
        try {
            return dbOperateService.queryForListOfJoin(tableJoinList," ",from,size);
        } catch (Exception e) {
            log.error("querySourceDataDetail", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_DATABASE_ERROR, "预配数据异常");
        }
    }


    /**
     * convert
     *
     * @param tableJoinInfo tableJoinInfo
     * @return com.wiseco.var.process.app.server.service.dto.innerdata.TableJoinDto
     */
    private TableJoinDto convert(BacktrackingSaveInputVO.TableJoinInfo tableJoinInfo) {
        return TableJoinDto.builder().tableName(tableJoinInfo.getTableName()).alias(tableJoinInfo.getAlias()).joinType(tableJoinInfo.getJoinType())
                .joinField(tableJoinInfo.getJoinOnCondition()).build();
    }

}
