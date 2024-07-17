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

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum KinShipTypeEnum {

    /**
     * 数据模型
     */
    DATA_MODEL("数据模型"),
    /**
     * 变量
     */
    VARIABLE("变量"),
    /**
     * 变量模板
     */
    TEMPLATE("变量模板"),
    /**
     * 变量清单
     */
    MANIFEST("变量清单"),
    /**
     * 公共方法
     */
    FUNCTION("公共方法"),
    /**
     * 数据预处理
     */
    PREP("数据预处理"),
    /**
     * 实时服务
     */
    SERVICE("实时服务"),
    /**
     * 批量回溯
     */
    BACKTRACKING("批量回溯");

    String      desc;

    /**
     * 根据desc获取枚举类型
     * @param desc 枚举中文名
     * @return 枚举类型
     */
    public static KinShipTypeEnum getByDesc(String desc) {
        for (KinShipTypeEnum kinShipTypeEnum : KinShipTypeEnum.values()) {
            if (Objects.equals(kinShipTypeEnum.getDesc(), desc)) {
                return kinShipTypeEnum;
            }
        }
        return null;
    }
}
