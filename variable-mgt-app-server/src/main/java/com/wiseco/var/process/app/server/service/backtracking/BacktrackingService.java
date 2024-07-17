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
package com.wiseco.var.process.app.server.service.backtracking;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.commons.util.MySqlTableValidatorUtil;
import com.wiseco.var.process.app.server.controller.vo.input.MultipartPreviewRespVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingPreviewInputVO;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.VarProcessBatchBacktrackingMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.service.DbOperateService;
import com.wiseco.var.process.app.server.service.dto.BacktrackingDetailDto;
import com.wiseco.var.process.app.server.service.dto.BacktrackingQueryDto;
import com.wiseco.var.process.app.server.service.dto.VariableUseVarPathDto;
import com.wiseco.var.process.app.server.service.dto.innerdata.TableJoinDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BacktrackingService extends ServiceImpl<VarProcessBatchBacktrackingMapper, VarProcessBatchBacktracking> {
    public static final int INT_100 = 100;

    @Autowired
    private VarProcessBatchBacktrackingMapper varBatchBacktrackingMapper;
    @Resource
    private DbOperateService dbOperateService;

    /**
     * findBacktrackingList
     *
     * @param page 入参
     * @param queryDto 入参
     * @return Ipage
     */
    public IPage<BacktrackingDetailDto> findBacktrackingList(IPage page, BacktrackingQueryDto queryDto) {
        return varBatchBacktrackingMapper.findBacktrackingList(page, queryDto);
    }

    /**
     * getBackTrackingState
     * @param manifestId 变量清单Id
     * @return 批量回溯的list
     */
    public List<VarProcessBatchBacktracking> getBackTrackingState(Long manifestId) {
        //获取使用该变量清单的批量回溯任务的state列表
        LambdaQueryWrapper<VarProcessBatchBacktracking> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(VarProcessBatchBacktracking::getId, VarProcessBatchBacktracking::getStatus)
                .eq(VarProcessBatchBacktracking::getManifestId, manifestId)
                .eq(VarProcessBatchBacktracking::getDeleteFlag, DeleteFlagEnum.USABLE.getCode());

        return varBatchBacktrackingMapper.selectList(queryWrapper);
    }

    /**
     * 本地数据库预览数据
     *
     * @param reqVO 预览传参
     * @return 数据预览信息
     */
    public MultipartPreviewRespVO previewData(BacktrackingPreviewInputVO reqVO) {
        String whereCondition = StringUtils.isEmpty(reqVO.getWhereCondition()) ? "" : reqVO.getWhereCondition();
        if (!MySqlTableValidatorUtil.validWhereCondition(whereCondition)) {
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_INPUT,"where条件不正确, 请重新输入!");
        }
        List<TableJoinDto> tableJoinList = reqVO.getTableJoinInfoList().stream().map(this::convert).collect(Collectors.toList());
        try {
            //总数
            Integer count = dbOperateService.countOfJoin(tableJoinList, whereCondition);
            //列表值
            List<Map<String, Object>> list = dbOperateService.queryForListOfJoin(tableJoinList, whereCondition, 0, INT_100);
            // 表头
            List<String> headers = !CollectionUtils.isEmpty(list) ? new ArrayList<>(list.get(0).keySet()) : new ArrayList<>();

            //组装出参
            MultipartPreviewRespVO.HeaderPreviewResult headerPreviewResult = MultipartPreviewRespVO.HeaderPreviewResult.builder().headers(headers).build();
            MultipartPreviewRespVO.DataPreviewResult dataPreviewResult = MultipartPreviewRespVO.DataPreviewResult.builder().totalCount(count).data(list).build();
            return MultipartPreviewRespVO.builder().headerPreviewResult(headerPreviewResult).dataPreviewResult(dataPreviewResult).build();
        } catch (Exception e) {
            log.error("预览数据发生错误: {}", e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.DATA_PREVIEW_ERROR,"预览数据发生错误");
        }
    }

    /**
     * convert
     *
     * @param tableJoinInfo
     * @return com.wiseco.var.process.app.server.service.dto.innerdata.TableJoinDto
     */
    private TableJoinDto convert(BacktrackingSaveInputVO.TableJoinInfo tableJoinInfo) {
        return TableJoinDto.builder().tableName(tableJoinInfo.getTableName()).alias(tableJoinInfo.getAlias()).joinType(tableJoinInfo.getJoinType()).joinField(tableJoinInfo.getJoinOnCondition()).build();
    }

    /**
     * 获取所有被批量回溯作为流水号使用的数据模型变量
     * @param spaceId 空间id
     * @return list
     */
    public List<VariableUseVarPathDto> getVarUseList(Long spaceId) {
        return varBatchBacktrackingMapper.getVarUseList(spaceId);
    }

    /**
     * 查询所有被使用的清单id
     * @param spaceId 空间id
     * @return set
     */
    public Set<Long> findUsedManifests(Long spaceId) {
        return varBatchBacktrackingMapper.findUsedManifests();
    }
}
