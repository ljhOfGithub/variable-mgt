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
package com.wiseco.var.process.app.server.service.common;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 文件在线预览
 *
 * @author xupei
 */
@Service
@Slf4j
@RefreshScope
public class FilePreviewManager {
    private static final MediaType FILE_TEXT = MediaType.parse("text/x-markdown;charset=utf-8");
    private static final String FILE_PREVIEW_BASE_URL = "http://%s/onlinePreview?url=%s";
    private static final String TEMP_PATH = "file://43dc6c7c-e109-4590-add9-fc59e6fa5bab/";
    private static final String TOTAL_SPLIT = "-";
    private static final int TWO_HUNDRED = 200;
    static Map<Integer, String> allService = new HashMap<>();
    static Map<Integer, String> workingService = new ConcurrentHashMap<>();
    private static SecureRandom random = null;

    static {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = new SecureRandom();
        }
    }

    @Value("${filePreview.fileService.ipAndPort:127.0.0.1:8012}")
    private String fileServiceIpAndPortList;
    @Value("${filePreview.fileService.userCode:decision}")
    private String userCode;
    //static Random random = new Random();
    /**
     * 密码
     */
    @Value("${filePreview.fileService.password:48a40cb596ee44cea9d26188c4acacfb}")
    private String password;
    @Value("${filePreview.fileService.url:http://%s/fileUpload}")
    private String filePreviewUploadUrl;
    @Value("${filePreview.fileService.auth:false}")
    private boolean auth;

    /**
     * bean 初始化
     */
    @PostConstruct
    public void init() {
        String[] serviceList = fileServiceIpAndPortList.split(";");
        for (int i = 0; i < serviceList.length; i++) {
            allService.put(i, serviceList[i]);
            workingService.put(i, serviceList[i]);
        }
    }

    /**
     * 获取文件预览的统一资源定位符
     * @param fileByte 文件字节码
     * @param fileName 文件名
     * @return 统一资源定位符
     * @throws IOException IO异常
     */
    public String getFilePreviewUrl(byte[] fileByte, String fileName) throws IOException {
        String filePreviewService = selectServer();
        // step1、推送文件到kkFile
        String url = String.format(filePreviewUploadUrl, filePreviewService);
        uploadFile(url, fileByte, fileName, getAuthCode());
        // step2、准备预览路径
        String previewUrl = String.format(FILE_PREVIEW_BASE_URL, filePreviewService,
                new String(Base64.encodeBase64((TEMP_PATH + fileName).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));

        if (auth) {
            return previewUrl + "&authCode=" + getAuthCode();
        } else {
            return previewUrl;
        }

    }

    private String selectServer() throws RuntimeException {
        int size = workingService.size();
        if (size == 0) {
            throw new RuntimeException("没有可用的文件预览服务");
        }
        Integer index = random.nextInt(size);
        return workingService.get(index);
    }

    private String getAuthCode() {
        Long nonce = System.currentTimeMillis();
        String str = userCode + password + nonce;
        String signature = DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encodeBase64((nonce + TOTAL_SPLIT + userCode + TOTAL_SPLIT + signature).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * 上传文件
     * @param url 统一资源定位符
     * @param fileByte 文件字节码
     * @param fileName 文件名
     * @param authCode authCode
     * @throws IOException IO异常
     */
    public void uploadFile(String url, byte[] fileByte, String fileName, String authCode) throws IOException {
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody body = RequestBody.create(FILE_TEXT, fileByte);
        requestBody.addFormDataPart("file", fileName, body);
        requestBody.addFormDataPart("authCode", authCode);
        Request request = new Request.Builder().url(url).post(requestBody.build()).build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(MagicNumbers.INT_60, TimeUnit.SECONDS).readTimeout(MagicNumbers.INT_60, TimeUnit.SECONDS)
                .writeTimeout(MagicNumbers.INT_60, TimeUnit.SECONDS).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.code() != TWO_HUNDRED) {
            throw new IllegalArgumentException("文件推送预览服务失败");
        }
    }
}
