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
package com.wiseco.var.process.app.server.commons.util;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author liaody
 * @Description
 * @create 2021/11/19
 */
public class GenerateIdUtil {
    private static final UniqueTimestamp UT = new UniqueTimestamp();
    public static final int SIZE = 16;
    private static final int INT_255 = 0xff;

    /**
     * 生成id
     * 
     * @return String
     */
    public static String generateId() {
        synchronized (UT) {
            return StringUtils.leftPad(Long.toHexString(UT.getUniqueTimestamp()).toUpperCase(), SIZE, '0');
        }
    }

    /**
     * 根据业务标识结合时间戳 生成授权码
     * 
     * @param businessIdentifier 唯一标识符
     * @return java.lang.String
     */
    public static synchronized String generateRandomCode(String businessIdentifier) {
        // 结合业务标识和时间戳
        String source = businessIdentifier + System.nanoTime();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(INT_255 & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            // 转换为指定字符集
            return hexString.toString().toUpperCase().replaceAll("[^A-Z0-9]", "").substring(0, MagicNumbers.SIXTEEN);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
