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
package com.wiseco.var.process.app.server.repository.mongodb.entiry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * 变量测试执行结果表头 实体类
 *
 * @author Zhaoxiong Chen
 * @since 2022/3/2
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "var_process_test_variable_result_header")
public class MongoVarProcessTestVariableResultHeader implements Serializable {

    private static final long serialVersionUID = 2964152291719786869L;

    /**
     * 主键 ID
     */
    @Id
    @Field(order = 0)
    private ObjectId id;

    /**
     * 执行结果 ID
     * 对应 MySQL 表格 test_component_results.id
     */
    @Field(order = 1)
    @Indexed(direction = IndexDirection.ASCENDING)
    private Long resultId;

    /**
     * 执行结果内容表头, 供 Excel 生成使用
     */
    @Field(order = 2)
    private String resultHeader;
}
