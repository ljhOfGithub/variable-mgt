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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Table: sys_oss
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_oss")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SysOss extends BaseEntity  {
    /**
     * Column: id
     * Type: BIGINT
     */
    private Long id;

    /**
     * Column: biz_type
     * Type: VARCHAR(100)
     * Remark: 业务类型：inside、backtracking
     */
    private String bizType;

    /**
     * Column: file_name
     * Type: VARCHAR(500)
     */
    private String fileName;

    /**
     * Column: oss_path
     * Type: VARCHAR(500)
     * Remark: /业务类型/时间戳/文件名
     */
    private String ossPath;

    /**
     * Column: content_type
     * Type: VARCHAR(255)
     */
    private String contentType;

    /**
     * Column: file_size
     * Type: BIGINT
     */
    private Long fileSize;


    /**
     * Column: md5
     * Type: VARCHAR(500)
     */
    private String md5;

    /**
     * Column: created_user
     * Type: VARCHAR(24)
     */
    private String createdUser;

    /**
     * Column: updated_user
     * Type: VARCHAR(24)
     */
    private String updatedUser;

}
