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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.controller.vo.output.RestServiceListOutputVO;
import com.wiseco.var.process.app.server.controller.vo.output.VarSimpleServiceOutputDto;
import com.wiseco.var.process.app.server.repository.VarProcessRealtimeServiceMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessRealtimeService;
import com.wiseco.var.process.app.server.service.VarProcessRealtimeServiceService;
import com.wiseco.var.process.app.server.service.dto.ServiceQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 变量服务表 服务实现类
 */
@Service
public class VarVarProcessRealtimeServiceServiceImpl extends ServiceImpl<VarProcessRealtimeServiceMapper, VarProcessRealtimeService> implements VarProcessRealtimeServiceService {

    @Autowired
    private VarProcessRealtimeServiceMapper varProcessRealtimeServiceMapper;

    @Override
    public Page<VarProcessRealtimeService> findServiceBasicInfo(Page<RestServiceListOutputVO> resultVoPage, ServiceQueryDto serviceQueryDto) {
        return varProcessRealtimeServiceMapper.findServiceBasicInfo(resultVoPage,serviceQueryDto);
    }

    @Override
    public Page<VarSimpleServiceOutputDto> findSimpleUpServiceList(Page page, String keyWord, List<String> excludeCodes,List<String> deptCodes,List<String> userNames) {
        return varProcessRealtimeServiceMapper.findSimpleUpServiceList(page,keyWord,excludeCodes,deptCodes,userNames);
    }
}
