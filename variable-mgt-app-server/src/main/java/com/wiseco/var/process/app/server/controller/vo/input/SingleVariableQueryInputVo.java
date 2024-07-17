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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 变量查询Vo(用于单指标分析和指标对比分析报表选择监控对象, 属于公共服务的一个实体类)
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "变量查询Vo(用于单指标分析和指标对比分析报表选择监控对象, 属于公共服务的一个实体类)")
public class SingleVariableQueryInputVo implements Serializable {

    private static final long serialVersionUID = -3104290611416215193L;

    @Schema(description = "变量分类的Id, 可以为空", example = "10000")
    private Long categoryId;

    @Schema(description = "数据类型, 取值为string、int、double、date、datetime、boolean, 可以为空", example = "int")
    private String dataType;

    @Schema(description = "标签的Id, 可以为空", example = "10000")
    private Long tagId;

    @Schema(description = "标签组的Id, 可以为空", example = "20000")
    private Long groupId;

    @Schema(description = "部门Id, 可以为空", example = "1")
    private Long deptId;

    @Schema(description = "变量名称/编码, 可以为空", example = "variableCode")
    private String keywords;

    @Schema(description = "排序字段, label_asc——名称升序, label_desc——名称降序, code_asc——编码升序,"
            + " code_desc——编码降序, 如果不填，后端默认名称升序", example = "label_asc")
    private String order;
}
