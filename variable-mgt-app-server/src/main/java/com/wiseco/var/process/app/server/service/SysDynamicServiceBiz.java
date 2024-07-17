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

import com.wiseco.var.process.app.server.service.dto.input.SysDynamicSaveInputDto;
import com.wiseco.var.process.app.server.service.dto.input.VariableDynamicSaveInputDto;

/**
 * @author wangxianli
 * @since 2022/3/2
 */
public interface SysDynamicServiceBiz {

    /**
     * 保存系统动态
     * 
     * @param inputDto 入参
     */
    void saveDynamic(SysDynamicSaveInputDto inputDto);

    /**
     * 保存系统动态-变量空间内的操作
     * 
     * @param inputDto 入参
     */
    void saveDynamicVariable(VariableDynamicSaveInputDto inputDto);

}
