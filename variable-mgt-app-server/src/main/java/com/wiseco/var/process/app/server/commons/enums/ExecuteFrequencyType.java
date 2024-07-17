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
package com.wiseco.var.process.app.server.commons.enums;

/**
 * 任务执行执行频率类型
 *
 * @author ycc
 * @since 2023/2/2 15:54
 */
public enum ExecuteFrequencyType {

    /**
     * 每一时刻（每秒、分、时...）  *
     */
    EVERY("*"),
    /**
     * 指定范围   -
     */
    SCOPE("-"),
    /**
     * 固定间隔时长  /
     */
    FIXED_INTERVAL("/"),
    /**
     * 指定   ,
     */
    TARGET(","),
    /**
     * 不指定   ?
     */
    NONE("?"),
    /**
     * 距离X号最近的工作日（日配置）  W
     */
    RECENT_WORK_DAY("W"),
    /**
     * 本月最后一个（日、星期配置）  L
     */
    LAST("L"),
    /**
     * 第X周的星期X （星期配置）  #
     */
    WEEK_TARGET("#");

    private String typeChar;

    ExecuteFrequencyType(String typeChar) {
        this.typeChar = typeChar;
    }

    public String getTypeChar() {
        return typeChar;
    }
}
