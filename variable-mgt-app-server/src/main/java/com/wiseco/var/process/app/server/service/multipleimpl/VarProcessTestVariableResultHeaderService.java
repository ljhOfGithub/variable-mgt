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
package com.wiseco.var.process.app.server.service.multipleimpl;

public interface VarProcessTestVariableResultHeaderService {

    /**
     * 根据数据集 ID 查找执行结果表头
     * 
     * @param resultId 执行结果 ID
     * @return 执行结果表头
     */
    String findHeaderByResultId(Long resultId);

    /**
     * 保存或更新执行结果表头
     * 
     * @param resultId 执行结果 ID
     * @param resultHeader 执行结果表头
     */
    void saveOrUpdateHeader(Long resultId, String resultHeader);
}
