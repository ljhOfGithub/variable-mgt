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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.math3.util.Pair;

import java.util.Map.Entry;
import java.util.Set;

/**
 * @author liaody
 */
public class JsonObjectNodeUtils {
    /**
     * jsonTree
     * @param e json元素
     * @param target 目标
     * @return Pair
     */
    public static Pair<Boolean, JsonElement> jsonTree(JsonElement e, String target) {
        Pair<Boolean, JsonElement> result = new Pair<>(false, null);
        if (e.isJsonNull()) {
            boolean flag = target.equals(e.toString().replace("\"", ""));

            return new Pair<>(flag, null);
        }

        if (e.isJsonPrimitive()) {
            boolean flag = target.equals(e.toString().replace("\"", ""));

            return new Pair<>(flag, null);
        }

        if (e.isJsonArray()) {
            JsonArray ja = e.getAsJsonArray();
            if (null != ja) {
                for (JsonElement ae : ja) {
                    Pair<Boolean, JsonElement> pair = jsonTree(ae, target);
                    if (pair.getKey()) {
                        if (null == pair.getValue()) {
                            result = new Pair<>(true, ae);
                        } else {
                            result = pair;
                        }

                        break;
                    }
                }
            }
            return result;
        }

        if (e.isJsonObject()) {
            Set<Entry<String, JsonElement>> es = e.getAsJsonObject().entrySet();
            for (Entry<String, JsonElement> en : es) {
                Pair<Boolean, JsonElement> pair = jsonTree(en.getValue(), target);

                if (pair.getKey()) {
                    if (null == pair.getValue()) {
                        result = new Pair<>(true, e);
                    } else {
                        result = pair;
                    }

                    break;
                }
            }

            return result;
        }

        return result;
    }

    /**
     * nodeFilter
     * @param jsonStr json字符串
     * @param target 目标
     * @return Pair
     */
    public static Pair<Boolean, JsonElement> nodeFilter(String jsonStr, String target) {
        JsonParser p = new JsonParser();
        JsonElement e = p.parse(jsonStr);
        return jsonTree(e, target);
    }

}
