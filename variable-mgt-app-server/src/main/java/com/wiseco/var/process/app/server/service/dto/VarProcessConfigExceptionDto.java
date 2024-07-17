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
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "异常值查询 DTO")
public class VarProcessConfigExceptionDto implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 变量空间ID
     */
    private Integer varProcessSpaceId;

    /**
     * 异常值标识符
     */
    private String exceptionValueCode;

    /**
     * 变量类型：int、double、string、boolean、date、datetime
     */
    private String dataType;

    /**
     * 异常值:例如:NA,-9999
     */
    private String exceptionValue;

    /**
     * 类型:1:内置异常 2:自定义
     */
    private Integer exceptionType;

    /**
     * 异常说明
     */
    private String exceptionExplain;

    /**
     * 创建用户
     */
    private String createdUser;

    /**
     * 更新用户
     */
    private String updatedUser;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

}
