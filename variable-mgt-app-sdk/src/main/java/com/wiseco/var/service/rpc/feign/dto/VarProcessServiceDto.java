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
package com.wiseco.var.service.rpc.feign.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@ApiModel(value = "启用状态服务列表 出参DTO")
public class VarProcessServiceDto implements Serializable {

    private static final long serialVersionUID = -6120832682080437368L;

    /**
     * 空间名称
     */
    private String  spaceName;

    /**
     * 服务id
     */
    private Long    serviceId;

    /**
     * 服务名
     */
    private String  serviceName;

    /**
     * 服务编码
     */
    private String  serviceCode;

    /**
     * 入参信息
     */
    private List<ServiceParamsDto.ParamTreeDto> requestStructure;

    /**
     * 响应参数
     */
    private List<ServiceParamsDto.ParamTreeDto> responseStructure;
}
