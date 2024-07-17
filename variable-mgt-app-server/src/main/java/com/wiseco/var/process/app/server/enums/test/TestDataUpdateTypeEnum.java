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
 * 测试数据修改类型
 *
 * @author wangxianli
 */
@Getter
@AllArgsConstructor
public enum TestDataUpdateTypeEnum {

    /**
     * 新增
     */
    ADD("add", "新增"),
    UPDATE("update", "修改"),
    DELETE("delete", "删除"),
    COPY("copy", "复制");

    private String code;
    private String message;

}
