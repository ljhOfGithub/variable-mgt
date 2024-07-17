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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessMonitoringAlertMessageMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessMonitoringAlertMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author wiseco
 * @since 睿信2.3
 */
@Service
public class VarProcessMonitoringAlertMessageService extends ServiceImpl<VarProcessMonitoringAlertMessageMapper, VarProcessMonitoringAlertMessage> {
    @Resource
    private VarProcessMonitoringAlertMessageMapper varProcessMonitoringAlertMessageMapper;

    /**
     * 获取最后一次发送信息的事件
     *
     * @param confId               confId
     * @param monitoringTargetName 监控对象名称
     * @param messageType          messageType
     * @return java.time.LocalDateTime
     */
    public LocalDateTime getLastAlterDate(Long confId, String monitoringTargetName, Integer messageType) {
        return varProcessMonitoringAlertMessageMapper.getLastAlterDate(confId,monitoringTargetName,messageType);
    }
}
