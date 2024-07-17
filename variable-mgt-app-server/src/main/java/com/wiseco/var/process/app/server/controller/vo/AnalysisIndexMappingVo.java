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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Schema(description = "统计分析 分析指标配置vo")
public class AnalysisIndexMappingVo implements Serializable {
    private static final long serialVersionUID = 8759865846955173880L;

    /**
     * 来源 0实时服务 1内部数据表
     */
    private boolean iv;

    private boolean psi;

    private boolean zeroRatio;

    private boolean uniqueNum;

    private boolean percentage;

    private boolean missingRatio;

    private boolean specialRatio;
}
