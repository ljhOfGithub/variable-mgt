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
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitJar;

import java.util.List;

/**
 * <p>
 * java工具类jar包表 服务类
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-21
 */
public interface JavaToolkitJarService extends IService<JavaToolkitJar> {

    /**
     * get jar list by jar name
     *
     * @param name 入参
     * @return List
     */
    List<JavaToolkitJar> listByName(String name);


    /**
     * get usable status jar by identifier
     *
     * @param identifier 入参
     * @return JavaToolkitJar
     */
    JavaToolkitJar getUsableByIdentifier(String identifier);
}
