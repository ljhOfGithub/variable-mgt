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
package com.wiseco.var.process.app.server.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wiseco.var.process.app.server.enums.BacktrackingOutsideCallStrategyEnum;
import com.wiseco.var.process.app.server.enums.VarProcessDataModelSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * 批量回溯-数据模型关联表
 * </p>
 *
 * @author wiseco
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("var_process_batch_backtracking_data_model")
public class VarProcessBatchBacktrackingDataModel extends BaseEntity {

    /**
     * Column: var_process_space_id
     * Type: INT
     * Default value: 0
     * Remark: 空间ID
     */
    private Long varProcessSpaceId;

    /**
     * Column: backtracking_id
     * Type: INT
     * Default value: 0
     * Remark: 批量回溯ID
     */
    private Long backtrackingId;

    /**
     * Column: object_name
     * Type: VARCHAR(100)
     * Remark: 数据模型名称
     */
    private String objectName;

    /**
     * Column: name_cn
     * Type: VARCHAR(100)
     * Remark: 数据模型中文名
     */
    private String nameCn;

    /**
     * Column: object_version
     * Type: TINYINT(3)
     * Default value: 0
     * Remark: 数据模型版本
     */
    private Long objectVersion;

    /**
     * Column: source_type
     * Type: VARCHAR(100)
     * Default value: 1
     * Remark: 对象来源
     */
    private VarProcessDataModelSourceType sourceType;

    /**
     * Column: outside_service_strategy
     * Type: VARCHAR(100)
     * Remark: 对象取值方式
     */
    private BacktrackingOutsideCallStrategyEnum outsideServiceStrategy;

}
