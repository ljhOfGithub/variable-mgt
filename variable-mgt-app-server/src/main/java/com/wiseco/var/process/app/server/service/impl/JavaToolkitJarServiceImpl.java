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
package com.wiseco.var.process.app.server.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.repository.JavaToolkitJarMapper;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitJar;
import com.wiseco.var.process.app.server.service.JavaToolkitJarService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * java工具类jar包表 服务实现类
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-21
 */
@Service
public class JavaToolkitJarServiceImpl extends ServiceImpl<JavaToolkitJarMapper, JavaToolkitJar> implements JavaToolkitJarService {

    @Override
    public List<JavaToolkitJar> listByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return Collections.emptyList();
        }
        return Optional.ofNullable(list(Wrappers.<JavaToolkitJar>lambdaQuery()
                        .select(JavaToolkitJar::getId)
                        .eq(JavaToolkitJar::getName, name)))
                .orElse(Collections.emptyList());
    }

    @Override
    public JavaToolkitJar getUsableByIdentifier(String identifier) {
        if (StringUtils.isEmpty(identifier)) {
            throw new NullPointerException("Parameter 'identifier' can not be null.");
        }
        return Optional.ofNullable(getOne(Wrappers.<JavaToolkitJar>lambdaQuery()
                        .select(JavaToolkitJar::getId)
                        .eq(JavaToolkitJar::getIdentifier, identifier)
                        .eq(JavaToolkitJar::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())))
                .orElse(null);
    }
}
