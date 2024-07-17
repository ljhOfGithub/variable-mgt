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

import lombok.Data;

/**
 * 变量清单发布数据预览 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/9/27
 */
@Data
public class VariableManifestDeployContentOverviewDto {

    /**deploo
     * 变量名称
     */
    private String name;

    /**
     * 变量中文名
     */
    private String label;

    /**
     * 变量分类
     */
    private String category;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 版本状态
     */
    private Integer status;
}
