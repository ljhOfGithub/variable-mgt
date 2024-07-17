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
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author fudengkui
 * @since 1.0
 */
public class JarCompressSupport {

    /**
     * transfer InputStream to ByteArrayOutputStream
     *
     * @param is 输入流
     * @throws  IOException io异常
     * @return ByteArrayOutputStream
     */
    public static ByteArrayOutputStream write(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int n;
        byte[] buf = new byte[MagicNumbers.THOUSAND_AND_TWENTY_FOUR * MagicNumbers.FOUR];
        while ((n = is.read(buf)) != MagicNumbers.MINUS_INT_1) {
            bos.write(buf, 0, n);
        }
        return bos;
    }

    /**
     * unzip in memory
     *
     * @param bytes 字节流
     * @throws IOException io异常
     * @return map的集合
     */
    public static Map<String, byte[]> unZipInMemory(byte[] bytes) throws IOException {
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try (SeekableInMemoryByteChannel seekableInMemoryByteChannel = new SeekableInMemoryByteChannel(bytes);
             ZipFile zipFile = new ZipFile(seekableInMemoryByteChannel)) {
            Enumeration<ZipArchiveEntry> zipFileEntries = zipFile.getEntries();
            Set<ZipArchiveEntry> zipArchiveEntries = new HashSet<>();
            while (zipFileEntries.hasMoreElements()) {
                ZipArchiveEntry zipArchiveEntry = zipFileEntries.nextElement();
                if (zipArchiveEntry.getName().endsWith(".class")) {
                    zipArchiveEntries.add(zipArchiveEntry);
                }
            }

            Map<String, byte[]> classNameBytesMapping = new HashMap<>(MagicNumbers.TWO_HUNDRED_AND_FIFTY_SIX);
            for (ZipArchiveEntry zipArchiveEntry : zipArchiveEntries) {
                is = zipFile.getInputStream(zipArchiveEntry);
                os = new ByteArrayOutputStream();
                byte[] buffer = new byte[MagicNumbers.THOUSAND_AND_TWENTY_FOUR * MagicNumbers.FOUR];
                int n;
                while (MagicNumbers.MINUS_INT_1 != (n = is.read(buffer))) {
                    os.write(buffer, 0, n);
                }
                byte[] classBytes = os.toByteArray();
                String className = zipArchiveEntry.getName().replaceAll("/", ".");
                classNameBytesMapping.put(className.replaceAll(".class", ""), classBytes);
            }
            return classNameBytesMapping;
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

}
