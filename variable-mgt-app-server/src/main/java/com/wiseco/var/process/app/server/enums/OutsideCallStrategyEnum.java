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

@AllArgsConstructor
@Getter
public enum OutsideCallStrategyEnum {
    // 优先缓存，增量查接口
    CACHE_FIRST_INTERFACE,
    // 只查mock
    MOCK_ONLY,
    // 优先mock(mock启用流程终止)，接着查缓存，最后查接口
    MOCK_CACHE_INTERFACE,
    // 优先mock(mock启用流程终止)，接着查接口
    CACHE_MOCK_INTERFACE,
    // 只查睿信自身缓存
    CACHE_ONLY;
}
