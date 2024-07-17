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

import com.wiseco.var.process.app.server.enums.FlowStatusEnum;
import com.wiseco.var.process.app.server.enums.FunctionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 公共函数表
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarProcessFunctionDto {

    private Long id;
    private Long varProcessSpaceId;

    private Long parentId;

    private String identifier;

    private String name;

    private FunctionTypeEnum functionType;

    private String description;

    private FlowStatusEnum status;

    private String functionDataType;

    private String prepObjectName;

    private String functionTemplateContent;

    private String content;

    private String classData;

    private String functionEntryContent;
}
