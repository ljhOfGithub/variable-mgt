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
package com.wiseco.var.process.app.server.commons.constant;

/**
 * 数据查询使用的常量
 * 
 * @author yangyunsen
 * @since 2022/10/18-17:51
 */
public class SqlQueryKeyConstant {

    // 分页偏移页数据量关键字,实际分批处理时可能出现
    /**
     * sql中的分页偏移量关键字，实际分批处理时可能出现
     */
    public static final String OFFSET = "<offset>";
    /**
     * 页数据量关键字
     */
    public static final String PAGESIZE = "<pageSize>";
    /**
     * oracle分页关键字，逻辑上等于 offset+pagesize
     */
    public static final String MAXOFFSET = "<maxOffset>";

    /**
     * join sql模板 0-join expr 1-table name 2-table alias 3-on condition
     */
    public static final String JOINTEMPLATE = " {0} {1} {2} {3} ";

    /**
     * 字段模板 0-表别名 加转义符号 1-字段名 加转义符号 2-表名 3-字段名 2_3 字段别名，避免字段冲突
     */
    public static final String FIELDTEMPLATE = "{0}.{1} {2}_{3}";

}
