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
package com.wiseco.var.process.app.server.controller.vo.input;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 上移/下移
 */
@Getter
@AllArgsConstructor
public enum OpeType {
    /**
     * 上移
     */
    UP("上移"),
    /**
     * 下移
     */
    DOWN("下移");
    /**
     * 描述信息
     */
    private final String desc;
}
