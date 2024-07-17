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
package com.wiseco.var.process.app.server.service.common;

import com.wiseco.boot.cache.annotation.CacheEvent;
import com.wiseco.var.process.app.server.commons.constant.CacheNameConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * 缓存时间发布服务
 * @author wuweikang
 */
@Component
@Log4j2
public class CacheEventSendService {

    /**
     * 服务更新事件（服务状态更新为启用或停用时发布）
     */
    @CacheEvent(cacheNames = {CacheNameConstant.SERVICE_VERSION_CHANGE},appCodes = {})
    public void serviceVersionChange() {
        log.info("服务更新事件发布");
    }

    /**
     * 清单更新事件（清单状态更新为启用或停用时发布）
     */
    @CacheEvent(cacheNames = {CacheNameConstant.MANIFEST_CHANGE},appCodes = {})
    public void manifestChange() {
        log.info("清单更新事件发布");
    }

    /**
     * 实时服务基本信息更新事件（实时服务基本信息保存和删除时发布）
     */
    @CacheEvent(cacheNames = {CacheNameConstant.REALTIME_SERVICE_CHANGE},appCodes = {"variable-service-rest"})
    public void realtimeServiceChange() {
        log.info("实时服务基本信息更新事件发布");
    }

    /**
     * 服务授权信息更新
     */
    @CacheEvent(cacheNames = {CacheNameConstant.AUTHORIZATION_CHANGE},appCodes = {"variable-service-rest"})
    public void authorizationChange() {
        log.info("服务授权更新（停用/启用/删除）事件发布");
    }

    /**
     * 服务授权配置信息更新
     */
    @CacheEvent(cacheNames = {CacheNameConstant.AUTHORIZATION_CONFIG_CHANGE},appCodes = {"variable-service-rest"})
    public void authorizationConfigChange() {
        log.info("服务授权配置信息更新事件发布");
    }
}
