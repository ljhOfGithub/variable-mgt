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
import com.wiseco.var.process.app.server.commons.enums.EnableDisableEnum;
import com.wiseco.var.process.app.server.commons.enums.ImportStatusEnum;
import com.wiseco.var.process.app.server.repository.JavaToolkitClassMapper;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitClass;
import com.wiseco.var.process.app.server.service.JavaToolkitClassService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * java工具类-class类表 服务实现类
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-21
 */
@Service
public class JavaToolkitClassServiceImpl extends ServiceImpl<JavaToolkitClassMapper, JavaToolkitClass> implements JavaToolkitClassService {

    @Override
    public List<JavaToolkitClass> listByConditions(String jarIdentifier, String classIdentifier, Integer status, Integer importStatus, Integer deleteFlag) {
        if (StringUtils.isEmpty(jarIdentifier) && StringUtils.isEmpty(classIdentifier) && Objects.isNull(status) && Objects.isNull(importStatus) && Objects.isNull(deleteFlag)) {
            throw new NullPointerException("All of parameter 'jarIdentifier' & 'classIdentifier' & 'status' & 'importStatus' & 'deleteFlag' can not be null.");
        }
        return Optional.ofNullable(list(Wrappers.<JavaToolkitClass>lambdaQuery()
                        .select(JavaToolkitClass::getId, JavaToolkitClass::getIdentifier,
                                JavaToolkitClass::getJarIdentifier, JavaToolkitClass::getName,
                                JavaToolkitClass::getCanonicalName, JavaToolkitClass::getLabel,
                                JavaToolkitClass::getJarName, JavaToolkitClass::getClassType,
                                JavaToolkitClass::getModifier, JavaToolkitClass::getStatus,
                                JavaToolkitClass::getImportStatus, JavaToolkitClass::getClassBizType,
                                JavaToolkitClass::getCreatedUser, JavaToolkitClass::getUpdatedUser,
                                JavaToolkitClass::getCreatedTime, JavaToolkitClass::getUpdatedTime)
                        .eq(!StringUtils.isEmpty(jarIdentifier), JavaToolkitClass::getJarIdentifier, jarIdentifier)
                        .eq(!StringUtils.isEmpty(classIdentifier), JavaToolkitClass::getIdentifier, classIdentifier)
                        .eq(Objects.nonNull(status), JavaToolkitClass::getStatus, status)
                        .eq(Objects.nonNull(importStatus), JavaToolkitClass::getImportStatus, importStatus)
                        .eq(Objects.nonNull(deleteFlag), JavaToolkitClass::getDeleteFlag, deleteFlag)))
                .orElse(Collections.emptyList());
    }

    @Override
    public JavaToolkitClass getOneByConditions(String jarIdentifier, String classIdentifier) {
        if (StringUtils.isEmpty(jarIdentifier) && StringUtils.isEmpty(classIdentifier)) {
            throw new NullPointerException("All of parameter 'jarIdentifier' & 'classIdentifier' can not be null.");
        }
        return getOne(Wrappers.<JavaToolkitClass>lambdaQuery()
                        .select(JavaToolkitClass::getId, JavaToolkitClass::getIdentifier,
                                JavaToolkitClass::getJarIdentifier, JavaToolkitClass::getName,
                                JavaToolkitClass::getCanonicalName, JavaToolkitClass::getLabel,
                                JavaToolkitClass::getJarName, JavaToolkitClass::getClassType,
                                JavaToolkitClass::getModifier, JavaToolkitClass::getStatus,
                                JavaToolkitClass::getImportStatus, JavaToolkitClass::getClassBizType,
                                JavaToolkitClass::getCreatedUser, JavaToolkitClass::getUpdatedUser,
                                JavaToolkitClass::getCreatedTime, JavaToolkitClass::getUpdatedTime,
                                JavaToolkitClass::getDeleteFlag, JavaToolkitClass::getAttributeJsonSchema)
                        .eq(!StringUtils.isEmpty(jarIdentifier), JavaToolkitClass::getJarIdentifier, jarIdentifier)
                        .eq(!StringUtils.isEmpty(classIdentifier), JavaToolkitClass::getIdentifier, classIdentifier));
    }

    @Override
    public JavaToolkitClass getOneByCanonicalName(String canonicalName) {
        if (StringUtils.isEmpty(canonicalName)) {
            throw new NullPointerException("Parameter 'canonicalName' can not be null.");
        }
        return Optional.ofNullable(getOne(Wrappers.<JavaToolkitClass>lambdaQuery()
                        .select(JavaToolkitClass::getId, JavaToolkitClass::getAttributeJsonSchema)
                        .eq(JavaToolkitClass::getStatus, EnableDisableEnum.ENABLE.getValue())
                        .eq(JavaToolkitClass::getImportStatus, ImportStatusEnum.IMPORTED.getStatus())
                        .eq(JavaToolkitClass::getDeleteFlag, DeleteFlagEnum.USABLE.getCode())
                        .eq(JavaToolkitClass::getCanonicalName, canonicalName)))
                .orElse(null);
    }
}
