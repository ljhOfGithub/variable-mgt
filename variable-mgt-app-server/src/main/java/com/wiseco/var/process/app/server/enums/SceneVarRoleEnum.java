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
package com.wiseco.var.process.app.server.enums;

import com.decision.jsonschema.util.enums.DataVariableTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum SceneVarRoleEnum {

    /**
     * 场景变量角色枚举
     */
    MATCH_DIMENSION("匹配维度", Arrays.asList(DataVariableTypeEnum.INT_TYPE,DataVariableTypeEnum.DOUBLE_TYPE,DataVariableTypeEnum.STRING_TYPE,DataVariableTypeEnum.BOOLEAN_TYPE,DataVariableTypeEnum.DATE_TYPE,DataVariableTypeEnum.DATETIME_TYPE)),
    SERIAL_NUMBER("流水号",Arrays.asList(DataVariableTypeEnum.INT_TYPE,DataVariableTypeEnum.DOUBLE_TYPE,DataVariableTypeEnum.STRING_TYPE,DataVariableTypeEnum.BOOLEAN_TYPE,DataVariableTypeEnum.DATE_TYPE,DataVariableTypeEnum.DATETIME_TYPE)),
    TIME("时间",Arrays.asList(DataVariableTypeEnum.DATETIME_TYPE)),
    EVENT_TYPE("事件类型",Arrays.asList(DataVariableTypeEnum.INT_TYPE,DataVariableTypeEnum.DOUBLE_TYPE,DataVariableTypeEnum.STRING_TYPE,DataVariableTypeEnum.BOOLEAN_TYPE,DataVariableTypeEnum.DATE_TYPE,DataVariableTypeEnum.DATETIME_TYPE));

    private final String desc;
    private List<DataVariableTypeEnum> varTypeList;

}
