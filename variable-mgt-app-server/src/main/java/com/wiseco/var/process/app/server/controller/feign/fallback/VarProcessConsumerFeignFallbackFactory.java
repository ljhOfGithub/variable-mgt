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
package com.wiseco.var.process.app.server.controller.feign.fallback;

import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.controller.feign.VarProcessConsumerFeign;
import com.wiseco.var.process.app.server.controller.feign.dto.CreateTabDto;
import com.wiseco.var.process.app.server.controller.feign.dto.DownLoadDataDto;
import com.wiseco.var.process.app.server.controller.vo.input.DataViewInputDto;
import com.wiseco.var.process.app.server.controller.vo.input.VariableResultListQueryInputDto;
import com.wiseco.var.process.app.server.controller.vo.output.DataViewOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.TableFieldVO;
import com.wiseco.var.process.app.server.controller.vo.output.VariableResultDatagramQueryOutputDto;
import com.wiseco.var.process.app.server.controller.vo.output.VariableResultListQueryOutputDto;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.service.dto.PagedQueryResult;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class VarProcessConsumerFeignFallbackFactory implements FallbackFactory<VarProcessConsumerFeign> {
    @Override
    public VarProcessConsumerFeign create(Throwable throwable) {
        return new VarProcessConsumerFeign() {
            @Override
            public APIResult<PagedQueryResult<VariableResultListQueryOutputDto>> findRestCallRecordList(VariableResultListQueryInputDto inputDto) {
                log.error(throwable.getMessage());
                log.error("findRestCallRecordList 方法调用异常");
                return APIResult.fail("findRestCallRecordList 方法调用异常");
            }

            @Override
            public APIResult<VariableResultDatagramQueryOutputDto> getRestCallRecordMessage(String engineSerialNo) {
                log.error(throwable.getMessage());
                log.error("getRestCallRecordMessage 方法调用异常");
                return APIResult.fail("getRestCallRecordMessage 方法调用异常");
            }

            @Override
            public APIResult<String> createVarTable(CreateTabDto createTabDto) {
                log.error(throwable.getMessage());
                log.error("createVarTable 方法调用异常");
                return APIResult.fail("创建数据库表失败 请联系管理员");
            }

            @Override
            public APIResult<DataViewOutputDto> getDataView(DataViewInputDto dataViewInputDto) {
                log.error(throwable.getMessage());
                log.error("getDataView 方法调用异常");
                return APIResult.fail("getDataView 方法调用异常");
            }

            @Override
            public Map<String, String> getRestServices() {
                log.error(throwable.getMessage());
                log.error("getRestServices 方法调用异常");
                return new HashMap<>(MagicNumbers.EIGHT);
            }

            @Override
            public List<TableFieldVO> getFiledType(String callList) {
                log.error(throwable.getMessage());
                log.error("getFiledType 方法调用异常");
                return new ArrayList<>();
            }

            @Override
            public List<TableFieldVO> findTabField(Long manifestId, Integer condition) {
                log.error(throwable.getMessage());
                log.error("findTabField 方法调用异常");
                return new ArrayList<>();
            }

            @Override
            public DownLoadDataDto fetchDownLoadData(DataViewInputDto dataViewInputDto) {
                log.error(throwable.getMessage());
                log.error("fetchDownLoadData 方法调用异常");
                throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_ENUM_ERROR, "获取下载数据异常");
            }
        };
    }
}
