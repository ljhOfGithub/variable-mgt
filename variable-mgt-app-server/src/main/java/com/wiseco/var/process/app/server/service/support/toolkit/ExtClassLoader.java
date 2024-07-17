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
package com.wiseco.var.process.app.server.service.support.toolkit;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fudengkui
 * @since 1.0
 */
public class ExtClassLoader extends ClassLoader {

    private Map<String, byte[]> classNameBytesMapping;

    private Map<String, Class<?>> classNameClassObjectMapping = new HashMap<>(MagicNumbers.TWO_HUNDRED_AND_FIFTY_SIX);

    /**
     * 构造
     * @param classNameBytesMapping map数据
     */
    public ExtClassLoader(Map<String, byte[]> classNameBytesMapping) {
        this.classNameBytesMapping = classNameBytesMapping;
    }


    public Map<String, Class<?>> getClassNameClassObjectMapping() {
        return classNameClassObjectMapping;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (classNameBytesMapping.containsKey(className)) {
            byte[] bytes = classNameBytesMapping.get(className);
            Class<?> clazz = defineClass(className, bytes, 0, bytes.length);
            classNameClassObjectMapping.put(className, clazz);
            return clazz;
        } else {
            return null;
        }
    }

}
