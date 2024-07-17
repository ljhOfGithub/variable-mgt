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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页父类
 * @author yangyunsen
 */
@ApiModel(value = "分页父类")
@Data
public class PageDto {

    private static final int TEN = 10;

    @ApiModelProperty(value = "页码", required = true, example = "1")
    private int currentNo = 1;

    @ApiModelProperty(value = "页面条数", required = true, example = "10")
    private int size = TEN;

    /**
     * generatePage
     * @return 分页查询的结果
     * @param <T> 泛型类
     */
    public <T> Page<T> generatePage() {
        //不能使用getPage作为方法名，否则swagger会把这个返回的Page当成属性
        return new Page<T>(currentNo, size);
    }
}
