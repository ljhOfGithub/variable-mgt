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

import java.util.Date;

/**
 * 实时服务和接口信息
 *
 * @author wangxianli
 * @since 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarProcessServiceManifestDto {

    /**
     * 服务 ID
     */
    private Long serviceId;

    /**
     * 服务编码
     */
    private String code;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务类型 1:实时 2:批量
     */
    private Integer type;

    /**
     * 变量清单 ID
     */
    private Long manifestId;

    /**
     * 接口版本
     */
    private String version;

    /**
     * 接口版本描述
     */
    private String description;

    /**
     * 选择发布的变量 JSON Schema 快照
     */
    private String schemaSnapshot;

    /**
     * 接口状态
     */
    private Integer state;

    /**
     * 创建人
     */
    private String createdUser;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 最后编辑人
     */
    private String updatedUser;

    /**
     * 编辑时间
     */
    private Date updatedTime;
}
