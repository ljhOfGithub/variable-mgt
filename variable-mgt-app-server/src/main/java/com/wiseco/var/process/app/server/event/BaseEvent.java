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
package com.wiseco.var.process.app.server.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author xupei
 * @param <T> 泛型
 */
public class BaseEvent<T> extends ApplicationEvent {

    T data;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public BaseEvent(T source) {
        super(source);
        this.data = source;
    }

    public T getData() {
        return this.data;
    }
}
