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
package com.wiseco.var.process.app.server.service;

import com.alibaba.nacos.api.exception.NacosException;
import com.wiseco.boot.pubsub.PubSubClient;
import com.wiseco.boot.pubsub.dto.ConfigInfo;
import com.wiseco.config.dto.ItemDTO;
import com.wiseco.decision.nacos.DecisionNacosConfigProperties;
import com.wiseco.decision.nacos.config.DecisionInstance;
import com.wiseco.decision.nacos.config.DecisionNacosClient;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wisecotech.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 变量发布 业务实现
 *
 * @author Zhaoxiong Chen
 * @since 2022/6/14
 */
@Slf4j
@Service
public class VariablePublishBiz {
    public static final String METADATA = "metadata";
    public static final String NACOS = "nacos";
    @Autowired
    private DecisionNacosClient decisionNacosClient;

    @Autowired(required = false)
    private PubSubClient pubSubClient;

    /**
     * 策略发布状态的循环查询次数
     */
    @Value("${var.process.publish.queryLoopNums:360}")
    private int statusQueryLoopNums;

    /**
     * 策略发布状态的循环查询间隔 (ms)
     */
    @Value("${var.process.publish.loopInterval:500}")
    private int statusQueryLoopInterval;

    /**
     * 发布方式，nacos/pubsub
     */
    @Value("${var.process.publish.type:nacos}")
    private String publishType;

    /**
     * 发布实时服务中主清单与服务id的配置文件名称
     */
    public static final String VAR_PROCESS_PUBLIC = "var-process-public";

    /**
     * 发布实时服务中异步清单与服务id的配置文件名称
     */
    public static final String VAR_PROCESS_ASYNC_PUBLIC = "var-process-async-public";

    /**
     * 发布批量回溯id配置文件名称
     */
    private static final String VAR_PROCESS_BACKTRACKING_PUBLIC = "var-process-backtracking-public";

    /**
     * 监听异步清单、批量回溯并执行的服务
     */
    public static final String VARIABLE_TASK_SERVICE = "variable-task-server";

    /**
     * 监听主清单并执行的服务
     */
    public static final String VARIABLE_SERVICE_REST = "variable-service-rest";

    /**
     * 发布变量
     *
     * @param bizId   业务id
     * @param bizType 业务类型
     * @return 发布变量的结果
     */
    public boolean publishVariable(String bizId, int bizType) {
        String configFileName;
        String appName;
        // 0:异步清单 1:实时服务 2：批量回溯
        if (bizType == 0) {
            configFileName = VAR_PROCESS_ASYNC_PUBLIC;
            appName = VARIABLE_TASK_SERVICE;
        } else if (bizType == 1) {
            configFileName = VAR_PROCESS_PUBLIC;
            appName = VARIABLE_SERVICE_REST;
        } else {
            configFileName = VAR_PROCESS_BACKTRACKING_PUBLIC;
            appName = VARIABLE_TASK_SERVICE;
        }

        if (NACOS.equals(publishType)) {
            //上传id至配置文件
            publishByNacos(bizId, configFileName);
        } else {
            publishByPubSub(bizId, configFileName, appName);
        }

        //todo 检查是否加载成功 pubsub是否使用nacos元数据？
        boolean publishStatus = false;
        for (int i = 0; i < statusQueryLoopNums && !publishStatus; i++) {
            if (NACOS.equals(publishType)) {
                publishStatus = checkConfigLoadingStatusOfNacos(bizId, configFileName, appName);
            } else {
                publishStatus = checkConfigLoadingStatusOfPubSub(bizId, configFileName, appName);
            }

            try {
                //每 500 ms 检查一次s
                Thread.sleep(statusQueryLoopInterval);
            } catch (InterruptedException ie) {
                log.warn("Variable service loading status checking thread is interrupted.", ie);
                Thread.currentThread().interrupt();
            }
        }

        if (!publishStatus) {
            // 没有发布成功，卸载已发布的id
            if (NACOS.equals(publishType)) {
                removeConfigOfNacos(bizId, configFileName);
            } else {
                removeConfigOfPubSub(bizId, configFileName, appName);
            }
        }
        // 3.返回REST服务是否加载实时服务的结果
        return publishStatus;
    }

