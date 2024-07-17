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
package com.wiseco.var.process.app.server.controller.feign.dto;

import com.wiseco.var.process.app.server.enums.ColRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 动态建表dto
 */
@Data
public class CreateTabDto {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段列表
     */
    private List<DbColumn> columns;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class DbColumn {

        /**
         * 字段列名
         */
        private String columnName;

        /**
         * 字段类型
         */
        private String columnDataType;

        /**
         * 是否可以为空
         */
        private Boolean allowNull = true;

        /**
         * 是否索引列
         */
        private Boolean isIndex;

        /**
         * 列角色
         */
        private ColRoleEnum colRole;
    }
}
