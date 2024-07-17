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
 * @Description:
 * @Author: xiewu
 * @Date: 2021/11/17
 * @Time: 15:51
 */
@AllArgsConstructor
@Getter
public enum ExcelDomainRosterDetailTemplateEnum {

    /**
     * 数据集模板
     */
    ROSTER_DETAIL_TEMPLATE_PATH(2, "roster_detail_template.xls", "template\\domain_roster_detail\\roster_detail_template.xls"),

    /**
     * 字典项模板
     */
    DOMAIN_DICT_DETAILS_TEMPLATE_PATH(3, "domain_dict_details_template.xls", "template\\domain_dict_details\\domain_dict_details_template.xls"),

    /**
     * 字典项模板
     */
    STRATEGY_ENGINE_DICT_DETAILS_TEMPLATE_PATH(3, "strategy_engine_dict_details_template.xls", "template\\strategy_engine_dict_details\\strategy_engine_dict_details_template.xls");


    private int code;
    private String fileName;
    private String path;

}