    /**
     * 使用nacos发布变量清单ID
     *
     * @param bizId          业务id DTO
     * @param configFileName 上传文件名称
     */
    private void publishByNacos(String bizId, String configFileName) {
        try {
            String nacosContent = decisionNacosClient.getConfig(configFileName);
            if (nacosContent == null) {
                nacosContent = "[]";
            }
            List<String> lastTimeServiceCodes = JSON.parseArray(nacosContent, String.class);
            lastTimeServiceCodes.add(bizId);
            boolean isSuccess = decisionNacosClient.publishConfig(configFileName, JSON.toJSONString(Sets.newHashSet(lastTimeServiceCodes)));
            log.info("configFileName:{},serviceManifestId:{},nacos config file upload status: {}.", configFileName, bizId, isSuccess);
        } catch (NacosException ne) {
            log.error("configFileName:{},serviceManifestId:{},exception encountered in nacos profile upload progress.", configFileName, bizId, ne);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_PUBLISH_FAIL, "变量清单ID上传异常。");
        }
    }

    /**
     * 使用Pubsub发布清单
     *
     * @param bizId   业务id
     * @param key     key
     * @param appCode appcode
     */
    private void publishByPubSub(String bizId, String key, String appCode) {
        try {
            //获取app的全部数据并根据key进行过滤
            List<String> values = pubSubClient.getFromAppId(appCode).stream().filter(itemDTO -> itemDTO.getKey().equals(key)).map(ItemDTO::getValue).collect(Collectors.toList());
            String value;
            if (CollectionUtils.isEmpty(values)) {
                value = "[]";
            } else {
                value = values.get(0);
            }
            List<String> lastTimeServiceCodes = JSON.parseArray(value, String.class);
            lastTimeServiceCodes.add(bizId);
            ConfigInfo configInfo = new ConfigInfo();
            configInfo.setValue(JSON.toJSONString(Sets.newHashSet(lastTimeServiceCodes)));
            pubSubClient.publish(appCode, key, configInfo);
        } catch (Exception e) {
            log.error("configFileName:{},serviceManifestId:{},exception encountered in pubsub profile upload progress.", key, bizId, e);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_PUBLISH_FAIL, "变量清单ID上传异常。");
        }
    }


    /**
     * 使用nacos卸载清单
     *
     * @param bizId          发布的serviceManifestId
     * @param configFileName 配置文件名
     */
    private void removeConfigOfNacos(String bizId, String configFileName) {
        try {
            // 获取 nacos 配置文件当前的变量清单
            String nacosContent = decisionNacosClient.getConfig(configFileName);
            if (nacosContent == null) {
                nacosContent = "[]";
            }

            Set<String> lastTimeServiceCodes = new HashSet<>(JSON.parseArray(nacosContent, String.class));
            lastTimeServiceCodes.remove(bizId);
            decisionNacosClient.publishConfig(configFileName, JSON.toJSONString(Sets.newHashSet(lastTimeServiceCodes)));
        } catch (NacosException ne) {
            log.error(" configFileName:{},serviceManifestId:{}, unload error.", configFileName, bizId, ne);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "指标清单 ID 移除异常。");
        }
    }

    /**
     * 使用pubsub卸载清单
     *
     * @param bizId   发布的serviceManifestId
     * @param key     key
     * @param appCode appCode
     */
    private void removeConfigOfPubSub(String bizId, String key, String appCode) {
        try {
            //获取app的全部数据并根据key进行过滤
            List<String> values = pubSubClient.getFromAppId(appCode).stream().filter(itemDTO -> itemDTO.getKey().equals(key)).map(ItemDTO::getValue).collect(Collectors.toList());
            String value;
            if (CollectionUtils.isEmpty(values)) {
                value = "[]";
            } else {
                value = values.get(0);
            }
            List<String> lastTimeServiceCodes = JSON.parseArray(value, String.class);
            lastTimeServiceCodes.remove(bizId);
            ConfigInfo configInfo = new ConfigInfo();
            configInfo.setValue(JSON.toJSONString(Sets.newHashSet(lastTimeServiceCodes)));
            pubSubClient.publish(appCode, key, configInfo);
        } catch (Exception ne) {
            log.error(" configFileName:{},serviceManifestId:{}, unload error.", key, bizId, ne);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INVALID_OPERATION, "指标清单 ID 移除异常。");
        }
    }


    /**
     * 检查变量清单服务知识库加载状态
     *
     * @param bizId          业务id DTO
     * @param configFileName 上传文件名称
     * @param appName        appName
     * @return boolean
     */
    private boolean checkConfigLoadingStatusOfNacos(String bizId, String configFileName, String appName) {
        try {
            String configData = decisionNacosClient.getConfig(configFileName);
            List<String> lastTimeServiceCodes = JSON.parseArray(configData, String.class);
            boolean isPublished = lastTimeServiceCodes.contains(bizId);
            log.info("configFileName:{},serviceManifestId:{},config publish status: {}.", configFileName, bizId, isPublished);
            if (!isPublished) {
                return false;
            }
            boolean metadataStatus = false;
            List<DecisionInstance> varProcessInstances = decisionNacosClient.getInstancesWithDefaultClusters(DecisionNacosConfigProperties.DEFAULT_CLUSTER_NAME, appName);
            for (DecisionInstance varProcessInstance : varProcessInstances) {
                String metaValue = varProcessInstance.getMataValue(configFileName);
                if (!StringUtils.isEmpty(metaValue)) {
                    List<String> instanceServiceCodes = JSON.parseArray(metaValue, String.class);
                    metadataStatus = instanceServiceCodes.contains(bizId);
                    if (metadataStatus) {
                        break;
                    }
                }
            }
            log.info("app name:'{}',metadata key:{},serviceManifestId:{},metadata loading status:{}.", appName, configFileName, bizId, metadataStatus);
            return metadataStatus;
        } catch (NacosException ne) {
            log.error("app name:'{}',metadata key:{},serviceManifestId:{},exception encountered in loading status query progress.", appName, configFileName, bizId, ne);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "指标清单发布状态查询异常。");
        }
    }

    /**
     * 检查变量清单服务知识库加载状态
     *
     * @param bizId   业务id DTO
     * @param key     key
     * @param appName appName
     * @return boolean
     */
    private boolean checkConfigLoadingStatusOfPubSub(String bizId, String key, String appName) {
        try {
            //获取app的全部数据并根据key进行过滤
            List<ItemDTO> itemDTOList = pubSubClient.getFromAppId(appName);
            List<String> pubValues = itemDTOList.stream().filter(itemDTO -> itemDTO.getKey().equals(key)).map(ItemDTO::getValue).collect(Collectors.toList());
            String value;
            if (CollectionUtils.isEmpty(pubValues)) {
                value = "[]";
            } else {
                value = pubValues.get(0);
            }
            List<String> publishData = JSON.parseArray(value, String.class);
            //1.先检查有没有发布成功
            boolean isPublished = publishData.contains(bizId);
            log.info("configFileName:{},serviceManifestId:{},config publish status: {}.", key, bizId, isPublished);
            if (!isPublished) {
                return false;
            }

            //2.再检查有没有加载成功
            List<String> loadValues = itemDTOList.stream().filter(itemDTO -> itemDTO.getKey().equals(METADATA + "-" + key)).map(ItemDTO::getValue).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(loadValues)) {
                value = "[]";
            } else {
                value = loadValues.get(0);
            }
            List<String> loadData = JSON.parseArray(value, String.class);
            boolean metadataStatus = loadData.contains(bizId);
            log.info("app name:'{}',metadata key:{},serviceManifestId:{},metadata loading status:{}.", appName, METADATA + "-" + key, bizId, metadataStatus);
            return metadataStatus;
        } catch (Exception ne) {
            log.error("app name:'{}',metadata key:{},serviceManifestId:{},exception encountered in loading status query progress.", appName, key, bizId, ne);
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.MANIFEST_STATUS_NO_MATCH, "指标清单发布状态查询异常。");
        }
    }
}
