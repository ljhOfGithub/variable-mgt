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
package com.wiseco.var.process.app.server.service.backtracking.param;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wiseco.boot.commons.io.SftpClient;
import com.wiseco.var.process.app.server.controller.vo.DataModelTreeVo;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.BatchBacktrackingTriggerTypeEnum;
import com.wiseco.var.process.app.server.enums.OutsideParamImportServiceTypeEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import com.wiseco.var.process.app.server.repository.entity.VarProcessBatchBacktrackingTask;
import com.wisecotech.json.JSONObject;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xupei
 */
public interface BacktrackingParamImportService {
    /**
     * 获取外部参数导入服务类型
     *
     * @return 外部参数导入服务类型的枚举
     */
    OutsideParamImportServiceTypeEnum getType();

    /**
     * 数据库取值
     *
     * @param dataGetTypeInfo 取值方式
     * @param from            from
     * @param size            size
     * @return java.util.List
     */
    default List<String> importDataByDb(BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo, int from, int size) {
        return new ArrayList<>();
    }

    /**
     * FTP文件取值
     *
     * @param dataGetTypeInfo                 取值方式
     * @param varProcessBatchBacktrackingTask 批量回溯任务的实体类
     * @param triggerType                     任务执行类型
     * @param sftpClient    服务器
     * @return java.io.InputStream
     */
    default InputStream importDataByFtpFile(BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo, VarProcessBatchBacktrackingTask varProcessBatchBacktrackingTask,
                                            BatchBacktrackingTriggerTypeEnum triggerType, SftpClient sftpClient) {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "未实现的方法");
    }


    /**
     * 本地文件获取数据
     *
     * @param s3Client        oss客户端
     * @param dataGetTypeInfo 取值方式
     * @return java.io.InputStream
     */
    default InputStream importDataByLocalFile(S3Client s3Client, BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo) {
        throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_INTERFACE_UNREALIZED, "未实现的方法");
    }


    /**
     * 获取json——整体映射
     *
     * @param data          表头：值
     * @param selectColumns 报文字段
     * @return java.util.List<java.lang.String>
     */
    default List<String> getJsonListOfWholeMapping(List<Map<String, Object>> data, List<String> selectColumns) {
        //结果
        List<String> result = new ArrayList<>(data.size() * selectColumns.size());
        for (String colum : selectColumns) {
            for (Map<String, Object> map : data) {
                Object object = map.get(colum);
                if (object != null) {
                    String jsonString = object.toString();
                    result.add(jsonString);
                }
            }
        }
        return result;
    }

    /**
     * 获取json——属性映射
     *
     * @param data          表头：值
     * @param dataModelTree 数据模型映射信息
     * @return java.util.List<java.lang.String>
     */
    default List<String> getJsonListOfAttributeMapping(List<Map<String, Object>> data, List<DataModelTreeVo> dataModelTree) {
        if (data == null) {
            return new ArrayList<>();
        }
        //数据模型全路径:表头
        Map<String, Object> modelValueMapping = new LinkedHashMap<>();
        //广度优先遍历，扁平化数
        LinkedList<DataModelTreeVo> queue = new LinkedList<>();
        for (DataModelTreeVo dataModelTreeVo : dataModelTree) {
            queue.offer(dataModelTreeVo);
            while (!queue.isEmpty()) {
                DataModelTreeVo poll = queue.poll();
                if (!CollectionUtils.isEmpty(poll.getChildren())) {
                    for (DataModelTreeVo node : poll.getChildren()) {
                        queue.offer(node);
                    }
                } else {
                    modelValueMapping.put(poll.getValue(), poll.getSourceVar());
                }
            }
        }

        List<String> result = new ArrayList<>(data.size());
        //将映射替换成值
        for (Map<String, Object> map : data) {
            JSONObject temp = new JSONObject();
            modelValueMapping.forEach((key, value) -> temp.put(key, map.get(value)));
            String json = transformJson(temp.toJSONString());
            result.add(json);
        }
        return result;
    }

    /**
     * 解析json
     *
     * @param inputJson 输入的JSON实体
     * @return java.lang.String
     */
    default String transformJson(String inputJson) {
        try {
            // 创建 ObjectMapper 对象，用于处理 JSON
            ObjectMapper mapper = new ObjectMapper();
            // 解析输入 JSON 字符串为 JsonNode 对象
            JsonNode rootNode = mapper.readTree(inputJson);
            // 创建目标 JSON 对象
            ObjectNode targetJson = mapper.createObjectNode();
            // 遍历输入 JSON 中的每个属性
            rootNode.fields().forEachRemaining(entry -> {
                String[] keys = entry.getKey().split("\\.");
                ObjectNode currentNode = targetJson;
                for (int i = 0; i < keys.length; i++) {
                    String key = keys[i];
                    if (i == keys.length - 1) {
                        // 最后一个键，设置属性值
                        currentNode.set(key, entry.getValue());
                    } else {
                        // 非最后一个键，创建子对象
                        if (!currentNode.has(key)) {
                            currentNode.set(key, mapper.createObjectNode());
                        }
                        currentNode = (ObjectNode) currentNode.get(key);
                    }
                }
            });
            // 将目标 JSON 转换为字符串
            return targetJson.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
