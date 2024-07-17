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
package com.wiseco.var.process.app.server.controller.vo.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author: wangxianli
 */
@Schema(description = "测试数据集DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TestCollectOutputDto implements Serializable {
    private static final long serialVersionUID = 8668690652316747092L;

    @Schema(description = "测试数据集ID", example = "1")
    private Long id;

    @Schema(description = "变量中文名/函数名称", example = "1")
    private String sourceTestName;

    @Schema(description = "测试集名称", example = "测试")
    private String name;

    @Schema(description = "备注", example = "无")
    private String remark;

    @Schema(description = "来源", example = "在线自动生成")
    private String source;

    @Schema(description = "测试执行版本", example = "1")
    private String changeNum;

    @Schema(description = "测试数据记录数", example = "1")
    private Integer dataCount;

    /**
     * 测试执行正常数
     */
    @Schema(description = "测试执行正常数", example = "1")
    private Integer executeNormalCount;

    /**
     * 测试执行异常数
     */
    @Schema(description = "测试执行异常数", example = "1")
    private Integer executeExceptionCount;

    /**
     * 测试预期结果一致数
     */
    @Schema(description = "测试预期结果一致数", example = "1")
    private Integer executeResulteqCount;

    /**
     * 测试预期结果不一致数
     */
    @Schema(description = "测试预期结果不一致数", example = "1")
    private Integer executeResultneqCount;

    @Schema(description = "测试成功率", example = "1")
    private String successRate;

    @Schema(description = "测试执行耗时", example = "1")
    private Long executeTime;

    @Schema(description = "创建人", example = "张三")
    private String createdUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后修改时间", example = "2021-10-12 15:00:00")
    private Timestamp updatedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后测试时间", example = "2021-10-12 15:00:00")
    private Timestamp testTime;

}
