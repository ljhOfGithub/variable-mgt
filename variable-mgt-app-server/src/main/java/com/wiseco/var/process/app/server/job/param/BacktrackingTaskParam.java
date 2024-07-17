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
package com.wiseco.var.process.app.server.job.param;

import com.wiseco.var.process.app.server.commons.BaseWisecoJobParam;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktracking;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xu pei
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BacktrackingTaskParam extends BaseWisecoJobParam {

    private Long backtrackingId;

    private Long taskId;

    private Boolean isContinueExecute = false;

    private VarProcessBatchBacktracking backtracking;

    private VarProcessBatchBacktrackingTask taskInfo;
}
