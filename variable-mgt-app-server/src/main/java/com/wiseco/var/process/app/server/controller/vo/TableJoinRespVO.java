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
package com.wiseco.var.process.app.server.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author yangyunsen
 * @since 2022/10/18-19:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableJoinRespVO {

    /**
     * 主表名称
     */
    private String mainTableName;

    /**
     * 全部关联表和关联条件sql
     */
    private String joinTableAndOnCondition;

}
