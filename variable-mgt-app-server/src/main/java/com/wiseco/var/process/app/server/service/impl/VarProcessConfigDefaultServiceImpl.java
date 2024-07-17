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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiseco.var.process.app.server.repository.VarProcessConfigDefaultMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessConfigDefault;
import com.wiseco.var.process.app.server.service.VarProcessConfigDefaultService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 变量空间-变量异常值配置
 * </p>
 *
 * @author kangyankun
 * @since 2022-08-31
 */

@Service
public class VarProcessConfigDefaultServiceImpl extends ServiceImpl<VarProcessConfigDefaultMapper, VarProcessConfigDefault> implements
        VarProcessConfigDefaultService {

}