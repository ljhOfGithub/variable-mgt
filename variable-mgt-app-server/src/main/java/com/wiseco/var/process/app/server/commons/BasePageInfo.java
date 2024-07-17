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

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Asker.J
 * @since  2021/12/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BasePageInfo {
    @Schema(description = "每页大小")
    private int pageSize = MagicNumbers.TEN;
    @Schema(description = "当前页,默认从1开始")
    private int pageNo = 1;
    @Schema(description = "总条数(一般不返回这个)")
    private int totalCount = 0;
    @Schema(description = "总页数")
    private int pageNum = 0;

    /**
     * startNum
     *
     * @return int
     */
    public int startNum() {
        return pageSize * (pageNo - 1) + 1;
    }

    /**
     * endNum
     *
     * @return int
     */
    public int endNum() {
        return pageSize * (pageNo);
    }
}
