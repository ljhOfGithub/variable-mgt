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

import lombok.Data;

/**
 * 报文实体
 */
@Data
public class VarProcessLogMsgDto {
    /**
     * request json报文
     */
    private String requestJson;
    /**
     * response json报文
     */
    private String responseJson;
    /**
     * request扩展后的 json报文
     * 引擎使用变量
     */
    private String rawData;

    /**
     * 异常信息
     */
    private String errorMessage;

    /**
     * 服务接口id
     */
    private String interfaceId;
}
