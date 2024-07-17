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
 * 函数类型 枚举类
 *
 * @author wangxianli
 * @since 2022/6/10
 */
@AllArgsConstructor
@Getter
public enum FunctionTypeEnum {
    /**
     * 公共函数类型
     */
    TEMPLATE("变量模板"), FUNCTION("公共方法"), PREP("数据预处理"),;

    private String desc;

}