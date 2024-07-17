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
package com.wiseco.var.process.app.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitAttribute;

import java.util.List;

/**
 * <p>
 * java工具类-attribute表 服务类
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-21
 */
public interface JavaToolkitAttributeService extends IService<JavaToolkitAttribute> {

    /**
     * get attribute list by conditions
     *
     * @param classIdentifier 入参
     * @param attributeIdentifier 入参
     * @param status 状态
     * @param importStatus 导入状态
     * @param deleteFlag 删除标志
     * @return java工具属性
     */
    List<JavaToolkitAttribute> listByConditions(String classIdentifier, String attributeIdentifier, Integer status, Integer importStatus,
                                                Integer deleteFlag);

    /**
     * get attribute by conditions
     *
     * @param classIdentifier 入参
     * @param attributeIdentifier 入参
     * @return java工具包属性
     */
    JavaToolkitAttribute getOneByConditions(String classIdentifier, String attributeIdentifier);

}
