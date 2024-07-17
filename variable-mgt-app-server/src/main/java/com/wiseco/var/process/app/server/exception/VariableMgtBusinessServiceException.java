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
package com.wiseco.var.process.app.server.exception;

import com.wiseco.boot.commons.exception.BusinessServiceException;
import com.wiseco.boot.commons.exception.WisecoErrorCode;

/**
 * 睿信自定义异常
 * 
 * @author Gmm
 * @date 2023/12/12
 */
public class VariableMgtBusinessServiceException extends BusinessServiceException {

    /**
     * 睿信业务异常
     * 
     * @param errorCode 异常码
     * @param errorMessage 错误消息
     * @param cause 异常
     */
    public VariableMgtBusinessServiceException(WisecoErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }

    /**
     * 睿信业务异常
     * 
     * @param errorCode 异常码
     * @param errorMessage 错误消息
     */
    public VariableMgtBusinessServiceException(WisecoErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 睿信业务异常
     * 
     * @param errorCode 睿信自定义异常码
     */
    public VariableMgtBusinessServiceException(VariableMgtErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    /**
     * 睿信业务异常
     * 
     * @param errorMessage 错误消息
     */
    public VariableMgtBusinessServiceException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * 睿信业务异常
     * 
     * @param errorMessage 错误消息
     * @param cause 异常
     */
    public VariableMgtBusinessServiceException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
