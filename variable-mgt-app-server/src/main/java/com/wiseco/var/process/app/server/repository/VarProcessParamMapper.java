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
package com.wiseco.var.process.app.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wiseco.var.process.app.server.repository.entity.VarProcessParameter;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 变量空间 - 通用配置参数定义- 审核参数 Mapper
 * </p>
 *
 * @author guozhuoyi
 * @since 2023/8/3
 */
@Mapper
public interface VarProcessParamMapper extends BaseMapper<VarProcessParameter> {
}
