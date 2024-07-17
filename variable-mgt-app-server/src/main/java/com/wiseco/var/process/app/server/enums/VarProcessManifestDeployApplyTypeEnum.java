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
 * 变量清单发布内容类型枚举类
 *
 * @author wangxianli
 * @author Zhaoxiong Chen
 * @since 2022/9/28
 */
@AllArgsConstructor
@Getter
public enum VarProcessManifestDeployApplyTypeEnum {

    // 变量空间
    SPACE(11, "var_process_space:start", "var_process_space:done", "变量空间", "导出成功"),

    // 实时服务
    SERVICE(12, "var_process_service:start", "var_process_service:done", "实时服务", "{0}个实时服务"),

    // 变量清单
    MANIFEST(13, "var_process_manifest:start", "var_process_manifest:done", "变量清单", "{0}个变量，1个调用流程图"),

    // 变量数据模型
    DATA_MODEL(14, "var_process_data_model:start", "var_process_data_model:done", "数据模型", "{0}个对象"),

    // 变量预处理逻辑
    PREP(15, "var_process_prep:start", "var_process_prep:done", "预处理逻辑", "{0}个预处理逻辑"),

    // 变量模板
    TEMPLATE(16, "var_process_template:start", "var_process_template:done", "变量模板", "{0}个变量模板"),

    // 变量公共方法
    FUNCTION(17, "var_process_function:start", "var_process_function:done", "公共方法", "{0}个公共方法"),

    //数据文件
    DATA_FILE(18, "data_file:start", "data_file:done", "生成文件", "成功"),

    //数据文件(sftp)
    DATA_SHARE_DIRECTORY_FILE(19, "data_share_directory_file:start", "data_share_directory_file:done", "文件保存成功,保存路径", "成功"),;

    /**
     * 内容项目序号
     */
    private final Integer seqNo;

    /**
     * 发布内容文件上分割线
     */
    private final String start;

    /**
     * 发布内容文件下分割线
     */
    private final String done;

    /**
     * 发布内容弹窗标签
     */
    private final String label;

    /**
     * 发布内容弹窗标签对应信息
     */
    private final String desc;

    /**
     * 根据序号获取枚举类
     *
     * @param seqNo 发布内容序号
     * @return 发布内容类型枚举类
     */
    public static VarProcessManifestDeployApplyTypeEnum getTypeEnumFromSeqNo(Integer seqNo) {
        for (VarProcessManifestDeployApplyTypeEnum location : VarProcessManifestDeployApplyTypeEnum.values()) {
            if (location.getSeqNo().equals(seqNo)) {
                return location;
            }
        }
        return null;
    }

    /**
     * 从 start 前缀获取枚举类
     *
     * @param prefix 发布内容前缀
     * @return 发布内容类型枚举类
     */
    public static VarProcessManifestDeployApplyTypeEnum getTypeEnumFromStart(String prefix) {
        for (VarProcessManifestDeployApplyTypeEnum location : VarProcessManifestDeployApplyTypeEnum.values()) {
            if (location.getStart().equals(prefix + ":start")) {
                return location;
            }
        }
        return null;
    }
}
