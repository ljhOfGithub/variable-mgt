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
import com.wiseco.var.process.app.server.enums.FunctionHandleTypeEnum;
import com.wiseco.var.process.app.server.service.dto.common.AuthFilterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionQueryDto extends AuthFilterDTO implements Serializable {

    private static final long serialVersionUID = 2498449735052716563L;

    private Long spaceId;

    private String functionType;

    private String dataModelName;

    private String name;

    private FlowStatusEnum functionStatus;

    private Boolean isUse;

    private Boolean isTest;

    private String createdDeptCode;

    private String sortKey;

    private String sortType;

    private List<Long> categoryIdList;

    private FunctionHandleTypeEnum handleType;
}
