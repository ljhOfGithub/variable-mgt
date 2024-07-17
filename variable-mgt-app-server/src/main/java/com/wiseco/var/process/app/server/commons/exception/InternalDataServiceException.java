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

import java.text.MessageFormat;

/**
 * 业务异常
 */
public class InternalDataServiceException extends RuntimeException {
    private static final long serialVersionUID = -6406707333660833511L;

    private Integer code;
    private String msg;

    /**
     * construct
     */
    public InternalDataServiceException() {
        super("业务异常！");
        this.code = BizExceptionMessage.UNKNOWN_ERROR.getCode();
        this.msg = BizExceptionMessage.UNKNOWN_ERROR.getMsg();
    }

    /**
     * construct
     *
     * @param errorMessage 错误信息
     */
    public InternalDataServiceException(String errorMessage) {
        super(errorMessage);
        this.code = BizExceptionMessage.UNKNOWN_ERROR.getCode();
        this.msg = errorMessage;
    }

    /**
     * InternalDataServiceException
     *
     * @param message 业务异常信息
     */
    public InternalDataServiceException(BizExceptionMessage message) {
        super(message.getMsg());
        this.code = message.getCode();
        this.msg = message.getMsg();
    }

    /**
     * 报错信息包含可变参数，params按照参数顺序依次定义
     *
     * @param message 业务异常信息
     * @param params 参数数组
     */
    public InternalDataServiceException(BizExceptionMessage message, Object[] params) {
        super(message.getMsg());
        this.code = message.getCode();
        this.msg = MessageFormat.format(message.getMsg(), params);
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 自定义异常忽略异常栈信息的打印
     *
     * @return throwable
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}

