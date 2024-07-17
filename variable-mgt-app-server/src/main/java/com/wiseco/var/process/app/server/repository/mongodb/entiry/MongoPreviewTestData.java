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
 * 测试预览数据 实体类
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "preview_test_data")
public class MongoPreviewTestData implements Serializable {

    private static final long serialVersionUID = -617753390192835120L;

    /**
     * 主键 ID
     */
    @Id
    @Field(order = 0)
    private ObjectId id;

    /**
     * 测试数据预览 UUID
     * m id - 1 uuid
     */
    @Field(order = 1)
    @Indexed(direction = IndexDirection.ASCENDING)
    private String uuid;

    /**
     * 测试数据预览内容
     */
    @Field(order = 2)
    private String dataContent;

    /**
     * 创建时间
     */
    @Field(order = 3)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
}
