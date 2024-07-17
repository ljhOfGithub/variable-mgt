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
package com.wiseco.var.process.app.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变量清单发布解析控制器 URL 枚举类
 *
 * @author wangxianli
 * @author Zhaoxiong Chen
 * @since 2022/5/24
 */
@Getter
@AllArgsConstructor
public enum VarProcessManifestDeployParseUrlEnum {

    /**
     * 变量清单上线前校验
     */
    CHECK_DEPLOY("/variableManifestDeployParse/deploy/check"),

    /**
     * 同步数据（一键发布）
     */
    START_DEPLOY("/variableManifestDeployParse/deploy/start"),

    /**
     * 查询发布状态
     */
    QUERY_DEPLOY_STATUS("/variableManifestDeployParse/deploy/queryStatus"),

    /**
     * 获取发布最终结果
     */
    OBTAIN_DEPLOY_FINAL_STATUS("/variableManifestDeployParse/obtainDeployFinalStatus");

    private final String url;
}
