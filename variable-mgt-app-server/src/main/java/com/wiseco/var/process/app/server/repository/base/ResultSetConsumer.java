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
package com.wiseco.var.process.app.server.repository.base;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author xujiawei
 * @since 2021/12/20
 */
public interface ResultSetConsumer {
    /**
     * 函数式接口，消费jdbc的ResultSet
     * 
     * @param resultSet jdbc查出来的resultSet
     * @throws SQLException JDBC异常直接上抛
     */
    void accept(ResultSet resultSet) throws SQLException;
}
