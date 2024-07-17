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
package com.wiseco.var.process.app.server.commons.util.cron;

import lombok.Builder;
import lombok.Data;

/**
 * 固定间隔配置
 */
@Data
@Builder
public class FixedInterval {
    /**
     * 开始位置
     */
    private int start;
    /**
     * 间隔长度
     */
    private int interval;
}
