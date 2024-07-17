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

import com.wiseco.var.process.app.server.enums.BacktrackingTaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * task表
 * </p>
 *
 * @author wiseco
 * @since 2023-08-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BacktrackingTaskDto {
    private Long id;

    private Long backtrackingId;

    private String startTime;

    private BacktrackingTaskStatusEnum status;

    private String code;

    private String endTime;

    private String completion;

    private String success;

    private Integer maximumResponseTime;

    private Integer minimumResponseTime;

    private Integer averageResponseTime;

    private String requestInfo;

    private String responseInfo;

    private String exceptionInfo;

    private String engineInfo;

    private String createdUser;

    private String errorMessage;
}
