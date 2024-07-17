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
 * 测试数据 表默认字段定义
 *
 * @author wangxianli
 * @date 2021/12/29
 */
@Getter
@AllArgsConstructor
public enum TestExcelFileEnum {
    /**
     * 信息编号ID
     */
    ID("id", "编号"),

    /**
     * 父级信息ID
     */
    PARENT_ID("parentId", "主信息编号");

    private String code;

    private String message;
}
