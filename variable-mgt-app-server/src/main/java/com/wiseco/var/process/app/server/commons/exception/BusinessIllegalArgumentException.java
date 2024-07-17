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
package com.wiseco.var.process.app.server.commons.exception;

/**
 * 业务逻辑上的参数验证错误
 * 
 * @author xujiawei
 * @since 2022/08/15
 */
public class BusinessIllegalArgumentException extends IllegalArgumentException {
    /**
     * BusinessIllegalArgumentException
     */
    public BusinessIllegalArgumentException() {
        super();
    }

    /**
     * BusinessIllegalArgumentException
     * 
     * @param s String
     */
    public BusinessIllegalArgumentException(String s) {
        super(s);
    }

    /**
     * BusinessIllegalArgumentException
     * 
     * @param message 异常信息
     * @param cause 原因
     */
    public BusinessIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * BusinessIllegalArgumentException
     * 
     * @param cause 原因
     */
    public BusinessIllegalArgumentException(Throwable cause) {
        super(cause);
    }
}
