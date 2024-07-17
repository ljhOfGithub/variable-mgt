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

/**
 * 测试数据自动生成自定义方法
 *
 * @author wangxianli
 * @date 2021/12/10
 */
@AllArgsConstructor
@Getter
public enum GenerateCustomEnum {

    /**
     * 生成姓名
     */
    NAME("generateName", "生成姓名"),
    /**
     * 生成身份证号
     */
    IDNO("generateIdNo", "生成身份证号"),
    /**
     * 生成手机号
     */
    MOBILE("generateMobilePhone", "生成手机号"),
    /**
     * 生成邮箱
     */
    EMAIL("generateEmail", "生成邮箱"),
    /**
     * 生成公司名称
     */
    COMPANY("generateCompanyName", "生成公司名称"),
    /**
     * 生成流水号
     */
    SERIAL_NUMBER("generateSerialNumber", "生成流水号"),
    /**
     * 生成详细地址
     */
    ADDRESS("generateAddress", "生成详细地址"),;

    private String code;
    private String desc;


}
