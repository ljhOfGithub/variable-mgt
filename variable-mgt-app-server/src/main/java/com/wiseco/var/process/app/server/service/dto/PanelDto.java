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
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;

/**
 * 属性面板
 *
 * @author: wangxianli
 */

/**
 * 属性面板
 * @param <T> 泛型
 */
@Data
@Schema(description = "属性面板对象")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PanelDto<T> {

    @Schema(description = "属性面板名称", example = "基本信息")
    private String title;

    @Schema(description = "数据对应的type类型: 基本信息: desc，文档信息: doc，版本信息: table，策略引用: cell，当前标签: currentTag，可用标签: tags，新建标签: addTag，当前版本保存记录: log，备注: remark", example = "基本信息")
    private String type;

    @Valid
    @Schema(description = "内容信息", example = "")
    private T datas;

}
