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
 * 监控预警通知方式
 *
 * @author wuweikang
 */
@Getter
@AllArgsConstructor
public enum MonitoringConfNotifyMethodEnum {
    /**
     * 短信
     */
    MESSAGE("短信"),
    /**
     * 邮件
     */
    EMAIL("邮件"),
    /**
     * 飞书
     */
    FEI_SHU("飞书");
    /**
     * 描述
     */
    private final String desc;
}
