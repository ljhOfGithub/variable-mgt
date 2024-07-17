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

import java.io.Serializable;

/**
 * 变量发布知识包 DTO
 * 
 * @author Zhaoxiong Chen
 * @since 2022/6/14
 */
public class VariableKnowledgeDto implements Serializable {

    private static final long serialVersionUID = -7126930279672416245L;

    /**
     * 变量空间 ID
     */
    private final Long spaceId;

    /**
     * 实时服务ID
     */
    private final Long serviceId;

    /**
     * 变量接口 ID
     */
    private final Long manifestId;

    /**
     * 上线变量内容索引
     * <p>
     * JSON 字符串
     * </p>
     */
    private final String variableIndex;

    /**
     * 上线变量 class 压缩包
     */
    private final byte[] variableClass;

    /**
     * VariableKnowledgeDto
     * 
     * @param spaceId 变量空间Id
     * @param serviceId 实时服务Id
     * @param manifestId 变量清单Id
     * @param variableIndex 变量索引
     * @param variableClass 变量类
     */
    public VariableKnowledgeDto(Long spaceId, Long serviceId, Long manifestId, String variableIndex,
        byte[] variableClass) {
        this.spaceId = spaceId;
        this.serviceId = serviceId;
        this.manifestId = manifestId;
        this.variableIndex = variableIndex;
        this.variableClass = variableClass;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getManifestId() {
        return manifestId;
    }

    public String getVariableIndex() {
        return variableIndex;
    }

    public byte[] getVariableClass() {
        return variableClass;
    }
}
