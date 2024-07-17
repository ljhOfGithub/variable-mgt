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
package com.wiseco.var.process.app.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessMonitoringAlertMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * <p>
 * 监控预警消息 Mapper
 * </p>
 *
 * @author wiseco
 * @since 睿信2.3
 */
@Mapper
public interface VarProcessMonitoringAlertMessageMapper extends BaseMapper<VarProcessMonitoringAlertMessage> {


    /**
     * 获取最后一次发送信息的事件
     *
     * @param confId               confId
     * @param monitoringTargetName 监控对象名称
     * @param messageType          messageType
     * @return java.time.LocalDateTime
     */
    LocalDateTime getLastAlterDate(@Param("confId")Long confId,@Param("monitoringTargetName") String monitoringTargetName, @Param("messageType")int messageType);
}
