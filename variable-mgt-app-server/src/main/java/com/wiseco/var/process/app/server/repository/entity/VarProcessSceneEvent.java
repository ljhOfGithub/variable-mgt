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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wiseco.var.process.app.server.enums.SceneCmpSymbolEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("var_process_scene_event")
public class VarProcessSceneEvent extends BaseEntity {
    @TableField("event_name")
    private String eventName;

    @TableField("scene_id")
    private Long sceneId;

    @TableField("code_value")
    private String codeValue;

    @TableField("scene_cmp_symbol")
    private SceneCmpSymbolEnum sceneCmpSymbol;
}
