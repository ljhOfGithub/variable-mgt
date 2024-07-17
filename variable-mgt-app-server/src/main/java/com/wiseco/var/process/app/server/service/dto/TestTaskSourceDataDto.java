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
package com.wiseco.var.process.app.server.service.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 策略测试任务数据 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/4/2
 */
@Data
@Builder
public class TestTaskSourceDataDto {

    /**
     * 数据测试明细Id
     */
    private Integer dataId;
    /**
     * 测试数据集明细
     */
    private String inputJson;

    /**
     * 测试数据集明细
     */
    private String expectJson;

    /**
     * 流水号
     */
    private String serialNo;

    /**
     * 清单名称
     */
    private String callName;
}
