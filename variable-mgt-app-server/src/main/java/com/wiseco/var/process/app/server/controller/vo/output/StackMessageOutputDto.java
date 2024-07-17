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
package com.wiseco.var.process.app.server.controller.vo.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wangxianli
 * @date 2022/06/22
 */
@Data
@ApiModel(value = "堆栈信息返回DTO")
public class StackMessageOutputDto {

    @ApiModelProperty(value = "验证状态：true通过，false失败", example = "true")
    private boolean state;

    @ApiModelProperty(value = "错误简单提示", example = "null")
    private String message;

    @ApiModelProperty(value = "堆栈信息", example = "null")
    private String stackMessage;


}

