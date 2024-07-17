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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 变量-公共函数使用情况 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableFunctionUsageDto {

    /**
     * 公共函数 ID
     */
    private Long functionId;

    /**
     * 公共函数标识
     */
    private String functionIdentifier;

    /**
     * 公共函数名称
     */
    private String functionName;

    /**
     * 公共函数类型
     */
    private Integer functionType;

    /**
     * 公共函数状态
     */
    private Integer functionStatus;

    /**
     * 路径
     */
    private String varPath;

    /**
     * 名称
     */
    private String varName;

    /**
     * 数据类型
     */
    private String varType;

    /**
     * 是否数组
     */
    private Integer isArray;

    /**
     * 读写操作记录
     */
    private String actionHistory;
}
