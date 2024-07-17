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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 实时服务索引 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariableServiceIndexDto implements Serializable {

    private static final long serialVersionUID = -4417273369280601887L;

    /**
     * 变量空间编码
     */
    private String spaceCode;

    /**
     * 变量空间数据模型是否存在输入数据
     */
    private Boolean input;

    /**
     * 变量空间数据模型是否存在内部数据
     */
    private Boolean internalData;

    /**
     * 内存模式是map
     */
    private String dataMode;

    /**
     * 变量空间服务列表
     */
    private List<Service> services;

    /**
     * 实时服务索引 - 服务
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Service implements Serializable {

        private static final long serialVersionUID = -2091516595091903707L;

        /**
         * 服务编码
         */
        private String serviceCode;

        /**
         * 服务使用的变量
         */
        private List<Variable> vars;
    }

    /**
     * 实时服务索引 - 服务 - 变量
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Variable implements Serializable {

        private static final long serialVersionUID = 8461513626934627749L;

        /**
         * 变量清单 ID
         */
        private Long manifestId;

        /**
         * 服务涉及的所有变量标识符
         * <p>ONLY CONTAINS "entryVarIdentifiers"</p>
         */
        private List<String> varIdentifiers;

        /**
         * 输出变量标识符
         */
        private List<String> entryVarIdentifiers;
    }
}
