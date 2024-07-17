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
package com.wiseco.var.process.app.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTest;
import com.wiseco.var.process.app.server.service.dto.TestCollectAndResultsDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 变量测试数据集 Mapper 接口
 * </p>
 *
 * @author wiseco
 * @since 2022-06-09
 */
public interface VarProcessTestMapper extends BaseMapper<VarProcessTest> {

    /**
     * 分页查询测试数据集
     *
     * @param page       MyBatis-Plus 分页对象
     * @param spaceId    空间id
     * @param testType   测试类型
     * @param variableId 查询测试数据集的变量 ID
     * @param identifier 查询测试数据集的组件编号
     * @return MyBatis-Plus 测试集返回数据 分页查询结果
     */
    @Select("SELECT ts.id as id, ts.name, ts.remark, ts.source, ts.data_count,\n"
            + "ts.updated_user as createdUser, ts.updated_time as updatedTime,\n" + "tsr.id as resultId, tsr.change_num, tsr.test_time,\n"
            + "tsr.execute_time, tsr.success_rate, tsr.updated_user as updatedUser \n" + "from var_process_test ts \n"
            + "LEFT JOIN var_process_test_results tsr ON ts.id = tsr.test_id \n AND tsr.variable_id = #{variableId} " + "WHERE ts.test_type = #{testType} \n"
            + "and ts.var_process_space_id = #{spaceId} and ts.delete_flag='1' and ts.identifier = #{identifier} order by ts.id desc")
    IPage<TestCollectAndResultsDto> findPageByVariableIdAndIdentifier(IPage<TestCollectAndResultsDto> page, @Param("spaceId") Long spaceId,
                                                                      @Param("testType") Integer testType, @Param("variableId") Long variableId,
                                                                      @Param("identifier") String identifier);

    /**
     * 查找最大标识符
     *
     * @param spaceId    空间id
     * @param identifier 编号
     * @return 最大序号
     */
    @Select("SELECT max(seq_no) from var_process_test WHERE var_process_space_id = #{spaceId} and identifier = #{identifier}")
    Integer findMaxSeqNoByIdentifier(@Param("spaceId") Long spaceId, @Param("identifier") String identifier);

    /**
     * 获取测试清单
     *
     * @return 变量id List
     */
    @Select("select\n" + "\t\ttest.variable_id as manifest_id\n" + "\tfrom\n" + "\t\tvar_process_manifest vpm2\n"
            + "\tinner join var_process_test test on\n" + "\t\ttest.variable_id = vpm2.id\n" + "\t\tand test.delete_flag = 1 and test.test_type = 3\n"
            + "\tinner join var_process_test_results test_result on\n" + "\t\ttest.id = test_result.test_id\n" + "\tgroup by\n"
            + "\t\ttest.variable_id")
    List<Long> getTestedManifests();

    /**
     * 通过测试流水号，获取对应的组件类型(1——变量；2——公共方法+数据预处理+变量模板；3——变量清单)
     * @param serialNo 流水号
     * @return 组件类型
     */
    @Select("SELECT\n"
            + "\tvpt.test_type\n"
            + "FROM\n"
            + "\tvar_process_test AS vpt\n"
            + "WHERE\n"
            + "\tvpt.id = (\n"
            + "\tSELECT\n"
            + "\t\tvptvr.test_id\n"
            + "\tFROM\n"
            + "\t\tvar_process_test_variable_result AS vptvr\n"
            + "\tWHERE\n"
            + "\t\tvptvr.test_serial_no = #{serialNo});")
    Integer getTestTypeBySerialNo(@Param("serialNo") String serialNo);
}
