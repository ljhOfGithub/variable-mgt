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
package com.wiseco.var.process.app.server.commons.test.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestExcelDto {
    /**
     * sheet名称
     */
    private String sheetName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 标题
     */

    private List<String> titleList;

    /**
     * 英文标题
     */
    private List<String> keyList;

    /**
     * 内容
     */
    private List<List<String>> valueList;

    /**
     * 内容数据类型
     */
    private List<String> typeList;
}
