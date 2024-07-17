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

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author: zhouxiuxiu
 */
@Data
@Builder
@ApiModel(value = "概览变量相关目标DTO")
@AllArgsConstructor
@NoArgsConstructor
public class OverviewTargetStatisticsDto {

    /**
     * 变量名称
     */
    private String name;

    /**
     * 默认标识 0:非默认 1:默认
     */
    private Double target;


}
