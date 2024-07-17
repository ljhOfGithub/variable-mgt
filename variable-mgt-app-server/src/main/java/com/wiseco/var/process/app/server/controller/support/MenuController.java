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
package com.wiseco.var.process.app.server.controller.support;

import com.wiseco.auth.common.MenuVo;
import com.wiseco.auth.common.SessionUserDTO;
import com.wiseco.boot.commons.web.APIResult;
import com.wiseco.boot.security.SessionContext;
import com.wiseco.boot.user.MenuClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xupei
 */
@RestController
@Slf4j
@Tag(name = "侧边栏菜单")
public class MenuController {

    @Resource
    MenuClient menuClient;

    @Value("${spring.application.name}")
    private String appCode;

    /**
     * 侧边栏菜单列表
     * @return APIResult
     */
    @GetMapping("/api/v1/user/build")
    @Operation(summary = "侧边栏菜单列表")
    public APIResult<List<MenuVo>> buildMenuList() {
        final Integer userId = SessionContext.getSessionUser().getUser().getId();
        log.info("侧边栏菜单列表，appCode:{}， userId:{}", appCode, userId);
        final List<MenuVo> menuVos = menuClient.buildMenuList(appCode, userId);
        log.info("menuVos:{}", menuVos);
        return APIResult.success(menuVos);
    }

    /**
     * 当前详细用户信息
     * @return APIResult
     */
    @GetMapping("/api/v1/user/details")
    @Operation(summary = "当前详细用户信息")
    public APIResult<SessionUserDTO> menuInfo() {
        final Integer userId = SessionContext.getSessionUser().getUser().getId();
        log.info("当前详细用户信息，appCode:{}， userId:{}", appCode, userId);
        return APIResult.success(menuClient.menuInfo(appCode, userId));
    }

}
