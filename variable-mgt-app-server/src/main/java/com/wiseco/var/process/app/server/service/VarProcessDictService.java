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
import com.wiseco.var.process.app.server.controller.vo.input.DictListInputDto;
import com.wiseco.var.process.app.server.repository.entity.VarProcessDict;
import com.wiseco.var.process.app.server.service.dto.DictDetailsDto;

import java.util.List;

/**
 * <p>
 * 变量空间-字典类型表 服务类
 * </p>
 *
 * @author wiseco
 * @since 2022-06-07
 */
public interface VarProcessDictService extends IService<VarProcessDict> {
    /**
     * getDictDetails
     *
     * @param spaceId 变量空间Id
     * @return 字典
     */
    List<DictDetailsDto> getDictDetails(Long spaceId);

    /**
     * 获取所有的字典列表
     *
     * @param inputDto 输入实体类对象
     * @return 字典列表
     */
    List<VarProcessDict> getAllList(DictListInputDto inputDto);

    /**
     * 通过code，获取dict的id
     * @param code 编码
     * @return dict的id
     */
    Long getIdByCode(String code);
}
