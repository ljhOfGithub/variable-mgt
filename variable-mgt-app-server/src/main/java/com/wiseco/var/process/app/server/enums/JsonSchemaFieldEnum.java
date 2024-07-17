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
package com.wiseco.var.process.app.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xiewu
 */
@AllArgsConstructor
@Getter
public enum JsonSchemaFieldEnum {

    /**
     * title
     */
    TITLE_FIELD("title"),
    /**
     *  description
     */
    DESCRIPTION_FIELD("description"),
    /**
     * type
     */
    TYPE_FIELD("type"),
    /**
     * properties
     */
    PROPERTIES_FIELD("properties"),
    /**
     * required
     */
    REQUIRED_FIELD("required"),
    /**
     * items
     */
    ITEMS_FIELD("items"),
    /**
     * array
     */
    ARRAY_FIELD("array"),
    /**
     * isEnum
     */
    IS_ENUM_FIELD("isEnum"),
    /**
     * extend
     */
    EXTEND("extend");


    private String message;

}
