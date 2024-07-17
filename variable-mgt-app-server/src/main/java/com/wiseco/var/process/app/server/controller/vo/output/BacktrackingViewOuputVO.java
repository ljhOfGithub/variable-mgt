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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiseco.var.process.app.server.controller.vo.DataModelTreeVo;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingOutputFile;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.BacktrackingDataSourceTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingDataTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingFileImportTypeEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.CharsetType;
import com.wiseco.var.process.app.server.enums.DateFromType;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.OutputType;
import com.wiseco.var.process.app.server.enums.BacktrackingOutsideCallStrategyEnum;
import com.wiseco.var.process.app.server.enums.TableJoinType;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import com.wiseco.var.process.app.server.service.dto.innerdata.TaskInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.Serializable;
import java.util.List;

@Schema(description = "批量回溯查看返回DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BacktrackingViewOuputVO implements Serializable {

    @Schema(description = "任务ID：有值（修改数据），null（新增）", example = "1")
    private Long id;

    @Schema(description = "任务名称", required = true, example = "abc")
    @NotEmpty(message = "任务名称不能为空")
    private String name;

    @Schema(description = "变量清单ID", required = true)
    @NotEmpty(message = "变量清单ID不能为空")
    private Long manifestId;

    @Schema(description = "变量清单名称", required = true)
    @NotEmpty(message = "变量清单名称不能为空")
    private String manifestName;

    @Schema(description = "触发方式")
    private BatchBacktrackingTriggerTypeEnum triggerType;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "依赖的数据模型信息")
    private List<DataModelInfo> dataModelInfoList;

    @Schema(description = "主体绑定唯一标识", required = true, example = "abc")
    @NotEmpty(message = "主体唯一标识不能为空")
    private String serialNo;

    @Schema(description = "结果输出信息（文件）")
    private BacktrackingOutputFile outputInfoFile;

    @Schema(description = "结果输出信息（数据库）")
    @JsonProperty("outputInfoDB")
    private BacktrackingSaveInputVO.BacktrackingOutputDb outputInfoDb;

    @Schema(description = "输出类型")
    @NotNull(message = "请选择输出类型")
    private OutputType outputType;

    @Schema(description = "任务信息")
    private TaskInfoDto taskInfo;

    @Schema(description = "批量回溯流程状态")
    private FlowStatusEnum status;

    @Schema(description = "取值方式")
    private DataGetTypeInfo dataGetTypeInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "数据模型信息")
    public static class DataModelInfo implements Serializable {

        @Schema(description = "批量回溯-数据模型映射表ID", required = true, example = "1")
        @NotNull(message = "批量回溯-数据模型映射表ID不能为空")
        private Long id;

        @Schema(description = "对象名")
        private String name;

        @Schema(description = "对象中文名")
        private String nameCn;

        @Schema(description = "对象来源")
        private VarProcessDataModelSourceType sourceType;

        @Schema(description = "版本号")
        private Long versionId;

        @Schema(description = "外数取值方式")
        private BacktrackingOutsideCallStrategyEnum outsideServiceStrategy;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "取值方式")
    public static class DataGetTypeInfo implements Serializable {
        @Schema(description = "数据来源")
        private BacktrackingDataSourceTypeEnum dataSourceType;

        @Schema(description = "数据文件的方式填写")
        private DataFile dataFile;

        @Schema(description = "本地数据库的方式填写")
        private DataBase dataBase;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "数据文件的方式填写")
    public static class DataFile implements Serializable {
        @Schema(description = "添加方式")
        private BacktrackingFileImportTypeEnum dataSourceType;

        @Schema(description = "文件服务器")
        // TODO 待底座支持
        private String ftpServer;

        @Schema(description = "数据文件")
        private File localFile;

        @Schema(description = "数据格式")
        private BacktrackingDataTypeEnum inputFileType = BacktrackingDataTypeEnum.JSON;

        @Schema(description = "编码格式")
        private CharsetType charsetType = CharsetType.UTF_8;

        @Schema(description = "数据起始行")
        private int startLine = 1;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "数据文件的方式-触发方式为“定时”填写")
    public static class DataFileScheduled implements Serializable {
        @Schema(description = "文件服务器")
        // TODO 待底座支持
        private String ftpServer;

        @Schema(description = "文件目录")
        private String filePath;

        @Schema(description = "文件名称")
        private String fileName;

        @Schema(description = "成功文件")
        private String okFile;

        @Schema(description = "yyyyMMdd取值")
        private DateFromType dateFrom;

        @Schema(description = "数据格式")
        private BacktrackingDataTypeEnum inputFileType = BacktrackingDataTypeEnum.JSON;

        @Schema(description = "编码格式")
        private CharsetType charsetType = CharsetType.UTF_8;

        @Schema(description = "数据起始行")
        private int startLine = 1;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "本地数据库的方式填写")
    public static class DataBase implements Serializable {
        @Schema(description = "数据格式")
        private BacktrackingDataTypeEnum inputFileType;

        @Schema(description = "关联表")
        private List<BacktrackingSaveInputVO.TableJoinInfo> tableJoinInfoList;

        @Schema(description = "数据对象映射-结构化数据时显示此项")
        private DataModelTreeVo whereCondition;

        @Schema(description = "数据对象映射-报文数据时显示此项")
        private List<String> selectColumns;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "关联表")
    public static class TableJoinInfo implements Serializable {
        @Schema(description = "源表类型")
        private TableJoinType tableInputType;

        @Schema(description = "表名")
        private String tableName;

        @Schema(description = "别名")
        private String alias;

        @Schema(description = "关联类型")
        private TableJoinType joinType;

        @Schema(description = "关联关系")
        private String joinOnCondition;
    }

}
