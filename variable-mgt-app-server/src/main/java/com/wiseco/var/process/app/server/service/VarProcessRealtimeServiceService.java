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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.controller.vo.output.RestServiceListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VarSimpleServiceOutputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.service.dto.ServiceQueryDto;

import java.util.List;

/**
 * 变量服务表 服务类
 */
public interface VarProcessRealtimeServiceService extends IService<VarProcessRealtimeService> {

    /**
     * 查询服务基本信息
     * @param resultVoPage page
     * @param serviceQueryDto 入参dto
     * @return page
     */
    Page<VarProcessRealtimeService> findServiceBasicInfo(Page<RestServiceListOutputVO> resultVoPage, ServiceQueryDto serviceQueryDto);

    /**
     * 查询简单服务列表
     * @param page 分页
     * @param keyWord 查询
     * @param excludeCodes excludeCodes
     * @param deptCodes 部门codes
     * @param userNames 用户名称集合
     * @return page
     */
    Page<VarSimpleServiceOutputDto> findSimpleUpServiceList(Page page, String keyWord, List<String> excludeCodes,List<String> deptCodes,List<String> userNames);


}
