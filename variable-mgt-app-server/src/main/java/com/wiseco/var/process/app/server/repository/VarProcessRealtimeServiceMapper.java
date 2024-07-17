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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiseco.var.process.app.server.controller.vo.output.RestServiceListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VarSimpleServiceOutputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.service.dto.ServiceQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 变量服务表 Mapper 接口
 */
public interface VarProcessRealtimeServiceMapper extends BaseMapper<VarProcessRealtimeService> {

    /**
     * 查询服务基本信息
     * @param page page
     * @param queryDto 入参dto
     * @return page
     */
    Page<VarProcessRealtimeService> findServiceBasicInfo(Page<RestServiceListOutputVO> page, @Param("queryDto") ServiceQueryDto queryDto);

    /**
     * 查询服务基本信息-分页计数
     * @param page page
     * @param queryDto 入参dto
     * @return Integer
     */
    Integer findServiceBasicInfoCount(Page<RestServiceListOutputVO> page, @Param("queryDto") ServiceQueryDto queryDto);


    /**
     * 查询服务简单信息list
     * @param page 分页
     * @param keyWord 查询关键字
     * @param excludeCodes excludeCodes
     * @param deptCodes 部门codes
     * @param userNames 用户名称集合
     * @return page
     */
    Page<VarSimpleServiceOutputDto> findSimpleUpServiceList(Page page, @Param("keyWord") String keyWord, @Param("excludeCodes") List<String> excludeCodes,
                                                            @Param("deptCodes")List<String> deptCodes,@Param("userNames") List<String> userNames);
}
