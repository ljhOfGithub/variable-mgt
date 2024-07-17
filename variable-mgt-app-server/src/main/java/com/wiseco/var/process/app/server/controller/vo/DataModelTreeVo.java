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
package com.wiseco.var.process.app.server.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 决策领域树形结构实体复制版本
 *
 * @author wangxiansheng
 * @since 16:04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataModelTreeVo implements Serializable {

    private static final long serialVersionUID = -3155150419381232198L;
    /**
     * 变量英文名
     */
    @Schema(description = "变量英文名", example = "variable")
    private String name;

    /**
     * 变量标签(英文名-中文名)
     */
    @Schema(description = "变量标签 (英文名-中文名)", example = "variable-变量")
    private String label;

    /**
     * 字段全路径
     */
    @Schema(description = "字段全路径", example = "input.variable")
    private String value;

    /**
     * 字段全路径, 针对循环内 this_ 字段添加全路径
     */
    @Schema(description = "字段全路径", example = "input.variable")
    private String fullPathValue;

    /**
     * 变量编号
     * type=method 引入java类型方法编号
     */
    @Schema(description = "变量编号", example = "0005E13AE2B0BEA0")
    private String identifier;

    /**
     * type=method 引入java类型方法所在实例信息
     */
    @Schema(description = "引入java类型方法所在实例信息", example = "engine.javabean.User")
    private String instanceValue;

    /**
     * 变量中文名
     */
    @Schema(description = "变量中文名", example = "变量")
    private String describe;

    /**
     * 数组标识, 0: false, 1: true
     */
    @Schema(description = "数组标识, 0: false, 1: true")
    private String isArr = "0";

    /**
     * 扩展标识, 0: false, 1: true
     */
    @Schema(description = "扩展标识, 0: false, 1: true")
    private String isExtend = "0";

    /**
     * 是否可设空值, 0: 允许, 1: 不允许
     */
    @Schema(description = "是否可设空值, 0: 允许, 1: 不允许")
    private String isEmpty = "0";

    /**
     * 字典类型编码
     */
    @Schema(description = "字典类型编码", example = "dictionaryCode")
    private String enumName;

    /**
     * 变量类型
     */
    @Schema(description = "变量类型", example = "int")
    private String type;

    /**
     * 引用变量类型
     */
    @Schema(description = "引用变量类型")
    private String typeRef;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "memorandum")
    private String remarks;

    /**
     * 是否被使用: 0 没有被使用，1 已经被使用
     */
    @Schema(description = "是否被使用: 0 没有被使用, 1 已经被使用")
    private String isUse;

    /**
     * 变量操作 (新增/删除) 标识, add: 新增, delete: 删除, null: 原数据 (无操作)
     */
    @Schema(description = "变量操作 (新增/删除) 标识, add: 新增, delete: 删除, null: 原数据 (无操作)", example = "add")
    private String addDelete;

    /**
     * 修改数据的字段名称
     */
    @Schema(description = "修改数据的字段名称", example = "['isArr','describe']")
    private List<String> updatePro;

    /**
     * 参数中文名
     */
    @Schema(description = "参数中文名", example = "测试")
    private String parameterLabel;

    /**
     * 参数类型
     */
    @Schema(description = "参数类型", example = "input.application")
    private String parameterType;

    /**
     * 参数是否数组, "0": 否, "1": 是
     */
    @Schema(description = "参数是否数组, \"0\": 否, \"1\": 是", example = "0")
    private String isParameterArray;

    /**
     * 对象类型：ref（引入java对象）
     */
    @Schema(description = "对象类型：ref（引入java对象）", example = "ref")
    private String objectType;

    /**
     * 类名
     */
    @Schema(description = "类名", example = "ClaimSummary")
    private String className;

    /**
     * java类全路径
     */
    @Schema(description = "java类全路径", example = "claim.model.ClaimSummary")
    private String existingJavaType;

    /**
     * 访问权限：readonly、read/write
     */
    @Schema(description = "访问权限：readonly、read/write", example = "readonly")
    private String access;

    /**
     * 属性来源：1=字段，2=方法
     */
    @Schema(description = "属性来源：1=字段，2=方法", example = "1")
    private Integer sourceType;

    /**
     * 引用java对象根节点：0：false，1：true
     */
    @Schema(description = "引用java对象根节点：0：false，1：true")
    private String isRefRootNode = "0";

    @Schema(description = "javabean属性identifier")
    private String attributeIdentifier;

    @Schema(description = "禁止修改本行：true-是，false-否", example = "false")
    private boolean rowDisEdit = false;

    @Schema(description = "禁止删除本行：true-是，false-否", example = "false")
    private boolean rowDisDel = false;

    @Schema(description = "禁止添加同级按钮：true-是，false-否", example = "false")
    private boolean rowDisAddNode = false;

    @Schema(description = "禁止添加下级按钮：true-是，false-否", example = "false")
    private boolean rowDisAddChild = false;

    @Schema(description = "禁止上移按钮：true-是，false-否", example = "false")
    private boolean rowDisUp = false;

    @Schema(description = "禁止下移按钮：true-是，false-否", example = "false")
    private boolean rowDisDown = false;

    @Schema(description = "禁止复制按钮：true-是，false-否", example = "false")
    private boolean rowDisCopy = false;

    @Schema(description = "禁止剪切按钮：true-是，false-否", example = "false")
    private boolean rowDisCut = false;

    @Schema(description = "禁止粘贴按钮：true-是，false-否", example = "false")
    private boolean rowDisPaste = false;

    /**
     * 默认值
     */
    private String sourceVar;

    /**
     * 属性
     */
    @Schema(description = "属性")
    private List<DataModelTreeVo> children;

    /**
     * 默认值
     */
    private String defaults;

    /**
     * 通过对象序列化实现深拷贝
     *
     * @return DataModelTreeVo
     */
    public DataModelTreeVo deepClone() {
        //深度克隆
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            //return super.clone();//默认浅克隆，只克隆八大基本数据类型和String
            //序列化
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this);

            //反序列化
            bis = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bis);
            DataModelTreeVo copy = (DataModelTreeVo) ois.readObject();
            return copy;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
