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
package com.wiseco.var.process.app.server.service.engine.transform.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wiseco.boot.commons.util.CollectionUtils;
import com.wiseco.decision.engine.var.enums.VarTypeEnum;
import com.wiseco.decision.engine.java.common.enums.DataValuePrefixEnum;
import com.wiseco.decision.engine.java.common.enums.EngineComponentInvokeMetaTypeEnum;
import com.wiseco.decision.engine.var.transform.component.data.VarFunctionCompileQueryResultDto;
import com.wiseco.var.process.app.server.commons.enums.DeleteFlagEnum;
import com.wiseco.var.process.app.server.repository.entity.VarProcessCompileVar;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunction;
import com.wiseco.var.process.app.server.repository.entity.VarProcessFunctionClass;
import com.wiseco.var.process.app.server.repository.entity.VarProcessManifest;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariable;
import com.wiseco.var.process.app.server.repository.entity.VarProcessVariableClass;
import com.wiseco.var.process.app.server.service.VarProcessCompileVarService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionClassService;
import com.wiseco.var.process.app.server.service.VarProcessFunctionService;
import com.wiseco.var.process.app.server.service.VarProcessVariableClassService;
import com.wiseco.var.process.app.server.service.VarProcessVariableService;
import com.wiseco.var.process.app.server.service.engine.IVarProcessCompileVarProvider;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestService;
import com.wiseco.var.process.app.server.service.manifest.VarProcessManifestVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VarProcessCompileVarProviderImpl implements IVarProcessCompileVarProvider {
	@Autowired
	VarProcessCompileVarService varProcessCompileVarService;
	@Autowired
	VarProcessFunctionService varProcessFunctionService;
	@Autowired
	VarProcessVariableService varProcessVariableService;
	@Autowired
	VarProcessManifestService varProcessManifestService;
	@Autowired
	private VarProcessVariableClassService varProcessVariableClassService;
	@Autowired
	private VarProcessFunctionClassService varProcessFunctionClassService;
	@Autowired
	private VarProcessManifestVariableService varProcessManifestVariableService;
	@Override
	public VarFunctionCompileQueryResultDto getAllIdentifiersAndVarPathes(Long spaceId, Long manifestId, VarTypeEnum varTypeEnum, String identifier, boolean isDirect) {
		//调用的组件identiifer
		Set<String> variableIdSet = new HashSet<>();
		Set<String> functionIdSet = new HashSet<>();
		Long comId = null;
		if (varTypeEnum == VarTypeEnum.FUNCTION) {
			VarProcessFunction varProcessFunction = varProcessFunctionService.getOne(new QueryWrapper<VarProcessFunction>().lambda().eq(VarProcessFunction::getIdentifier, identifier)
					.eq(VarProcessFunction::getDeleteFlag, DeleteFlagEnum.USABLE.getCode()));
			comId = varProcessFunction.getId();
			functionIdSet.add(String.valueOf(comId));
		} else if (varTypeEnum == VarTypeEnum.VAR) {
			VarProcessVariable varProcessVariable = null;
			if (manifestId != null) {
				varProcessVariable = varProcessManifestVariableService.getManifestVariableByIdentifier(manifestId, identifier);
			} else {
				varProcessVariable = varProcessVariableService.getOne(new QueryWrapper<VarProcessVariable>().lambda().eq(VarProcessVariable::getIdentifier, identifier).eq(VarProcessVariable::getDeleteFlag,DeleteFlagEnum.USABLE.getCode()));
			}
			comId = varProcessVariable.getId();
			variableIdSet.add(String.valueOf(comId));
		} else if (varTypeEnum == VarTypeEnum.MAINFLOW) {
			VarProcessManifest varProcessmanifest = varProcessManifestService.getOne(new QueryWrapper<VarProcessManifest>().lambda().eq(VarProcessManifest::getIdentifier, identifier).eq(VarProcessManifest::getDeleteFlag,DeleteFlagEnum.USABLE.getCode()));
			comId = varProcessmanifest.getId();
		}
		Assert.notNull(comId,"查询关联数据 记录不存在[spaceId = " + spaceId + " , identifier = " + identifier + "]");
		//调用的变量路径
		Set<String> varPathSet = new HashSet<>();
		// 参数和本地变量引用的类型信息
		Set<String> modelTypeSet = new HashSet<>();
		// 依赖组件class信息
		List<String> javaClsList = new ArrayList<>();
		List<VarProcessCompileVar> varLst = varProcessCompileVarService.list(new QueryWrapper<VarProcessCompileVar>().lambda()
				.eq(VarProcessCompileVar::getInvokId, comId)
				.eq(VarProcessCompileVar::getInvokType, varTypeEnum.name()));
		if (!CollectionUtils.isEmpty(varLst)) {
			for (VarProcessCompileVar compileVar : varLst) {
				//如果是变量记录
				if (EngineComponentInvokeMetaTypeEnum.VAR.name().equals(compileVar.getCallType())) {
					boolean isSpecialPath = compileVar.getValue().toLowerCase().startsWith(DataValuePrefixEnum.PARAMETERS.name().toLowerCase())
							|| compileVar.getValue().toLowerCase().startsWith(DataValuePrefixEnum.LOCALVARS.name().toLowerCase())
							|| compileVar.getValue().toLowerCase().startsWith(DataValuePrefixEnum.FUNCTIONRETURN.name().toLowerCase());
					if (!isDirect && isSpecialPath) {
						continue;
					}
					varPathSet.add(compileVar.getValue());
				} else if (EngineComponentInvokeMetaTypeEnum.TYPE.name().equals(compileVar.getCallType())) {
					modelTypeSet.add(compileVar.getValue());
				} else {
					//否则是非变量的组件
					if (compileVar.getCallComponentType().equals(VarTypeEnum.VAR.name()) && !variableIdSet.contains(compileVar.getValue())) {
						variableIdSet.add(compileVar.getValue());
						fillData(Long.parseLong(compileVar.getValue()), compileVar.getCallComponentType(), variableIdSet, functionIdSet,varPathSet,modelTypeSet);
					} else if (compileVar.getCallComponentType().equals(VarTypeEnum.FUNCTION.name()) && !variableIdSet.contains(compileVar.getValue())) {
						functionIdSet.add(compileVar.getValue());
						fillData(Long.parseLong(compileVar.getValue()), compileVar.getCallComponentType(), variableIdSet, functionIdSet,varPathSet,modelTypeSet);
					}
				}
			}
		}
		Set<String> variableIdentifierSet = new HashSet<>();
		Set<String> functionIdentifierSet = new HashSet<>();
		if (!CollectionUtils.isEmpty(variableIdSet)) {
			List<VarProcessVariable> variableList = varProcessVariableService.list(new QueryWrapper<VarProcessVariable>().lambda().in(VarProcessVariable::getId, variableIdSet));
			Assert.isTrue(variableList.size() == variableIdSet.size(),"查询指标定义信息出错,componentId = [" + variableIdSet + "]");
			variableIdentifierSet = variableList.stream().map(e -> e.getIdentifier()).collect(Collectors.toSet());
			List<VarProcessVariableClass> variableClassList = varProcessVariableClassService.list(new QueryWrapper<VarProcessVariableClass>().lambda().select(VarProcessVariableClass::getId, VarProcessVariableClass::getClassData).in(VarProcessVariableClass::getVariableId, variableIdSet));
			javaClsList.addAll(variableClassList.stream().map(e -> e.getClassData()).collect(Collectors.toSet()));
		}
		if (!CollectionUtils.isEmpty(functionIdSet)) {
			List<VarProcessFunction> functionList = varProcessFunctionService.list(new QueryWrapper<VarProcessFunction>().lambda().in(VarProcessFunction::getId, functionIdSet));
			Assert.isTrue(functionList.size() == functionIdSet.size(),"查询函数定义信息出错,componentId = [" + functionIdSet + "]");
			functionIdentifierSet = functionList.stream().map(e -> e.getIdentifier()).collect(Collectors.toSet());
			List<VarProcessFunctionClass> functionClassList = varProcessFunctionClassService.list(new QueryWrapper<VarProcessFunctionClass>().lambda().select(VarProcessFunctionClass::getId, VarProcessFunctionClass::getClassData).in(VarProcessFunctionClass::getFunctionId, functionIdSet));
			javaClsList.addAll(functionClassList.stream().map(e -> e.getClassData()).collect(Collectors.toSet()));
		}
		return new VarFunctionCompileQueryResultDto(variableIdentifierSet, functionIdentifierSet, varPathSet,modelTypeSet, javaClsList);
	}

	/**
	 * 递归查询变量和组件
	 * @param componentId 调用组件id
	 * @param componentType 调用组件类型
	 * @param varComponentSet 被调用变量集合
	 * @param functionComponentSet 被调用函数集合
	 * @param varPathSet 变量路径
	 * @param modelTypeSet 模型集合
	 */
	private void fillData(Long componentId, String componentType, Set<String> varComponentSet,Set<String> functionComponentSet,Set<String> varPathSet,Set<String> modelTypeSet) {
		List<VarProcessCompileVar> varLst = varProcessCompileVarService.list(new QueryWrapper<VarProcessCompileVar>().lambda()
				.eq(VarProcessCompileVar::getInvokId,componentId)
				.eq(VarProcessCompileVar::getInvokType, componentType));

		if (!CollectionUtils.isEmpty(varLst)) {
			for (VarProcessCompileVar compileVar : varLst) {
				//如果是变量记录
				if (EngineComponentInvokeMetaTypeEnum.VAR.name().equals(compileVar.getCallType())) {
					if (compileVar.getValue().toLowerCase().startsWith(DataValuePrefixEnum.PARAMETERS.name().toLowerCase())
							||  compileVar.getValue().toLowerCase().startsWith(DataValuePrefixEnum.LOCALVARS.name().toLowerCase())
							||  compileVar.getValue().toLowerCase().startsWith(DataValuePrefixEnum.FUNCTIONRETURN.name().toLowerCase())) {
						continue;
					}
					varPathSet.add(compileVar.getValue());
				} else if (EngineComponentInvokeMetaTypeEnum.TYPE.name().equals(compileVar.getCallType())) {
					modelTypeSet.add(compileVar.getValue());
				} else {
					//否则是非变量的组件
					if (compileVar.getCallComponentType().equals(VarTypeEnum.VAR.name()) && !varComponentSet.contains(compileVar.getValue())) {
						varComponentSet.add(compileVar.getValue());
						fillData(Long.parseLong(compileVar.getValue()), compileVar.getCallComponentType(), varComponentSet, functionComponentSet,varPathSet,modelTypeSet);
					} else if (compileVar.getCallComponentType().equals(VarTypeEnum.FUNCTION.name()) && !varComponentSet.contains(compileVar.getValue())) {
						functionComponentSet.add(compileVar.getValue());
						fillData(Long.parseLong(compileVar.getValue()), compileVar.getCallComponentType(), varComponentSet, functionComponentSet,varPathSet,modelTypeSet);
					}
				}
			}
		}
	}

}
