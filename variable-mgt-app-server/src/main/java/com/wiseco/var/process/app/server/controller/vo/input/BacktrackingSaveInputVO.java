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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiseco.var.process.app.server.commons.enums.JobExecuteFrequency;
import com.wiseco.var.process.app.server.commons.enums.JoinEnum;
import com.wiseco.var.process.app.server.commons.enums.TimeUnit;
import com.wiseco.var.process.app.server.controller.vo.DataModelTreeVo;
import com.wiseco.var.process.app.server.enums.BacktrackingDataSourceTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingDataTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingFileImportTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingFileSpiltCharEnum;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.CharsetType;
import com.wiseco.var.process.app.server.enums.DateFromType;
import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.OutputType;
import com.wiseco.var.process.app.server.enums.BacktrackingOutsideCallStrategyEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Schema(description = "批量回溯保存DTO")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BacktrackingSaveInputVO implements Serializable {

    @Schema(description = "任务ID：有值（修改数据），null（新增）", example = "1")
    private Long id;

    @Schema(description = "任务名称", required = true, example = "abc")
    @NotEmpty(message = "任务名称不能为空")
    private String name;

    @Schema(description = "变量清单ID", required = true)
    private Long manifestId;

    @Schema(description = "触发方式")
    private BatchBacktrackingTriggerTypeEnum triggerType;

    @Schema(description = "开启trace")
    private Boolean enableTrace;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "依赖的数据模型信息")
    private List<DataModelInfo> dataModelInfoList;

    @Schema(description = "主体绑定唯一标识", required = true, example = "abc")
    @NotEmpty(message = "主体唯一标识不能为空")
    private String serialNo;

    @Schema(description = "输出类型")
    @NotNull(message = "请选择输出类型")
    private OutputType outputType;

    @Schema(description = "结果输出信息（文件）")
    private BacktrackingOutputFile outputInfoFile;

    @Schema(description = "结果输出信息（数据库）")
    @JsonProperty("outputInfoDB")
    private BacktrackingOutputDb outputInfoDb;

    @Schema(description = "批量回溯流程状态")
    private FlowStatusEnum status;

    @Schema(description = "取值方式")
    private DataGetTypeInfo dataGetTypeInfo;

    @Schema(description = "定时任务信息")
    private TaskInfo taskInfo;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "定时任务信息")
    public static class TaskInfo implements Serializable {
        @Schema(description = "执行频率 每日：EVERY_DAY  每月：EVERY_MONTH  固定：TARGET  cron：CRON")
        private JobExecuteFrequency executionFrequency;

        @Schema(description = "每月执行日，执行频率为月时必填")
        private String dayInMonth;

        @Schema(description = "执行日期，执行频率为固定日期时必填")
        private String executeData;

        @Schema(description = "执行时间，执行频率为非CRON时必填")
        private String executeTime;

        @Schema(description = "CRON表达式，执行频率为cron时必填")
        private String cron;

        @Schema(description = "失败是否重试：0不重试，1重试")
        private Integer isRetry;

        @Schema(description = "失败重试次数,  非重试任务传null")
        private Integer retryCount;

        @Schema(description = "重试时间间隔, 非重试任务传null")
        private Integer retryInterval;

        @Schema(description = "重试时间间隔单位, 非重试任务传null")
        private TimeUnit retryIntervalUnit;
    }

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

        @Schema(description = "数据文件的方式填写（人工）")
        private DataFile dataFile;

        @Schema(description = "数据文件的方式填写（定时）")
        private DataFileScheduled dataFileScheduled;

        @Schema(description = "本地数据库的方式填写(定时)")
        private DataBase dataBase;

        @Schema(description = "报文格式")
        private String msgFormat;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "数据文件的方式填写")
    public static class DataFile implements Serializable {
        @Schema(description = "添加方式")
        private BacktrackingFileImportTypeEnum dataFileType;

        @Schema(description = "文件服务器ID")
        private Long ftpServerId;

        @Schema(description = "文件服务器文件路径")
        private String filePath;

        @Schema(description = "文件服务器文件目录")
        private String directory;

        @Schema(description = "本地数据文件ID")
        private Long localFileId;

        @Schema(description = "数据文件名称")
        private String fileName;

        @Schema(description = "数据格式")
        private BacktrackingDataTypeEnum inputFileType = BacktrackingDataTypeEnum.JSON;

        @Schema(description = "编码格式")
        private CharsetType charsetType = CharsetType.UTF_8;

        @Schema(description = "数据起始行")
        private int startLine = 1;

        @Schema(description = "分隔符")
        private BacktrackingFileSpiltCharEnum split;

        @Schema(description = "分隔符_其他")
        private String splitKey;

        @Schema(description = "列引号")
        private String quoteChar;

        @Schema(description = "有无表头")
        private Boolean includeHeader = true;

        @Schema(description = "数据对象映射-结构化数据时显示此项")
        private List<DataModelTreeVo> dataModelTree;

        @Schema(description = "数据对象映射-报文数据时显示此项")
        private List<String> selectColumns;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "数据文件的方式-触发方式为“定时”填写")
    @ToString
    public static class DataFileScheduled implements Serializable {
        @Schema(description = "文件服务器ID")
        private Long ftpServerId;

        @Schema(description = "文件路径")
        private String filePath;

        @Schema(description = "文件服务器文件目录")
        private String directory;

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

        @Schema(description = "分隔符")
        private BacktrackingFileSpiltCharEnum split;

        @Schema(description = "分隔符_其他")
        private String splitKey;

        @Schema(description = "列引号")
        private String quoteChar;

        @Schema(description = "有无表头")
        private Boolean includeHeader = true;

        @Schema(description = "数据对象映射-结构化数据时显示此项")
        private List<DataModelTreeVo> dataModelTree;

        @Schema(description = "数据对象映射-报文数据时显示此项")
        private List<String> selectColumns;

        @Schema(description = "本地数据文件ID")
        private Long localFileId;

        @Schema(description = "示例文件名称")
        private String exampleFileName;
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
        private List<TableJoinInfo> tableJoinInfoList;

        @Schema(description = "数据预览（仅100条）-where条件")
        private String whereCondition;

        @Schema(description = "数据对象映射-结构化数据时显示此项")
        private List<DataModelTreeVo> dataModelTree;

        @Schema(description = "数据对象映射-报文数据时显示此项")
        private List<String> selectColumns;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "关联表")
    public static class TableJoinInfo implements Serializable {

        @Schema(description = "表名")
        private String tableName;

        @Schema(description = "别名")
        private String alias;

        @Schema(description = "关联类型, 主表默认为空")
        private JoinEnum joinType;

        @Schema(description = "关联关系")
        private String joinOnCondition;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "结果输出信息（数据库）")
    public static class BacktrackingOutputDb implements Serializable {

        @Schema(description = "结果表名")
        private String tableName;
        @Schema(description = "结果表中文名")
        private String tableNameDesc;
    }
}
