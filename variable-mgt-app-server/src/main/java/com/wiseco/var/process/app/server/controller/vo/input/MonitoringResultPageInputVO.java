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
package com.wiseco.var.process.app.server.controller.vo.input;

import com.wiseco.boot.data.PageDTO;
import com.wiseco.var.process.app.server.enums.AlertGradeEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTargetEnum;
import com.wiseco.var.process.app.server.enums.MonitoringConfTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author wangxiansheng
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "监控告警结果分页输入参数")
public class MonitoringResultPageInputVO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;


    @Schema(description = "监控类型", required = true)
    @NotNull(message = "监控类型不能为空")
    private MonitoringConfTypeEnum monitoringType;


    /**
     *服务名称
     */
    @Schema(description = "服务名称")
    private String serviceName;

    /**
     *服务版本号
     */
    @Schema(description = "版本号")
    private String serviceVersion;

    /**
     *监控指标
     */
    @Schema(description = "监控指标")
    private MonitoringConfTargetEnum monitoringTarget;

    /**
     *告警等级
     */
    @Schema(description = "告警等级")
    private AlertGradeEnum alertGrade;

    /**
     *告警类型
     */
    @Schema(description = "告警类型")
    private Integer messageType;


    /**
     *变量清单
     */
    @Schema(description = "变量清单")
    private String manifestName;

    /**
     *模糊查询字段
     */
    @Schema(description = "模糊查询字段")
    private String fuzzyField;

    /**
     *排序相关字段
     */
    @Schema(description = "排序相关字段字段和升序或降序")
    private String order;

    @Schema(description = "order拆分成两个字段sortedKey和sortMethod")
    private String sortedKey;

    /**
     * 正序/倒序
     */
    @Schema(description = "order拆分成两个字段sortedKey和sortMethod")
    private String sortMethod;

    /**
     * 部门code集合
     */
    @Schema(description = "部门code集合")
    private List<String> deptCodes;

    /**
     * 用户名称集合
     */
    @Schema(description = "用户名称集合")
    private List<String> userNames;
}
