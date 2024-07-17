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
import java.util.List;
import java.util.Map;

/**
 * 监控对象映射Vo
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "监控对象映射Vo")
public class MonitorObjectMappingVo implements Serializable {

    private static final long serialVersionUID = 5114700087369567564L;

    @Schema(description = "报表分类为服务报表时赋值, 传入的内容是1个或者多个实时服务的Id")
    private List<Long> serviceIds;

    @Schema(description = "报表分类为单指标分析时赋值, 传入的内容是单个变量的Id和name")
    private ReportFormVariableMappingVo variableMappingVo;

    @Schema(description = "报表分类为指标对比分析时赋值, 传入的内容是多个变量的Id和name")
    private List<ReportFormVariableMappingVo> variableMappingVos;

    @Schema(description = "服务id：name map")
    private Map<Long,String> serviceIdNameMap;
}
