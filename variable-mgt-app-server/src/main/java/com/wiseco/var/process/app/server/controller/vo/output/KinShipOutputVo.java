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
package com.wiseco.var.process.app.server.controller.vo.output;

import com.wiseco.var.process.app.server.enums.KinShipTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "血缘关系出参Vo")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KinShipOutputVo implements Serializable {

    private static final long SERIAL_VERSION_UID = 8759865846955185997L;

    @Schema(description = "节点类型名称",example = "变量")
    private String              name;

    @Schema(description = "节点类型",example = "VARIABLE")
    private KinShipTypeEnum     type;

    @Schema(description = "子节点")
    private List<NodeInfo>              children;

    public String getName() {
        return type.getDesc();
    }


    @Builder
    public static class NodeInfo {
        private Long                id;

        @Schema(description = "名称")
        private String              name;

        @Schema(description = "编码")
        private String              code;

        @Schema(description = "标签")
        private String              label;

        @Schema(description = "版本")
        private Integer             version;

        @Schema(description = "是否有子节点")
        private Boolean             hasChildren;

        /**
         * label getter
         * @return label
         */
        public String getLabel() {
            label = name;
            if (!StringUtils.isEmpty(code)) {
                label = label + "." + code;
            }
            if (version != null) {
                label = label + ".V" + version;
            }
            return label;
        }

        /**
         * code getter
         * @return code
         */
        public String getCode() {
            if (code == null) {
                return "";
            }
            return code;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Integer getVersion() {
            return version;
        }

        public Boolean getHasChildren() {
            return hasChildren;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public void setHasChildren(Boolean hasChildren) {
            this.hasChildren = hasChildren;
        }
    }

}
