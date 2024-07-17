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
 * @Description: 数据模型引用类型
 * @Author: wangxianli
 * @Date: 2022/4/4
 */
@AllArgsConstructor
@Getter
public enum DataModelRefTypeEnum {
    // 直接引用类型
    DIRECT("直接引用"),
    // 间接引用类型 (作为参数和本地变量的绑定元素)
    PARAM("参数引用"),
    LOCAL("本地变量引用"),;


    private String desc;

}

