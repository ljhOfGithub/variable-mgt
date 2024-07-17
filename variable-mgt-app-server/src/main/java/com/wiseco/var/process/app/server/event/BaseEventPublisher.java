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
package com.wiseco.var.process.app.server.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author  ycc
 * @since  2023/1/11 14:35
 */
@Component
@Slf4j
public class BaseEventPublisher {
    @Resource
    private ApplicationEventPublisher eventPublisher;

    /**
     * 发送事件
     *
     * @param event 基础事件
     */
    public void sendEvent(BaseEvent event) {
        eventPublisher.publishEvent(event);
        log.info("事件发布成功");
    }
}
