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

import com.google.common.io.Files;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * jar 包处理的util
 *
 * @author wiseco
 */
public class JarUtil {
    /**
     * 提取class数据
     * 返回结果 key 示例 com.wiseco.JarUtil  value 是class内容
     *
     * @param jarData
     * @return Map
     * @throws IOException
     */
    public static Map<String, byte[]> extractClass(byte[] jarData) throws IOException {
        SeekableInMemoryByteChannel inMemoryByteChannel = null;
        InputStream inputStream = null;
        ByteArrayOutputStream output = null;
        ZipFile zipFile = null;
        try {
            inMemoryByteChannel = new SeekableInMemoryByteChannel(jarData);
            zipFile = new ZipFile(inMemoryByteChannel);

            Enumeration<ZipArchiveEntry> entrys = zipFile.getEntries();
            Set<ZipArchiveEntry> classFileSet = new HashSet<>();
            while (entrys.hasMoreElements()) {
                ZipArchiveEntry tempZip = entrys.nextElement();
                if (tempZip.getName().endsWith(".class")) {
                    classFileSet.add(tempZip);
                }
            }

            Map<String, byte[]> maps = new HashMap<>(MagicNumbers.SIXTEEN);
            for (ZipArchiveEntry zipEntry : classFileSet) {
                inputStream = zipFile.getInputStream(zipEntry);

                output = new ByteArrayOutputStream();
                byte[] buffer = new byte[MagicNumbers.INT_1024 * MagicNumbers.FOUR];
                int n = 0;
                while (MagicNumbers.MINUS_INT_1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                byte[] kk = output.toByteArray();
                String className = zipEntry.getName().replaceAll("/", ".");

                maps.put(className.replaceAll(".class", ""), kk);
            }
            return maps;
        } finally {
            if (inMemoryByteChannel != null) {
                inMemoryByteChannel.close();
            }
            if (zipFile != null) {
                zipFile.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * 分析jar
     *
     * @param jarFile
     * @return Map
     * @throws IOException
     */
    public static Map<String, byte[]> extractClassFromFile(File jarFile) throws IOException {
        if (jarFile == null) {
            return null;
        }
        byte[] jarData = Files.toByteArray(jarFile);
        return extractClass(jarData);
    }

    /**
     * 读取jar包数据里面的class
     *
     * @param jarDataLst
     * @return Map
     * @throws IOException
     */
    public static Map<String, byte[]> extractClass(List<byte[]> jarDataLst) throws IOException {
        if (jarDataLst == null || jarDataLst.isEmpty()) {
            return null;
        }
        Map<String, byte[]> mergeResult = new HashMap<>(MagicNumbers.SIXTEEN);
        for (byte[] jarData : jarDataLst) {
            mergeResult.putAll(extractClass(jarData));
        }
        return mergeResult;
    }

    /**
     * 读取jar里面的class
     *
     * @param jarDataLst
     * @return Map
     * @throws IOException
     */
    public static Map<String, byte[]> extractClassFromFile(List<File> jarDataLst) throws IOException {
        if (jarDataLst == null || jarDataLst.isEmpty()) {
            return null;
        }
        Map<String, byte[]> mergeResult = new HashMap<>(MagicNumbers.SIXTEEN);
        for (File jarData : jarDataLst) {
            if (jarData.exists() && !jarData.isDirectory()) {
                mergeResult.putAll(extractClassFromFile(jarData));
            }
        }
        return mergeResult;
    }

    /**
     * 读取jar里面的class
     *
     * @param jarDataLst
     * @return Map
     * @throws IOException
     */
    public static Map<String, byte[]> extractClassFromFile(File[] jarDataLst) throws IOException {
        if (jarDataLst == null) {
            return null;
        }
        Map<String, byte[]> mergeResult = new HashMap<>(MagicNumbers.SIXTEEN);
        for (File jarData : jarDataLst) {
            if (jarData.exists() && !jarData.isDirectory()) {
                mergeResult.putAll(extractClassFromFile(jarData));
            }
        }
        return mergeResult;
    }

    /**
     * 读取文件下class文件，并拼接全类名
     *
     * @param folderPath
     * @return Map
     * @throws IOException
     */
    public static Map<String, byte[]> readClasses(String folderPath) throws IOException {
        File folder = new File(folderPath);
        Map<String, byte[]> classMap = new HashMap<>(MagicNumbers.SIXTEEN);

        if (folder.isDirectory()) {
            readClassFiles(folder, "", classMap);
        }

        return classMap;
    }

    private static void readClassFiles(File folder, String packagePath, Map<String, byte[]> classMap) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String newPackagePath = packagePath + file.getName() + ".";
                    readClassFiles(file, newPackagePath, classMap);
                } else if (file.getName().endsWith(".class")) {
                    String className = packagePath + file.getName().replace(".class", "");
                    byte[] classBytes = FileUtils.readFileToByteArray(file);
                    classMap.put(className, classBytes);
                }
            }
        }
    }


}
