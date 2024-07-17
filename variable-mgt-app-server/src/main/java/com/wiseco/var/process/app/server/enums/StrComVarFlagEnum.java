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
 * 组件与变量引用关系，测试标识枚举
 *
 * @author wangxianli
 * @date 2021/12/29
 */
@Getter
@AllArgsConstructor
public enum StrComVarFlagEnum {
    //0:不需要测试数据 1:输入测试数据 2:输出测试数据 3:输入和输出
    NOT_TEST_DATE("0"),
    INPUT("1"),
    OUTPUT("2"),
    INPUT_AND_OUTPUT("3");

    private String code;
}

