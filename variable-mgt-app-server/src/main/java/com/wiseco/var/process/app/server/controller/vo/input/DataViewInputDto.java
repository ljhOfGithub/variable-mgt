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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiseco.boot.data.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wzc
 */
@Data
@Schema(description = "结果查询数据查看 入参DTO")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DataViewInputDto implements Serializable {

    private static final long serialVersionUID = 6465698601616893561L;

    @Schema(description = "内置参数")
    private DataViewInputDto.BuiltInParam builtInParam;

    @Schema(description = "自定义参数")
    private DataViewInputDto.CustomParam customParam;

    @Schema(description = "查看参数")
    private DataViewInputDto.ViewParam viewParam;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class RowPage extends PageDTO {

        private int total;
        private int pageNum;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ColumnPage extends PageDTO {

        private int total;
        private int pageNum;
    }

    @Data
    public static class FillItems {

        private String connectType;
        @JsonProperty("field_name")
        private String fieldName;
        @JsonProperty("field_type")
        private String fieldType;
        private List<Items> items;
        private Boolean selected;
        private Boolean visible;
    }

    @Data
    public static class Items {

        private String conditionType;
        private String value;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class BuiltInParam {
        @Schema(description = "实时服务名称（传id）")
        private String realTimeServiceName;

        @Schema(description = "只能看的实时服务ID集合")
        private List<Long> serviceIds;

        @Schema(description = "调用清单-id")
        private String callList;

        @Schema(description = "调用时间 (开始)")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date startDate;

        @Schema(description = "调用时间 (结束)")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date endDate;

        @Schema(description = "外部流水号")
        private String externalSerialNo;

        @Schema(description = "主体唯一标识")
        private String principalUniqueIdentification;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class CustomParam {

        @Schema(description = "客户编号")
        private String custNo;

        @Schema(description = "姓名")
        private String custName;

        @Schema(description = "证件类型")
        private String certType;

        @Schema(description = "证件号码")
        private String certNo;

        @Schema(description = "手机号码")
        private String mobile;

        @Schema(description = "产品编码")
        private String productCode;

        @Schema(description = "渠道编码")
        private String channelCode;

        @Schema(description = "业务场景")
        private String bizType;

        @Schema(description = "变量过滤条件")
        @JsonProperty("filter_items")
        private List<FillItems> filterItems = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ViewParam {
        @Schema(description = "行分页")
        private RowPage rowPage;

        @Schema(description = "列分页")
        private ColumnPage columnPage;

        @Schema(description = "排序")
        private String order;

        @Schema(description = "显示列，显示列没设置传null", example = "null")
        private List<String> columns;
    }
}
