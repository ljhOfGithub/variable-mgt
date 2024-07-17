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
package com.wiseco.var.process.app.server.service.multipleimpl;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class EntityConverter {

    /**
     * 实体转换工具
     * @param sourceEntity 原始实体
     * @param targetEntityType 目标实体
     * @return 目标实体对象
     * @param <T> 泛型
     * @param <U> 泛型
     */
    public static <T, U> U convertEntity(T sourceEntity, Class<U> targetEntityType) {
        try {
            U targetEntity = targetEntityType.newInstance();
            BeanUtils.copyProperties(sourceEntity, targetEntity);

            Object id = sourceEntity.getClass().getMethod("getId").invoke(sourceEntity);
            if (id != null) {
                if (id instanceof ObjectId) {
                    //id类型为ObjectId -> 转mysqlEntity
                    setIdProperty(targetEntity, Long.valueOf(String.valueOf(String.valueOf(id))));
                } else if (id instanceof Number) {
                    //id类型为number -> 转mongoEntity
                    ObjectId objectId = new ObjectId(StringUtils.leftPad(id.toString(), MagicNumbers.INT_24, '0'));
                    PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(targetEntityType, "id");
                    if (propertyDescriptor != null) {
                        Method writeMethod = propertyDescriptor.getWriteMethod();
                        if (writeMethod != null) {
                            writeMethod.invoke(targetEntity, objectId);
                        }
                    }
                }
            }
            return targetEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <U> void setIdProperty(U targetEntity, Object id) throws Exception {
        // Assuming setId method exists in the targetEntity class
        Method setIdMethod = findSetIdMethod(targetEntity.getClass(), id.getClass());
        if (setIdMethod != null) {
            setIdMethod.invoke(targetEntity, id);
        }
    }

    private static Method findSetIdMethod(Class<?> clazz, Class<?> idType) {
        try {
            return clazz.getMethod("setId", idType);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

}
