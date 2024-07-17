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
package com.wiseco.var.process.app.server.service.multipleimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.config.MessageCondition;
import com.wiseco.var.process.app.server.repository.VarProcessTestVariableDataMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestVariableData;
import com.wiseco.var.process.app.server.repository.mongodb.entiry.MongoVarProcessTestVariableData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@MessageCondition.OnMysqlMessageEnabled
public class MysqlVarProcessTestVariableDataServiceImpl extends ServiceImpl<VarProcessTestVariableDataMapper, VarProcessTestVariableData> implements VarProcessTestVariableDataService {

    @Resource
    private VarProcessTestVariableDataMapper varProcessTestVariableDataMapper;

    @Override
    public MongoVarProcessTestVariableData findOneByTestDataId(Long testId, Integer dataId) {
        VarProcessTestVariableData varProcessTestVariableData = varProcessTestVariableDataMapper.selectOne(Wrappers.<com.wiseco.var.process.app.server.repository.entity.VarProcessTestVariableData>lambdaQuery()
                .eq(VarProcessTestVariableData::getTestId, testId)
                .eq(VarProcessTestVariableData::getDataId, dataId));
        if (varProcessTestVariableData == null) {
            return null;
        }
        return EntityConverter.convertEntity(varProcessTestVariableData, MongoVarProcessTestVariableData.class);
    }

    @Override
    public void saveOne(Long testId, Integer dataId, String inputContent, String expectContent) {
        VarProcessTestVariableData testData = VarProcessTestVariableData.builder().testId(testId).dataId(dataId)
                .inputContent(inputContent).expectContent(expectContent).build();
        varProcessTestVariableDataMapper.insert(testData);
    }

    @Override
    public void saveBatch(List<MongoVarProcessTestVariableData> mongoVarProcessTestVariableDataList) {
        List<VarProcessTestVariableData> list = mongoVarProcessTestVariableDataList.stream().map(item -> EntityConverter.convertEntity(item,VarProcessTestVariableData.class))
                .collect(Collectors.toList());
        super.saveBatch(list);
    }

    @Override
    public List<MongoVarProcessTestVariableData> findAllByTestId(Long testId) {
        List<VarProcessTestVariableData> varProcessTestVariableData = varProcessTestVariableDataMapper.selectList(Wrappers.<VarProcessTestVariableData>lambdaQuery()
                .eq(VarProcessTestVariableData::getTestId, testId));
        return varProcessTestVariableData.stream().map(item -> EntityConverter.convertEntity(item, MongoVarProcessTestVariableData.class)).collect(Collectors.toList());
    }

    @Override
    public List<MongoVarProcessTestVariableData> findPageByTestId(Long testId, Integer pageIndex, Integer pageSize) {
        LambdaQueryWrapper<VarProcessTestVariableData> queryWrapper = Wrappers.<VarProcessTestVariableData>lambdaQuery()
                .eq(VarProcessTestVariableData::getTestId, testId)
                .orderByDesc(VarProcessTestVariableData::getTestId);
        Page<VarProcessTestVariableData> varProcessTestVariableDataPage = varProcessTestVariableDataMapper.selectPage(new Page<>(pageIndex + MagicNumbers.ONE, pageSize), queryWrapper);
        return varProcessTestVariableDataPage.getRecords().stream().map(item -> EntityConverter.convertEntity(item, MongoVarProcessTestVariableData.class)).collect(Collectors.toList());
    }

    @Override
    public void deleteOneByTestDataId(Long testId, Integer dataId) {
        varProcessTestVariableDataMapper.delete(Wrappers.<VarProcessTestVariableData>lambdaQuery()
                .eq(VarProcessTestVariableData::getTestId,testId)
                .eq(VarProcessTestVariableData::getDataId,dataId));
    }

    @Override
    public void updateOneByTestDataId(Long testId, Integer dataId, String inputContent, String expectContent) {
        VarProcessTestVariableData updateEntity = VarProcessTestVariableData.builder().testId(testId).dataId(dataId).inputContent(inputContent).expectContent(expectContent).build();
        varProcessTestVariableDataMapper.update(updateEntity,Wrappers.<VarProcessTestVariableData>lambdaUpdate()
                .eq(VarProcessTestVariableData::getTestId,testId)
                .eq(VarProcessTestVariableData::getDataId,dataId));
    }

    @Override
    public Integer findMaxDataIdByTestId(Long testId) {
        return varProcessTestVariableDataMapper.findMaxDataIdByTestId(testId);
    }

    @Override
    public void duplicateDataSetByTestId(Long testId, Long newTestId) {
        // 获取测试数据集的全部数据明细
        List<VarProcessTestVariableData> varProcessTestVariableDataList = varProcessTestVariableDataMapper.selectList(Wrappers.<VarProcessTestVariableData>lambdaQuery()
                .eq(VarProcessTestVariableData::getTestId,testId));
        // 遍历待复制的测试数据集
        varProcessTestVariableDataList.forEach(testVariableData -> {
            // 擦除 id
            testVariableData.setId(null);
            // 设置新测试数据集 ID
            testVariableData.setTestId(newTestId);
        });
        // 再次保存全部数据
        super.saveBatch(varProcessTestVariableDataList);
    }
}
