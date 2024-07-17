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
package com.wiseco.var.process.app.server.commons.enums;

/**
 * 分析枚举
 */
public enum AnalysisIndexEnum {
    /**
     * iv
     */
    IV("iv"),
    /**
     * psi
     */
    PSI("psi"),
    /**
     * zeroRatio
     */
    ZERO_RATIO("zeroRatio"),
    /**
     * uniqueNum
     */
    UNIQUE_NUM("uniqueNum"),
    /**
     * percentage
     */
    PERCENTAGE("percentage"),
    /**
     * missingRatio
     */
    MISSING_RATIO("missingRatio"),
    /**
     * specialRatio
     */
    SPECIAL_RATIO("specialRatio"),
    /**
     * common
     */
    COMMON("common");

    private String code;

    AnalysisIndexEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
