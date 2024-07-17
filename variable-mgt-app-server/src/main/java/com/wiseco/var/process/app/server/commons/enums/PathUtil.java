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
package com.wiseco.var.process.app.server.commons.enums;

import cn.hutool.core.date.DateUtil;
import com.wiseco.boot.commons.util.DateTimeUtils;
import com.wiseco.model.common.utils.oss.OssHelper;
import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xujiawei
 * @since 2022/08/16
 */
@Slf4j
public class PathUtil {
    /**
     * 模型文档目录名
     */
    public static final String MODEL_DOCUMENT_DIRECTORY_NAME = "modelDoc";

    /**
     * 模型版本下的模型文件目录名
     */
    public static final String MODEL_VERSION_FILE_DIRECTORY_NAME = "modelFile";

    /**
     * 模型开发项目下的模型文件（开发）文件夹
     */
    public static final String MODEL_PRODUCT_FILE_DEV_NAME = "modelFileDev";

    /**
     * 模型开发项目下的模型文件（验证）文件夹
     */
    public static final String MODEL_PRODUCT_FILE_PRE_NAME = "modelFilePre";

    /**
     * 模型代码目录名
     */
    public static final String MODEL_CODE_DIRECTORY_NAME = "modelCode";

    /**
     * 模型训练数据集目录名
     */
    public static final String MODEL_DATA_DIRECTORY_NAME = "modelDataset";

    /**
     * 模型目录下的模型版本目录名前缀
     */
    public static final String MODEL_VERSION_DIRECTORY_PREFIX = "v";

    /**
     * 展示给前端时，模型目录下的模型版本目录名前缀
     */
    public static final String MODEL_VERSION_DIRECTORY_DESCRIPTION_PREFIX = "版本";

    /**
     * 模型目录下的模型开发项目目录名前缀
     */
    public static final String MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX = "p";

    /**
     * 展示给前端时，模型目录下的模型开发项目目录名前缀
     */
    public static final String MODEL_DEVELOPMENT_PROJECT_DIRECTORY_DESCRIPTION_PREFIX = "项目";
    public static final String SLASH = "/";

    /**
     * 模型开发项目或者模型版本下的四个子目录的中英文的mapping，展示给前端时转换
     */
    public static final Map<String, String> SUB_DIRECTORY_NAME_MAPPING;

    static {
        Map<String, String> tempMap = new HashMap<>(MagicNumbers.EIGHT);
        tempMap.put(MODEL_DOCUMENT_DIRECTORY_NAME, "模型文档");
        tempMap.put(MODEL_VERSION_FILE_DIRECTORY_NAME, "模型文件");
        tempMap.put(MODEL_CODE_DIRECTORY_NAME, "模型代码");
        tempMap.put(MODEL_DATA_DIRECTORY_NAME, "模型数据集");
        tempMap.put(MODEL_PRODUCT_FILE_DEV_NAME, "模型文件(开发)");
        tempMap.put(MODEL_PRODUCT_FILE_PRE_NAME, "模型文件(验证)");
        SUB_DIRECTORY_NAME_MAPPING = Collections.unmodifiableMap(tempMap);
    }

    /**
     * 生成模型空间在OSS中的文件目录路径
     *
     * @param workspaceCode 模型空间编码
     * @return 模型空间在OSS中的文件目录路径
     */
    public static String generateWorkspaceDirectoryPath(String workspaceCode) {
        return workspaceCode + SLASH;
    }

    /**
     * 生成模型在OSS中的文件目录路径
     *
     * @param workspaceCode 模型空间编码
     * @param modelCode     模型编码
     * @return 模型在OSS中的文件目录路径
     */
    public static String generateModelDirectoryPath(String workspaceCode, String modelCode) {
        return workspaceCode + SLASH + modelCode + SLASH;
    }

    /**
     * 生成模型版本在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelVersionCode 模型版本编码，使用大版本号和小版本号拼凑，使用{@link #generateModelVersionCode}方法
     * @return 模型版本在OSS中的文件目录路径
     */
    public static String generateModelVersionDirectoryPath(String workspaceCode, String modelCode, String modelVersionCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_VERSION_DIRECTORY_PREFIX + modelVersionCode + SLASH;
    }

    /**
     * 生成模型开发项目在OSS中的文件目录路径
     *
     * @param workspaceCode 模型空间编码
     * @param modelCode     模型编码
     * @param projectCode   模型开发项目编码
     * @return 模型开发项目在OSS中的文件目录路径
     */
    public static String generateModelDevelopmentProjectDirectoryPath(String workspaceCode, String modelCode, String projectCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + projectCode + SLASH;
    }

