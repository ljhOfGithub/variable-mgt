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

import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BacktrackingQueryDto implements Serializable {
    private static final long serialVersionUID = 2498449735052716563L;

    private Long id;

    private String name;

    private String manifestName;

    private Long manifestId;

    private String variableSize;

    private BatchBacktrackingTriggerTypeEnum triggerType;

    private FlowStatusEnum status;

    private String deptCode;

    private String deptName;

    private String deleteFlag;

    private String description;

    private String taskStatus;

    private Date startTime;

    private String sortKey;

    private String sortType;

    private List<String> deptCodes;

    private List<String> userNames;
}
