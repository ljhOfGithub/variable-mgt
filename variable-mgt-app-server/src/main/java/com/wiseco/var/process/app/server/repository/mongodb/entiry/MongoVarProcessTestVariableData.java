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
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 组件测试数据集 实体类
 *
 * @author Zhaoxiong Chen
 * @since 2022/3/2
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "var_process_test_variable_data")
public class MongoVarProcessTestVariableData implements Serializable {

    private static final long serialVersionUID = -8631915276933752597L;

    /**
     * 主键 ID
     */
    @Id
    @Field(order = 0)
    private ObjectId id;

    /**
     * 测试数据集 ID
     * 对应 MySQL 表格 test_component.id
     */
    @Field(order = 1)
    @Indexed(direction = IndexDirection.ASCENDING)
    private Long testId;

    /**
     * 单条数据 (数据明细) 在测试数据集中的序列号
     */
    @Field(order = 2)
    private Integer dataId;

    /**
     * 创建时间
     */
    @Field(order = 3)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 输入
     * JSON 反序列化为 String
     */
    @Field(order = 4)
    private String inputContent;

    /**
     * 预期结果
     * JSON 反序列化为 String
     */
    @Field(order = 5)
    private String expectContent;
}
