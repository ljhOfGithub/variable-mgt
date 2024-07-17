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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiewu
 */
@Data
@Schema(description = "字典项excel导入错误行号 出参DTO")
public class DictDetailImportFailOutputDto {
    /**
     * 字典编码或者名称重复
     */
    private List<Integer> codeOrNameFailRow = new ArrayList<>();
    /**
     * 字典类型编码为空或不存在
     */
    private List<Integer> typeCodeFailRow = new ArrayList();
    /**
     * 字典编码为空或有误
     */
    private List<Integer> codeFailRow = new ArrayList();
    /**
     * 字典名称为空
     */
    private List<Integer> nameFailRow = new ArrayList();
    /**
     * 上级字典编码不存在
     */
    private List<Integer> parentCodeFailRow = new ArrayList();

    /**
     * 当前行号（不包含表头）
     */
    private int rowNumber;

    /**
     * 错误行号总数
     *
     * @return 行号总数
     */
    public int failTotal() {
        return codeOrNameFailRow.size() + typeCodeFailRow.size() + codeFailRow.size() + nameFailRow.size() + parentCodeFailRow.size();
    }
}
