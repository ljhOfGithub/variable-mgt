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
package com.wiseco.var.process.app.server.controller.vo;

import com.wiseco.var.process.app.server.enums.StreamProcessCalFunctionEnum;
import com.wiseco.var.process.app.server.enums.StreamProcessTemplateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SceneListSimpleOutputVO {
    private Long sceneId;
    private String sceneName;
    private String dataModelName;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventOutputDto {
        private Long eventId;
        private String eventName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProcessTemplateOutputDto {
        private StreamProcessTemplateEnum processTemplate;
        private String processTemplateDesc;

        public String getProcessTemplateDesc() {
            return processTemplate.getDesc();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CalculateFunctionOutputDto {
        private StreamProcessCalFunctionEnum calculateFunction;
        private String calculateFunctionDesc;

        public String getCalculateFunctionDesc() {
            return calculateFunction.getDesc();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MatchDimensionOutputDto {
        private String matchDimension;
        private String matchDimensionLabel;
    }
}
