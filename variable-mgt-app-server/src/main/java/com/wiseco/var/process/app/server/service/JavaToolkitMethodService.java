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
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitMethod;

import java.util.List;

/**
 * <p>
 * java工具类-method表 服务类
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-21
 */
public interface JavaToolkitMethodService extends IService<JavaToolkitMethod> {

    /**
     * get method list by conditions
     *
     * @param classIdentifier 入参
     * @param methodIdentifier 入参
     * @param status 入参
     * @param importStatus 入参
     * @param deleteFlag 入参
     * @return List
     */
    List<JavaToolkitMethod> listByConditions(String classIdentifier, String methodIdentifier, Integer status, Integer importStatus, Integer deleteFlag);

    /**
     * get method by conditions
     *
     * @param classIdentifier 入参
     * @param methodIdentifier 入参
     * @return JavaToolkitMethod
     */
    JavaToolkitMethod getOneByConditions(String classIdentifier, String methodIdentifier);

}