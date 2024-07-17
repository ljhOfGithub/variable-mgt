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
package com.wiseco.var.process.app.server.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServiceQueryDto {

    private String keyWord;

    private String deptCode;

    private List<Long> categoryIds;

    private Long manifestId;

    private List<String> deptCodes;

    private List<String> userNames;
}
