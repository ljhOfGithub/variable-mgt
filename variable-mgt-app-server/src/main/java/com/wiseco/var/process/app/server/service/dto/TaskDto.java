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
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * @author xupei
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private String inputString;
    private String code;
    private String batchNo;
    private String serialNo;
    private String taskName;
    private Boolean enableTrace;
    private Map<String, String> outsideServiceStrategyMap;
}
