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
package com.wiseco.var.process.app.server.service.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * @author liukl
 */
public class LinkMsgDto implements Serializable {

    private static final long serialVersionUID = -6852194991533855048L;
    private Long componentId;
    private Long directoryId;
    private String identifier;
    private String type;
    private String openType;
    private Long planId;
    private String branchContent;
    private Long outsideId;

    /**
     * 构造函数
     */
    public LinkMsgDto() {

    }

    /**
     * 构造函数
     *
     * @param componentId 组件Id
     * @param identifier 唯一标识符
     * @param directoryId 目录Id
     * @param type 类型
     * @param openType 开放类型
     */
    public LinkMsgDto(Long componentId, String identifier, Long directoryId, String type, String openType) {
        this.componentId = componentId;
        this.identifier = identifier;
        this.directoryId = directoryId;
        this.type = type;
        this.openType = openType;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getBranchContent() {
        return branchContent;
    }

    public void setBranchContent(String branchContent) {
        this.branchContent = branchContent;
    }

    public Long getOutsideId() {
        return outsideId;
    }

    public void setOutsideId(Long outsideId) {
        this.outsideId = outsideId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * getDirectoryId
     *
     * @return java.lang.Long
     */
    public Long getDirectoryId() {
        return directoryId;
    }

    /**
     * setDirectoryId
     * @param directoryId 目录Id
     */
    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
