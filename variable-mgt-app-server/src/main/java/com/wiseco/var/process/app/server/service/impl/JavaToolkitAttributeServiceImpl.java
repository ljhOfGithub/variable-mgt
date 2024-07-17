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
import com.wiseco.var.process.app.server.repository.JavaToolkitAttributeMapper;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitAttribute;
import com.wiseco.var.process.app.server.service.JavaToolkitAttributeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * java工具类-attribute表 服务实现类
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-21
 */
@Service
public class JavaToolkitAttributeServiceImpl extends ServiceImpl<JavaToolkitAttributeMapper, JavaToolkitAttribute> implements
        JavaToolkitAttributeService {

    @Override
    public List<JavaToolkitAttribute> listByConditions(String classIdentifier, String attributeIdentifier, Integer status, Integer importStatus, Integer deleteFlag) {
        if (StringUtils.isEmpty(classIdentifier) && StringUtils.isEmpty(attributeIdentifier) && Objects.isNull(status) && Objects.isNull(importStatus) && Objects.isNull(deleteFlag)) {
            throw new NullPointerException("All of parameter 'classIdentifier' & 'attributeIdentifier' & 'status' & 'importStatus' & 'deleteFlag' can not be null.");
        }
        return Optional.ofNullable(list(Wrappers.<JavaToolkitAttribute>lambdaQuery()
                        .eq(!StringUtils.isEmpty(classIdentifier), JavaToolkitAttribute::getClassIdentifier, classIdentifier)
                        .eq(!StringUtils.isEmpty(attributeIdentifier), JavaToolkitAttribute::getIdentifier, attributeIdentifier)
                        .eq(Objects.nonNull(status), JavaToolkitAttribute::getStatus, status)
                        .eq(Objects.nonNull(importStatus), JavaToolkitAttribute::getImportStatus, importStatus)
                        .eq(Objects.nonNull(deleteFlag), JavaToolkitAttribute::getDeleteFlag, deleteFlag)))
                .orElse(Collections.emptyList());
    }

    @Override
    public JavaToolkitAttribute getOneByConditions(String classIdentifier, String attributeIdentifier) {
        if (StringUtils.isEmpty(classIdentifier) && StringUtils.isEmpty(attributeIdentifier)) {
            throw new NullPointerException("All of parameter 'classIdentifier' & 'attributeIdentifier' can not be null.");
        }
        return Optional.ofNullable(getOne(Wrappers.<JavaToolkitAttribute>lambdaQuery()
                        .eq(!StringUtils.isEmpty(classIdentifier), JavaToolkitAttribute::getClassIdentifier, classIdentifier)
                        .eq(!StringUtils.isEmpty(attributeIdentifier), JavaToolkitAttribute::getIdentifier, attributeIdentifier)))
                .orElse(null);
    }
}
