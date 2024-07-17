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
package com.wiseco.var.process.app.server.enums.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 批量回溯文件拆分方式枚举
 * @author ycc
 */
@Getter
@AllArgsConstructor
public enum BacktrackingFileSpliteTypeEnum {

    /**
     * 按行数
     */
    OF_ROWS("按行数"),
    /**
     * 按大小
     */
    OF_SIZE("按大小");

    String desc;

}
