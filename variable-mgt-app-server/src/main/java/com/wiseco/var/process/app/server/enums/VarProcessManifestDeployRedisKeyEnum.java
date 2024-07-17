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
 * 变量清单发布 Redis 键内容定义
 * <p>按照不同功能划分 key</p>
 *
 * @author wangxianli
 * @author Zhaoxiong Chen
 * @since 2022/9/28
 */
@Getter
@AllArgsConstructor
public enum VarProcessManifestDeployRedisKeyEnum {

    /**
     * 发布申请
     */
    DEPLOY_APPLY_REDIS_KEY("deploy:variable:apply:"),

    /**
     * 发布方式
     */
    DEPLOY_APPLY_TYPE_REDIS_KEY("deploy:variable:apply:type:"),

    /**
     * 发布解析
     */
    DEPLOY_PARSE_REDIS_KEY("deploy:variable:parse:"),

    /**
     * 发布解析业务
     */
    DEPLOY_PARSE_BIZ_REDIS_KEY("deploy:variable:parse:biz:"),

    /**
     * 发布解析部署
     */
    DEPLOY_PARSE_PUBLISH_REDIS_KEY("deploy:variable:parse:publish:"),

    /**
     * 生产发布锁
     */
    //DEPLOY_LOCK_REDIS_KEY("deploy:variable:lock"),

    /**
     * 数据解析锁
     */
    DEPLOY_PARSE_LOCK_REDIS_KEY("deploy:variable:parse:lock"),;

    /**
     * key 内容定义
     */
    private final String key;
}
