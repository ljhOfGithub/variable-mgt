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

import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestDataModel;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifestVariable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 变量清单 DTO
 *
 * @author Zhaoxiong Chen
 * @since 2022/8/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariableManifestDto {

    /**
     * 实时服务-变量清单关系ID
     */
    private Long serviceManifestId;

    /**
     * 接口基本信息
     */
    private VarProcessManifest manifestEntity;

    /**
     * 发布变量清单
     */
    private List<VarProcessManifestVariable> variablePublishList;

    /**
     * 数据模型绑定
     */
    private List<VarProcessManifestDataModel> dataModelMappingList;
}
