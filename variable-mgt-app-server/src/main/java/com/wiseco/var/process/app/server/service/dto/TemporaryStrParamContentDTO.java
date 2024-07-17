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

/**
 * str_param_batch_content
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryStrParamContentDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主鍵id
     */
    private Long id;

    /**
     * 关联id
     */
    private Integer refId;

    /**
     * 排序序号
     */
    private Integer sortNum;

    /**
     * 参数值
     */
    private String parameters;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建用户
     */
    private String createdUser;

    /**
     * 更新用户
     */
    private String updatedUser;

}
