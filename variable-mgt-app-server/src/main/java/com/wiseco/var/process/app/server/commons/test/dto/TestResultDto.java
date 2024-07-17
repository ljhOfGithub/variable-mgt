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
package com.wiseco.var.process.app.server.commons.test.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TestResultDto {

    Integer dataId;
    String batchNo;
    String testSerialNo;
    String inputContent;
    String expectContent;

    String resultsContent;
    String originalContent;
    String comparisonContent;
    Integer executeStatus;

    Integer resultStatus;
    String exceptionMsg;
}
