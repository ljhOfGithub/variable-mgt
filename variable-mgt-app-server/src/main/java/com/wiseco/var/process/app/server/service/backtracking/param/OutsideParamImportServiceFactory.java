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

import com.wiseco.var.process.app.server.commons.exception.InternalDataServiceException;
import com.wiseco.var.process.app.server.controller.vo.input.BacktrackingSaveInputVO;
import com.wiseco.var.process.app.server.enums.BacktrackingDataSourceTypeEnum;
import com.wiseco.var.process.app.server.enums.BacktrackingFileImportTypeEnum;
import com.wiseco.var.process.app.server.enums.OutsideParamImportServiceTypeEnum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xupei
 */
@Component
public class OutsideParamImportServiceFactory implements InitializingBean, ApplicationContextAware {
    private static final Map<OutsideParamImportServiceTypeEnum, BacktrackingParamImportService> SERVICE_MAP = new HashMap<>(16);
    private ApplicationContext appContext;

    /**
     * 获得取数服务
     * @param dataGetTypeInfo 取值方式输入实体对象
     * @return 取数服务对象
     */
    public BacktrackingParamImportService getOutsideParamImportService(BacktrackingSaveInputVO.DataGetTypeInfo dataGetTypeInfo) {
        OutsideParamImportServiceTypeEnum typeEnum = null;
        if (dataGetTypeInfo.getDataSourceType() == BacktrackingDataSourceTypeEnum.DB && dataGetTypeInfo.getDataBase() != null) {
            typeEnum = OutsideParamImportServiceTypeEnum.DATABASE;
        } else if (dataGetTypeInfo.getDataSourceType() == BacktrackingDataSourceTypeEnum.FILE  && dataGetTypeInfo.getDataFile() != null && dataGetTypeInfo.getDataFile().getDataFileType() == BacktrackingFileImportTypeEnum.LOCAL) {
            typeEnum = OutsideParamImportServiceTypeEnum.LOCAL_FILE;
        } else {
            typeEnum = OutsideParamImportServiceTypeEnum.FTP_FILE;
        }
        if (!SERVICE_MAP.containsKey(typeEnum)) {
            throw new InternalDataServiceException("type[" + typeEnum + "] not supported.");
        }
        return SERVICE_MAP.get(typeEnum);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        appContext.getBeansOfType(BacktrackingParamImportService.class)
                .values()
                .forEach(service -> SERVICE_MAP.put(service.getType(), service));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }
}
