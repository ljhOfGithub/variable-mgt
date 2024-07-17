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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * TODO
 *
 * @author: zhouxiuxiu
 */
@Data
@Builder
@Schema(description = "组件JsonDTO")
@AllArgsConstructor
@NoArgsConstructor
public class BucketAndUseDto {
    /**
     * ID
     */
    private Long id;
    /**
     * useID
     */
    private Long useId;
    /**
     * 领域ID
     */
    private Long domainId;

    /**
     * 服务ID
     */
    private Long decisionId;

    /**
     * 决策细分/策略组名称
     */
    private String name;

    /**
     * 默认标识 0:非默认 1:默认
     */
    private Integer defaultFlag;

    /**
     * 删除标识 0:已删除 1:可用
     */
    private Integer deleteFlag;

    /**
     * 决策细分/策略组描述
     */
    private String description;

    /**
     * 创建用户
     */
    private String createdUser;

    /**
     * 更新用户
     */
    private String updatedUser;

    /**
     * 创建时间
     */
    private Timestamp createdTime;

    /**
     * 更新时间
     */
    private Timestamp updatedTime;

    /**
     * 状态 0:初始 1:启用 2:停用
     */
    private Integer useState;
}
