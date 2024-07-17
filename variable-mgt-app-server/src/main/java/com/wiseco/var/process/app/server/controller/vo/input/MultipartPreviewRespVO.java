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

import com.wiseco.var.process.app.server.commons.BasePageInfo;
import com.wiseco.var.process.app.server.commons.enums.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author Asker.J
 * @since 2022/4/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分片数据预览")
public class MultipartPreviewRespVO {

    @Schema(description = "数据")
    private DataPreviewResult dataPreviewResult;

    @Schema(description = "表头")
    private HeaderPreviewResult headerPreviewResult;

    @Data
    public static class HeaderPreviewResult extends BasePageInfo {

        @Schema(description = "内容")
        private List<String> headers;

        @Schema(description = "中文描述")
        private Map<String, String> labels;

        @Schema(description = "类型")
        private Map<String, DataType> types;

        /**
         * 构造函数
         *
         * @param pageSize 页大小
         * @param pageNo 页码
         * @param totalCount 总数
         * @param pageNum 页大小
         * @param headers 头部
         * @param labels 标签
         * @param types 类型
         */
        @Builder
        public HeaderPreviewResult(int pageSize, int pageNo, int totalCount, int pageNum, List<String> headers, Map<String, String> labels,
                                   Map<String, DataType> types) {
            super(pageSize, pageNo, totalCount, pageNum);
            this.headers = headers;
            this.labels = labels;
            this.types = types;
        }
    }

    @Data
    public static class DataPreviewResult extends BasePageInfo {

        @Schema(description = "内容")
        private List<Map<String, Object>> data;

        /**
         * 构造函数
         *
         * @param pageSize 分页大小
         * @param pageNo 页码
         * @param totalCount 总数
         * @param pageNum 页数
         * @param data 多行数据
         */
        @Builder
        public DataPreviewResult(int pageSize, int pageNo, int totalCount, int pageNum, List<Map<String, Object>> data) {
            super(pageSize, pageNo, totalCount, pageNum);
            this.data = data;
        }
    }

}

