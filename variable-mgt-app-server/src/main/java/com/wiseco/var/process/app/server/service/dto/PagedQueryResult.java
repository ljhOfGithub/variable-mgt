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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/15
 */

/**
 * 分页查询结果输出参数
 *
 * @param <T> 泛型
 * @author liusiyu
 * @since 2023/09/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页查询结果输出参数")
public class PagedQueryResult<T extends Serializable> implements Serializable {

    private static final long SERIAL_VERSION_UID = -2457009695841450443L;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "每页记录数")
    private Long size;

    @Schema(description = "总页数")
    private Long pages;

    @Schema(description = "当前页编号")
    private Long current;

    @Schema(description = "当前页查询记录")
    private List<T> records;
}
