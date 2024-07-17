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
package com.wiseco.var.process.app.server.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 测试数据集
 * </p>
 *
 * @author wangxianli
 * @since 2021-12-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "测试集返回数据")
public class TestCollectAndResultsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 测试数据集 ID
     */
    private Long id;

    /**
     * 测试结果元信息 ID
     */
    private Long resultId;

    /**
     * 测试集名称
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 来源：在线自动生成，在线输入，文件导入
     */
    private String source;

    /**
     * 测试数据记录数
     */
    private Integer dataCount;

    /**
     * 测试数据集预期结果表头字段
     */
    private String tableHeaderField;

    /**
     * 测试执行版本
     */
    private String changeNum;

    /**
     * 测试成功率
     */
    private String successRate;

    /**
     * 测试执行耗时
     */
    private Long executeTime;

    /**
     * 执行测试时间
     */
    private Timestamp testTime;

    /**
     * 最后编辑人
     */
    private String createdUser;

    /**
     * 最后测试人
     */
    private String updatedUser;

    /**
     * 创建时间
     */
    private Timestamp createdTime;

    /**
     * 更新时间
     */
    private Timestamp updatedTime;
}