    /**
     * 生成模型版本下的模型文档在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelVersionCode 模型版本编码，使用大版本号和小版本号拼凑，使用{@link #generateModelVersionCode}方法
     * @return 模型版本下的模型文档在OSS中的文件目录路径
     */
    public static String generateModelVersionDocumentDirectoryPath(String workspaceCode, String modelCode, String modelVersionCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_VERSION_DIRECTORY_PREFIX + modelVersionCode + SLASH + MODEL_DOCUMENT_DIRECTORY_NAME + SLASH;
    }

    /**
     * 生成模型版本下的模型文件在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelVersionCode 模型版本编码，使用大版本号和小版本号拼凑，使用{@link #generateModelVersionCode}方法
     * @return 模型版本下的模型文件在OSS中的文件目录路径
     */
    public static String generateModelVersionFileDirectoryPath(String workspaceCode, String modelCode, String modelVersionCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_VERSION_DIRECTORY_PREFIX + modelVersionCode + SLASH + MODEL_VERSION_FILE_DIRECTORY_NAME
                + SLASH;
    }

    /**
     * 生成模型版本下的模型代码在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelVersionCode 模型版本编码，使用大版本号和小版本号拼凑，使用{@link #generateModelVersionCode}方法
     * @return 模型版本下的模型代码在OSS中的文件目录路径
     */
    public static String generateModelVersionCodeDirectoryPath(String workspaceCode, String modelCode, String modelVersionCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_VERSION_DIRECTORY_PREFIX + modelVersionCode + SLASH + MODEL_CODE_DIRECTORY_NAME + SLASH;
    }

    /**
     * 生成模型版本下的模型数据集在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelVersionCode 模型版本编码，使用大版本号和小版本号拼凑，使用{@link #generateModelVersionCode}方法
     * @return 模型版本下的模型数据集在OSS中的文件目录路径
     */
    public static String generateModelVersionDataDirectoryPath(String workspaceCode, String modelCode, String modelVersionCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_VERSION_DIRECTORY_PREFIX + modelVersionCode + SLASH + MODEL_DATA_DIRECTORY_NAME + SLASH;
    }

