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

import javax.persistence.Column;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 测试预览数据表头 实体类
 */
@Data
@TableName("preview_test_header")
public class PreviewTestDataHeader implements Serializable {

    private static final long serialVersionUID = -8103880330045285269L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 测试数据预览 UUID
     * 1 id - 1 uuid
     */
    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    /**
     * 测试数据预览内容表头
     */
    @Column(name = "header_content")
    private String headerContent;

}

