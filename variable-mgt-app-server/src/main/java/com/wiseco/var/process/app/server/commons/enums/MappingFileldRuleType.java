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
 * 数据集变量映射数据转换规则类型
 *
 * @author ycc
 * @since 2022/11/8 16:09
 */
public enum MappingFileldRuleType {

    /**
     * 删除操作
     */
    DEL,
    /**
     * 添加操作
     */
    ADD,
    /**
     * 转换操作
     */
    CON,
    /**
     * 截取操作
     */
    SUB,
    /**
     * 替换操作
     */
    REP
}