    /**
     * 生成模型版本下的模型文档在OSS中的文件路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelVersionCode 模型版本编码，使用大版本号和小版本号拼凑，使用{@link #generateModelVersionCode}方法
     * @param fileName         模型文档文件名
     * @return 模型版本下的模型文档在OSS中的文件路径
     */
    public static String generateModelVersionDocumentPath(String workspaceCode, String modelCode, String modelVersionCode, String fileName) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_VERSION_DIRECTORY_PREFIX + modelVersionCode + SLASH + MODEL_DOCUMENT_DIRECTORY_NAME + SLASH
                + fileName;
    }

    /**
     * 生成模型版本下的模型文件在OSS中的文件路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelVersionCode 模型版本编码，使用大版本号和小版本号拼凑，使用{@link #generateModelVersionCode}方法
     * @param fileName         模型文件文件名
     * @return 模型版本下的模型文件在OSS中的文件路径
     */
    public static String generateModelVersionFilePath(String workspaceCode, String modelCode, String modelVersionCode, String fileName) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_VERSION_DIRECTORY_PREFIX + modelVersionCode + SLASH + MODEL_VERSION_FILE_DIRECTORY_NAME
                + SLASH + fileName;
    }

    /**
     * 生成模型版本下的模型代码在OSS中的文件路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelVersionCode 模型版本编码，使用大版本号和小版本号拼凑，使用{@link #generateModelVersionCode}方法
     * @param fileName         模型代码文件名
     * @return 模型版本下的模型代码在OSS中的文件路径
     */
    public static String generateModelVersionCodePath(String workspaceCode, String modelCode, String modelVersionCode, String fileName) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_VERSION_DIRECTORY_PREFIX + modelVersionCode + SLASH + MODEL_CODE_DIRECTORY_NAME + SLASH
                + fileName;
    }

    /**
     * 生成模型版本下的模型数据集在OSS中的文件路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelVersionCode 模型版本编码，使用大版本号和小版本号拼凑，使用{@link #generateModelVersionCode}方法
     * @param fileName         模型数据集文件名
     * @return 模型版本下的模型数据集在OSS中的文件路径
     */
    public static String generateModelVersionDataPath(String workspaceCode, String modelCode, String modelVersionCode, String fileName) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_VERSION_DIRECTORY_PREFIX + modelVersionCode + SLASH + MODEL_DATA_DIRECTORY_NAME + SLASH
                + fileName;
    }

    /**
     * 生成模型开发项目下的模型文档在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @return 模型开发项目下的模型文档在OSS中的文件目录路径
     */
    public static String generateModelProjectDocumentDirectoryPath(String workspaceCode, String modelCode, String modelProjectCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_DOCUMENT_DIRECTORY_NAME + SLASH;
    }

    /**
     * 生成模型开发项目下的模型文件(开发)在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @return 模型开发项目下的模型文件(开发)在OSS中的文件目录路径
     */
    public static String generateModelProjectFileDevDirectoryPath(String workspaceCode, String modelCode, String modelProjectCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_PRODUCT_FILE_DEV_NAME + SLASH;
    }

    /**
     * 生成模型开发项目下的模型文件(验证)在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @return 模型开发项目下的模型文件(验证)在OSS中的文件目录路径
     */
    public static String generateModelProjectFilePreDirectoryPath(String workspaceCode, String modelCode, String modelProjectCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_PRODUCT_FILE_PRE_NAME + SLASH;
    }

    /**
     * 生成模型开发项目下的模型代码在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @return 模型开发项目下的模型代码在OSS中的文件目录路径
     */
    public static String generateModelProjectCodeDirectoryPath(String workspaceCode, String modelCode, String modelProjectCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_CODE_DIRECTORY_NAME + SLASH;
    }

    /**
     * 生成模型开发项目下的模型数据集在OSS中的文件目录路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @return 模型开发项目下的模型数据集在OSS中的文件目录路径
     */
    public static String generateModelProjectDataDirectoryPath(String workspaceCode, String modelCode, String modelProjectCode) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_DATA_DIRECTORY_NAME + SLASH;
    }

    /**
     * 生成模型开发项目下的模型文档在OSS中的文件路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @param fileName         模型文档文件名
     * @return 模型开发项目下的模型文档在OSS中的文件路径
     */
    public static String generateModelProjectDocumentPath(String workspaceCode, String modelCode, String modelProjectCode, String fileName) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_DOCUMENT_DIRECTORY_NAME + SLASH + fileName;
    }

    /**
     * 生成模型开发项目下的模型文件(开发)在OSS中的文件路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @param fileName         模型文件文件名
     * @return 模型开发项目下的模型文件(开发)在OSS中的文件路径
     */
    public static String generateModelProjectDevFilePath(String workspaceCode, String modelCode, String modelProjectCode, String fileName) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_PRODUCT_FILE_DEV_NAME + SLASH + fileName;
    }

    /**
     * 生成模型开发项目下的模型文件(验证)在OSS中的文件路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @param fileName         模型文件文件名
     * @return 模型开发项目下的模型文件(验证)在OSS中的文件路径
     */
    public static String generateModelProjectPreFilePath(String workspaceCode, String modelCode, String modelProjectCode, String fileName) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_PRODUCT_FILE_PRE_NAME + SLASH + fileName;
    }

    /**
     * 生成模型开发项目下的模型代码在OSS中的文件路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @param fileName         模型代码文件名
     * @return 模型开发项目下的模型代码在OSS中的文件路径
     */
    public static String generateModelProjectCodePath(String workspaceCode, String modelCode, String modelProjectCode, String fileName) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_CODE_DIRECTORY_NAME + SLASH + fileName;
    }

    /**
     * 生成模型开发项目下的模型数据集在OSS中的文件路径
     *
     * @param workspaceCode    模型空间编码
     * @param modelCode        模型编码
     * @param modelProjectCode 模型开发项目编码
     * @param fileName         模型数据集文件名
     * @return 模型开发项目下的模型数据集在OSS中的文件路径
     */
    public static String generateModelProjectDataPath(String workspaceCode, String modelCode, String modelProjectCode, String fileName) {
        return workspaceCode + SLASH + modelCode + SLASH + MODEL_DEVELOPMENT_PROJECT_DIRECTORY_PREFIX + modelProjectCode + SLASH
                + MODEL_DATA_DIRECTORY_NAME + SLASH + fileName;
    }

    /**
     * 生成模型开发项目的编码：v大版本号.小版本号
     *
     * @param major 大版本号
     * @param minor 小版本号
     * @return 模型开发项目编码
     */
    public static String generateModelVersionCode(@NotNull Integer major, @NotNull Integer minor) {
        return "v" + major + "." + minor;
    }

    /**
     * 从modelVersionCode中还原出major和minor
     * @param modelVersionCode 模型版本的code
     * @return int[]
     */
    public static int[] splitModelVersionCode(String modelVersionCode) {
        String version = modelVersionCode.replaceAll("v", "");
        int index = version.indexOf(".");
        return new int[]{Integer.parseInt(version.substring(0, index)), Integer.parseInt(version.substring(index + 1))};
    }

    /**
     * 模型版本名称（与OSS目录名一致）
     * @param major 主版本
     * @param minor 从版本
     * @return String
     */
    public static String generateModelVersionCodeForOssDirName(@NotNull Integer major, @NotNull Integer minor) {
        return MODEL_VERSION_DIRECTORY_PREFIX + generateModelVersionCode(major, minor);
    }

    /**
     * 生成空间下设置的样本存储路径
     * @param workspaceCode 工作空间code
     * @param storagePath 存储路径
     * @param dynamicDir 动态文件夹路径
     * @param fileName 文件名
     * @return String
     */
    public static String generateDataStorageOssDirName(@NotNull String workspaceCode, @NotNull String storagePath, @NotNull String dynamicDir,
                                                       @NotNull String fileName) {
        return workspaceCode + formDirPath(storagePath, dynamicDir) + fileName;
    }

    /**
     * 组合路径
     * @param paths 多个路径
     * @return String
     */
    public static String formDirPath(String... paths) {
        String path = SLASH;
        if (null != paths && paths.length > 0) {
            path = Arrays.stream(paths)
                    .filter(str -> !StringUtils.isEmpty(str))
                    .map(PathUtil::removeStartSeparator)
                    .map(PathUtil::removeEndSeparator)
                    .collect(Collectors.joining(SLASH));
        }
        return addEndSeparator(addStartSeparator(path));
    }

    /**
     * 路径开头追加/
     *
     * @param path 路径
     * @return String
     */
    public static String addStartSeparator(String path) {
        if (StringUtils.isEmpty(path)) {
            return SLASH;
        }
        if (!path.startsWith(SLASH)) {
            path = SLASH + path;
        }
        return path;
    }

    /**
     * 路径末尾追加/
     *
     * @param path 路径
     * @return String
     */
    public static String addEndSeparator(String path) {
        if (StringUtils.isEmpty(path)) {
            return SLASH;
        }
        if (!path.endsWith(SLASH)) {
            path = path + SLASH;
        }
        return path;
    }

    /**
     * 路径开头追加/
     *
     * @param path 路径
     * @return String
     */
    public static String removeStartSeparator(String path) {
        path = path.trim();
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        if (path.startsWith(SLASH)) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * 路径末尾追加/
     *
     * @param path 路径
     * @return String
     */
    public static String removeEndSeparator(String path) {
        path = path.trim();
        if (StringUtils.isEmpty(path)) {
            return SLASH;
        }
        if (path.endsWith(SLASH)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * getFileName
     * @param path 路径
     * @return 文件名，若路径不是指向文件，则返回null
     */
    @Nullable
    public static String getFileName(String path) {
        path = path.trim();
        if (path.endsWith(OssHelper.SEP)) {
            return null;
        }
        return path.substring(path.lastIndexOf(OssHelper.SEP) + 1);
    }


    /**
     * 替换时间格式
     * @param arg 参数
     * @param validDate 校验格式
     * @param date 时间对象
     * @return java.lang.String
     */
    public static String replaceDateFormat(String arg, String validDate, Date date) {
        String format;
        int startIndex;
        int endIndex;
        String dateFormat;
        log.info("时间替换前：{}", arg);
        if (arg.contains(StringPool.REPLACE_START) && arg.contains(StringPool.REPLACE_END)) {
            while ((startIndex = arg.indexOf(StringPool.REPLACE_START)) != MagicNumbers.MINUS_INT_1 && (endIndex = arg.indexOf(StringPool.REPLACE_END)) != MagicNumbers.MINUS_INT_1) {
                format = arg.substring(startIndex, endIndex + 1);
                if (org.apache.commons.lang3.StringUtils.equals("0", validDate)) {
                    dateFormat = DateUtil.format(null == date ? new Date() : date, format.substring(MagicNumbers.TWO, format.length() - 1));
                } else {
                    dateFormat = DateUtil.format(
                            null == date ? DateTimeUtils.parseDate(LocalDateTime.now().minusDays(1)) : DateTimeUtils.parseDate(DateTimeUtils.parseLdt(
                                    date).minusDays(1)), format.substring(MagicNumbers.TWO, format.length() - 1));
                }
                arg = arg.substring(0, startIndex) + dateFormat + arg.substring(endIndex + 1);
            }
        }
        log.info("时间替换后：{}", arg);
        return arg;
    }
}
