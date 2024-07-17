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
package com.wiseco.var.process.app.server.service.dto.output;

import com.wiseco.var.process.app.server.repository.entity.JavaToolkitClass;
import com.wiseco.var.process.app.server.repository.entity.JavaToolkitMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: fudengkui
 * @since: 2023-03-15 19:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JavaToolkitClassMethodsInfoDTO implements Serializable {

    private JavaToolkitClass javaToolkitClass;

    private List<JavaToolkitMethod> javaToolkitMethods;

}
