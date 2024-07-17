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
package com.wiseco.var.process.app.server.repository.mongodb.entiry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 变量加工Mongo日志记录
 * @author taodizhou
 * @date 2022/8/16 14:32.
 * Copyright 2022 Wiseco Tech, All Rights Reserved.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "var_process_log")
@CompoundIndex(name = "spaceId_1_resultStatus_1_requestTime_1", def = "{'spaceId': 1, 'resultStatus': 1, 'requestTime': -1}")
public class MongoVarProcessLog {

    /**
     * rest内部流水号
     */
    @Indexed(direction = IndexDirection.ASCENDING)
    private String restSerialNo;
    /**
     * 引擎生成流水号（引擎返回）
     */
    @Indexed(direction = IndexDirection.ASCENDING)
    private String engineSerialNo;
    /**
     * 业务进件流水号
     */
    @Indexed(direction = IndexDirection.ASCENDING)
    private String externalSerialNo;
    /**
     * 空间id
     */
    private String spaceId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 服务接口id
     */
    private String serviceId;
    /**
     * 服务接口名称
     */
    private String serviceName;


    /**
     * 服务接口id
     */
    private String interfaceId;
    /**
     * 服务接口版本
     */
    private String interfaceVersion;
    /**
     * 服务接口类型 实时批量
     */
    private String interfaceType;

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
     */
    private String rawData;

    /**
     * 异常信息
     */
    private String errorMessage;

    /**
     * 调用时间
     */
    private String requestTime;
    /**
     * 调用时长
     */
    private String responseLongTime;
    /**
     * 调用状态（成功、失败）
     */
    private String resultStatus;
    /**
     * 创建时间
     */
    private String createdTime;
}
