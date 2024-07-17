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

import java.util.Map;

/**
 * @author wangxianli
 * @since 2022/6/27
 */
@Data
public class TestExcelContentDto {

    /**
     * sheet名称
     */
    private String sheetName;

    /**
     * 行数据
     */
    private Map<String, String> rowMapData;
}
