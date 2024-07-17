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
package com.wiseco.var.process.app.server.commons;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Asker.J
 * @since  2022/5/27
 */
@Data
@NoArgsConstructor
public class HeaderBasePage extends BasePageInfo {
    /**
     * constructor
     *
     * @param pageSize 页面大小
     * @param pageNo 当前页
     * @param totalCount 总条数
     * @param pageNum pageNum
     */
    @Builder
    public HeaderBasePage(int pageSize, int pageNo, int totalCount, int pageNum) {
        super(pageSize, pageNo, totalCount, pageNum);
    }
}
