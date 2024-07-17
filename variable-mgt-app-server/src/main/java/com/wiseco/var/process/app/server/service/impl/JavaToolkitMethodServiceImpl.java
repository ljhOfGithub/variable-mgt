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
import com.wiseco.var.process.app.server.repository.JavaToolkitMethodMapper;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitMethod;
import com.wiseco.var.process.app.server.service.JavaToolkitMethodService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * java工具类-method表 服务实现类
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-21
 */
@Service
public class JavaToolkitMethodServiceImpl extends ServiceImpl<JavaToolkitMethodMapper, JavaToolkitMethod> implements JavaToolkitMethodService {

    @Override
    public List<JavaToolkitMethod> listByConditions(String classIdentifier, String methodIdentifier, Integer status, Integer importStatus, Integer deleteFlag) {
        if (StringUtils.isEmpty(classIdentifier) && StringUtils.isEmpty(methodIdentifier) && Objects.isNull(status) && Objects.isNull(importStatus) && Objects.isNull(deleteFlag)) {
            throw new NullPointerException("All of parameter 'classIdentifier' & 'methodIdentifier' & 'status' & 'importStatus' & 'deleteFlag' can not be null.");
        }
        return Optional.ofNullable(list(Wrappers.<JavaToolkitMethod>lambdaQuery()
                        .eq(!StringUtils.isEmpty(classIdentifier), JavaToolkitMethod::getClassIdentifier, classIdentifier)
                        .eq(!StringUtils.isEmpty(methodIdentifier), JavaToolkitMethod::getIdentifier, methodIdentifier)
                        .eq(Objects.nonNull(status), JavaToolkitMethod::getStatus, status)
                        .eq(Objects.nonNull(importStatus), JavaToolkitMethod::getImportStatus, importStatus)
                        .eq(Objects.nonNull(deleteFlag), JavaToolkitMethod::getDeleteFlag, deleteFlag)))
                .orElse(Collections.emptyList());
    }

    @Override
    public JavaToolkitMethod getOneByConditions(String classIdentifier, String methodIdentifier) {
        if (StringUtils.isEmpty(classIdentifier) && StringUtils.isEmpty(methodIdentifier)) {
            throw new NullPointerException("All of parameter 'classIdentifier' & 'methodIdentifier' can not be null.");
        }
        return Optional.ofNullable(getOne(Wrappers.<JavaToolkitMethod>lambdaQuery()
                        .eq(!StringUtils.isEmpty(classIdentifier), JavaToolkitMethod::getClassIdentifier, classIdentifier)
                        .eq(!StringUtils.isEmpty(methodIdentifier), JavaToolkitMethod::getIdentifier, methodIdentifier)))
                .orElse(null);
    }
}
