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


/**
 * log操作类型枚举
 */
public enum LoggableMethodTypeEnum {
    /**
     * common
     */
    CREATE("添加"),
    NEW_VERSION("添加版本"),
    COPY("复制"),
    EDIT("编辑"),
    MODIFY("修改"),
    SAVE("保存"),
    DELETE("删除"),
    /**
     * 状态更新
     */
    UPDATE_STATUS("actionType"),
    ENABLE("启用"),
    DOWN("停用"),

    /**
     * 批量操作
     */
    BATCH_DELETE("批量删除"),

    /**
     * 批量回溯
     */
    NEW_BACKTRACKING_TASK("添加任务"),
    EXECUTE("执行"),
    REXECURE("重新执行"),
    CONTINUE_EXEC("继续执行"),
    PAUSE_EXEC("暂停执行"),

    /**
     * 服务授权
     */
    CONFIG_SERVICE("配置服务"),

    /**
     * 生成变量
     */
    GENERATE_VARS("生成变量"),

    /**
     * 字典
     */
    CREATE_DICT("添加字典类型"),
    EDIT_DICT("编辑字典类型"),
    DELETE_DICT("删除字典类型"),
    CREATE_DICT_DETAIL("添加字典项"),
    EDIT_DICT_DETAIL("编辑字典项"),
    DELETE_DICT_DETAIL("删除字典项"),


    /**
     * CONFIG
     */
    DEFAULT_VALUE("设置缺失值"),

    /**
     * 分类
     */
    CREATE_CAT("添加分类"),
    EDIT_CAT("修改分类"),
    DELETE_CAT("删除分类"),

    /**
     * 标签
     */
    CREATE_TAG_GROUP("添加标签组"),
    EDIT_TAG_GROUP("编辑标签组"),
    DELETE_TAG_GROUP("删除标签组"),


    /**
     * 内数-待删除
     */
    CREATE_TABLE("添加表"),
    ADD_DATA("在线添加");

    private final String value;

    LoggableMethodTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
