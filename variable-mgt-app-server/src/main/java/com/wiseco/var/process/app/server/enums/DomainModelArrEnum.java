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
 * 数据模型：是否数组
 *
 * @author wangxianli
 * @date 2021/12/28
 */
@AllArgsConstructor
@Getter
public enum DomainModelArrEnum {
    /**
     * 非数组
     */
    NO("0", "否"),

    /**
     * 数组
     */
    YES("1", "是"),;
    private String code;

    private String message;
}
