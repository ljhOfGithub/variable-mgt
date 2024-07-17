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
 * 测试数据来源枚举
 *
 * @author wangxianli
 * @date 2021/12/28
 */
@Getter
@AllArgsConstructor
public enum TestDataSourceEnum {
    /**
     * 在线自动生成，在线输入，文件导入
     */
    AUTO("在线自动生成"),


    INPUT("在线输入"),

    FILE("文件导入"),

    PROD("生产数据导入");

    private String message;

}

