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
import com.wiseco.var.process.app.server.repository.JavaToolkitParameterMapper;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitParameter;
import com.wiseco.var.process.app.server.service.JavaToolkitParameterService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * java工具类-parameter表 服务实现类
 * </p>
 *
 * @author fudengkui
 * @since 2023-02-22
 */
@Service
public class JavaToolkitParameterServiceImpl extends ServiceImpl<JavaToolkitParameterMapper, JavaToolkitParameter> implements
        JavaToolkitParameterService {

    @Override
    public List<JavaToolkitParameter> listByConditions(String methodIdentifier, String parameterIdentifier, Integer deleteFlag) {
        if (StringUtils.isEmpty(methodIdentifier) && StringUtils.isEmpty(parameterIdentifier) && Objects.isNull(deleteFlag)) {
            throw new NullPointerException("All of parameter 'methodIdentifier' & 'parameterIdentifier' & 'deleteFlag' can not be null.");
        }
        return Optional.ofNullable(list(Wrappers.<JavaToolkitParameter>lambdaQuery()
                        .eq(!StringUtils.isEmpty(methodIdentifier), JavaToolkitParameter::getMethodIdentifier, methodIdentifier)
                        .eq(!StringUtils.isEmpty(parameterIdentifier), JavaToolkitParameter::getIdentifier, parameterIdentifier)
                        .eq(Objects.nonNull(deleteFlag), JavaToolkitParameter::getDeleteFlag, deleteFlag)
                        .orderByAsc(JavaToolkitParameter::getIdx)))
                .orElse(Collections.emptyList());
    }

    @Override
    public JavaToolkitParameter getOneByConditions(String methodIdentifier, String parameterIdentifier) {
        if (StringUtils.isEmpty(methodIdentifier) && StringUtils.isEmpty(parameterIdentifier)) {
            throw new NullPointerException("All of parameter 'methodIdentifier' & 'parameterIdentifier' can not be null.");
        }
        return Optional.ofNullable(getOne(Wrappers.<JavaToolkitParameter>lambdaQuery()
                        .eq(!StringUtils.isEmpty(methodIdentifier), JavaToolkitParameter::getMethodIdentifier, methodIdentifier)
                        .eq(!StringUtils.isEmpty(parameterIdentifier), JavaToolkitParameter::getIdentifier, methodIdentifier)))
                .orElse(null);
    }
}
