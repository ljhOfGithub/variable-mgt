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

import java.io.Serializable;
import java.util.Date;

/**
 * @author: fudengkui
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlazeServiceAvailableReferenceDTO implements Serializable {

    /**
     * blaze决策服务ID
     */
    private Long decisionId;

    /**
     * 引入ID
     */
    private Long refId;

    /**
     * 模块名称
     */
    private String name;

    /**
     * 模块编码
     */
    private String code;

    /**
     * 服务状态
     */
    private Integer status;

    /**
     * 引入状态
     */
    private Integer refStatus;

    /**
     * 首次引入时间
     */
    private Date refTime;

}
