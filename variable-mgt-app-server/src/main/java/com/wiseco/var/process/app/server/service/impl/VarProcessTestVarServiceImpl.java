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
import com.wiseco.var.process.app.server.repository.VarProcessTestVarMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessTestVar;
import com.wiseco.var.process.app.server.service.VarProcessTestVarService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 变量测试-变量引用关系表 服务实现类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-09
 */
@Service
public class VarProcessTestVarServiceImpl extends ServiceImpl<VarProcessTestVarMapper, VarProcessTestVar> implements VarProcessTestVarService {

}
