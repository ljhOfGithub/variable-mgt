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
package com.wiseco.var.process.app.server.commons.test;

/**
 * 测试数据在线自动生成器
 * 
 * @author Zhaoxiong Chen
 * @since 2022/1/7
 */
public interface TestDataAutoGenerator {

    /**
     * 获取随机值（可能为空）
     * 
     * @return 随机值
     */
    String getValue();
}
