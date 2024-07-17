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
// /*
// * Licensed to the Wiseco Software Corporation under one or more
// * contributor license agreements. See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
// 2024-03-21 19:05:34.768 INFO 16044 --- [nio-9536-exec-4] c.w.v.p.e.compiler.VarEngineDelegator : 变量测试rawData代码生成
// spaceCode:varService codeMap:{com.wisecotech.strategy.rawData$AutoLoanId.java=
// package com.wisecotech.strategy;
//
// import java.io.Serializable;
// import javax.xml.bind.annotation.XmlAccessType;
// import javax.xml.bind.annotation.XmlAccessorType;
// import javax.xml.bind.annotation.XmlAttribute;
// import javax.xml.bind.annotation.XmlRootElement;
// import javax.xml.bind.annotation.XmlType;
// import com.fasterxml.jackson.annotation.JsonProperty;
//
// @XmlAccessorType(XmlAccessType.FIELD)
// @XmlType(name = "")
// @XmlRootElement(name = "rawData$AutoLoanId")
// public class rawData$AutoLoanId implements Serializable
// {
//
// @XmlAttribute(name = "id")
// @JsonProperty("id")
// public Integer _id;
//
// public Integer nullSafe_id() {
// return this._id;
// }
//
// }
// , com.wisecotech.strategy.rawData.java=
// package com.wisecotech.strategy;
//
// import java.io.Serializable;
// import javax.xml.bind.annotation.XmlAccessType;
// import javax.xml.bind.annotation.XmlAccessorType;
// import javax.xml.bind.annotation.XmlElement;
// import javax.xml.bind.annotation.XmlRootElement;
// import java.util.Objects;
// import java.util.ArrayList;
// import java.util.List;
// import java.math.BigDecimal;
// import org.joda.time.DateTime;
// import org.springframework.util.StringUtils;
// import com.fasterxml.jackson.core.JsonParser;
// import com.fasterxml.jackson.core.JsonToken;
// import com.wiseco.decision.engine.var.compiler.property.JsonParserConstant;
// import javax.xml.stream.XMLInputFactory;
// import javax.xml.stream.XMLStreamConstants;
// import javax.xml.stream.XMLStreamException;
// import javax.xml.stream.XMLStreamReader;
// import java.io.StringReader;
//
// import javax.xml.bind.annotation.XmlType;
// import com.fasterxml.jackson.annotation.JsonProperty;
// import com.wiseco.decision.engine.base.AbstractRawData;
//
// @XmlAccessorType(XmlAccessType.FIELD)
// @XmlType(name = "")
// @XmlRootElement(name = "rawData")
// public class rawData
// extends AbstractRawData
// implements Serializable
// {
//
// @XmlElement(name = "AutoLoanId")
// @JsonProperty("AutoLoanId")
// public rawData$AutoLoanId _AutoLoanId;
// @XmlElement(name = "zxx_application")
// @JsonProperty("zxx_application")
// public rawData$zxx_application _zxx_application;
//
// public rawData$AutoLoanId nullSafe_AutoLoanId() {
// if (this._AutoLoanId == null) {
// this._AutoLoanId = new rawData$AutoLoanId();
// }
// return this._AutoLoanId;
// }
//
// public rawData$zxx_application nullSafe_zxx_application() {
// if (this._zxx_application == null) {
// this._zxx_application = new rawData$zxx_application();
// }
// return this._zxx_application;
// }
//
//
// public void parseFillPropertyWithXml(String xml) throws Exception {
// if (xml == null || xml.isEmpty()) {
// return;
// }
// XMLInputFactory xif = XMLInputFactory.newInstance();
// StringReader sr = new StringReader(xml);
// XMLStreamReader xr = xif.createXMLStreamReader(sr);
// fillInputRootWithXmlFixed(xr);
// sr.close();
// xr.close();
// }
//
// private void fillInputRootWithXmlFixed(XMLStreamReader xr) throws Exception {
// int attrNeedCnt = 1;
// int stack = 1;
//
// if (nextTagButIgnoreEndDocument(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
//
// while (stack != 0) {
// String fieldName = xr.getLocalName();
// if (attrNeedCnt == 0) {
// while (stack != 1) {
// if (nextTagIgnoreAnything(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
// }
// } else if ("input".equals(fieldName)) {
// fillInputRootWithXml(xr);
// attrNeedCnt--;
// stack--;
// } else {
// while (stack != 1) {
// if (nextTagIgnoreAnything(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
// }
// }
// int event = nextTagButIgnoreEndDocument(xr);
// if (event == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else if (event == XMLStreamConstants.END_ELEMENT) {
// stack--;
// } else if (event == XMLStreamConstants.END_DOCUMENT) {
// if (stack != 1) {
// throw new IllegalArgumentException("XML结构错误");
// }
// stack--;
// }
// }
// }
// private static final String getEventTypeString(int eventType) {
// switch(eventType) {
// case 1:
// return "START_ELEMENT";
// case 2:
// return "END_ELEMENT";
// case 3:
// return "PROCESSING_INSTRUCTION";
// case 4:
// return "CHARACTERS";
// case 5:
// return "COMMENT";
// case 6:
// default:
// return "UNKNOWN_EVENT_TYPE";
// case 7:
// return "START_DOCUMENT";
// case 8:
// return "END_DOCUMENT";
// case 9:
// return "ENTITY_REFERENCE";
// case 10:
// return "ATTRIBUTE";
// case 11:
// return "DTD";
// case 12:
// return "CDATA";
// }
// }
//
// private static int nextTagButIgnoreEndDocument(XMLStreamReader xr) throws Exception {
// int eventType = xr.next();
// while ((eventType == XMLStreamConstants.CHARACTERS && xr.isWhiteSpace()) // skip whitespace
// || (eventType == XMLStreamConstants.CDATA && xr.isWhiteSpace())
// // skip whitespace
// || eventType == XMLStreamConstants.SPACE
// || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
// || eventType == XMLStreamConstants.COMMENT
// ) {
// eventType = xr.next();
// }
//
// if (eventType == XMLStreamConstants.END_DOCUMENT) {
// return eventType;
// }
//
// if (eventType != XMLStreamConstants.START_ELEMENT && eventType != XMLStreamConstants.END_ELEMENT) {
// throw new XMLStreamException(
// "found: " + getEventTypeString(eventType)
// + ", expected " + getEventTypeString(XMLStreamConstants.START_ELEMENT)
// + " or " + getEventTypeString(XMLStreamConstants.END_ELEMENT),
// xr.getLocation());
// }
//
// return eventType;
// }
// private static int nextTagIgnoreAnything(XMLStreamReader xr) throws XMLStreamException {
// int eventType = xr.next();
// while ((eventType == XMLStreamConstants.CHARACTERS) // skip text
// || (eventType == XMLStreamConstants.CDATA)
// || eventType == XMLStreamConstants.SPACE
// || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
// || eventType == XMLStreamConstants.COMMENT
// ) {
// eventType = xr.next();
// }
//
// if (eventType != XMLStreamConstants.START_ELEMENT && eventType != XMLStreamConstants.END_ELEMENT) {
// throw new XMLStreamException(
// "found: " + getEventTypeString(eventType)
// + ", expected " + getEventTypeString(XMLStreamConstants.START_ELEMENT)
// + " or " + getEventTypeString(XMLStreamConstants.END_ELEMENT),
// xr.getLocation());
// }
//
// return eventType;
// }
//
//
// private void fillInputRootWithXml(XMLStreamReader xr) throws Exception {
// int attrNeedCnt = 2;
// int stack = 1;
//
// if (nextTagButIgnoreEndDocument(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
//
// while (stack != 0) {
// String fieldName = xr.getLocalName();
// if (attrNeedCnt == 0) {
// while (stack != 1) {
// if (nextTagIgnoreAnything(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
// }
// }
// else if ("AutoLoanId".equals(fieldName)) {
// this._AutoLoanId = new rawData$AutoLoanId();
// fillRawDataAutoLoanIdWithXml(this._AutoLoanId, xr);
// attrNeedCnt--;
// stack--;
//
// } else if ("zxx_application".equals(fieldName)) {
// this._zxx_application = new rawData$zxx_application();
// fillRawDataZxx_applicationWithXml(this._zxx_application, xr);
// attrNeedCnt--;
// stack--;
//
// }
// else {
// while (stack != 1) {
// if (nextTagIgnoreAnything(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
// }
// }
// int event = nextTagButIgnoreEndDocument(xr);
// if (event == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else if (event == XMLStreamConstants.END_ELEMENT) {
// stack--;
// } else if (event == XMLStreamConstants.END_DOCUMENT) {
// if (stack != 1) {
// throw new IllegalArgumentException("XML结构错误");
// }
// stack--;
// }
//
// }
// }
//
// public void parseFillRawDataZxx_applicationWithXml(rawData$zxx_application _zxx_application, String xml) throws
// Exception {
// if (xml == null || xml.isEmpty()) {
// return;
// }
// XMLInputFactory xif = XMLInputFactory.newInstance();
// StringReader sr = new StringReader(xml);
// XMLStreamReader xr = xif.createXMLStreamReader(sr);
// fillRawDataZxx_applicationWithXml(_zxx_application, xr);
// sr.close();
// xr.close();
// }
//
//
// private void fillRawDataZxx_applicationWithXml(rawData$zxx_application _zxx_application, XMLStreamReader xr) throws
// Exception {
// int attrNeedCnt = 1;
// int stack = 1;
// for (int i = 0; i < xr.getAttributeCount(); i++) {
// String name = xr.getAttributeLocalName(i);
// if (attrNeedCnt == 0) {
// break;
// }
// else if ("name".equals(name)) {
// if (_zxx_application._name != null) {
// throw new IllegalArgumentException("同一属性值重复多次出现");
// }
// String text = xr.getAttributeValue(i);_zxx_application._name = text;attrNeedCnt--;
//
// }
// }
//
// if (nextTagButIgnoreEndDocument(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
//
// while (stack != 0) {
// String fieldName = xr.getLocalName();
// if (attrNeedCnt == 0) {
// while (stack != 1) {
// if (nextTagIgnoreAnything(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
// }
// }
// else if ("name".equals(fieldName)) {
// if (_zxx_application._name != null) {
// throw new IllegalArgumentException("同一属性值重复多次出现");
// }
// String text1 = xr.getAttributeValue(null, "value");
// String text2 = xr.getElementText();
// String text;
// if (text1 != null) {
// if (!text2.isEmpty()) {
// throw new IllegalArgumentException("XML结构错误：value值和text值不能同时存在");
// }
// text = text1;
// } else {
// text = text2;
// }
// _zxx_application._name = text;attrNeedCnt--;
// stack--;
//
// }
// else {
// while (stack != 1) {
// if (nextTagIgnoreAnything(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
// }
// }
// int event = nextTagButIgnoreEndDocument(xr);
// if (event == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else if (event == XMLStreamConstants.END_ELEMENT) {
// stack--;
// } else if (event == XMLStreamConstants.END_DOCUMENT) {
// if (stack != 1) {
// throw new IllegalArgumentException("XML结构错误");
// }
// stack--;
// }
//
// }
// }
//
// public void parseFillRawDataAutoLoanIdWithXml(rawData$AutoLoanId _AutoLoanId, String xml) throws Exception {
// if (xml == null || xml.isEmpty()) {
// return;
// }
// XMLInputFactory xif = XMLInputFactory.newInstance();
// StringReader sr = new StringReader(xml);
// XMLStreamReader xr = xif.createXMLStreamReader(sr);
// fillRawDataAutoLoanIdWithXml(_AutoLoanId, xr);
// sr.close();
// xr.close();
// }
//
//
// private void fillRawDataAutoLoanIdWithXml(rawData$AutoLoanId _AutoLoanId, XMLStreamReader xr) throws Exception {
// int attrNeedCnt = 1;
// int stack = 1;
// for (int i = 0; i < xr.getAttributeCount(); i++) {
// String name = xr.getAttributeLocalName(i);
// if (attrNeedCnt == 0) {
// break;
// }
// else if ("id".equals(name)) {
// if (_AutoLoanId._id != null) {
// throw new IllegalArgumentException("同一属性值重复多次出现");
// }
// String text = xr.getAttributeValue(i);_AutoLoanId._id = text == null || (text.length() == 0 || (text.length() == 2 &&
// text.contains(JsonParserConstant.DOUBLE_LINE))) ? null : Integer.parseInt(text);attrNeedCnt--;
//
// }
// }
//
// if (nextTagButIgnoreEndDocument(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
//
// while (stack != 0) {
// String fieldName = xr.getLocalName();
// if (attrNeedCnt == 0) {
// while (stack != 1) {
// if (nextTagIgnoreAnything(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
// }
// }
// else if ("id".equals(fieldName)) {
// if (_AutoLoanId._id != null) {
// throw new IllegalArgumentException("同一属性值重复多次出现");
// }
// String text1 = xr.getAttributeValue(null, "value");
// String text2 = xr.getElementText();
// String text;
// if (text1 != null) {
// if (!text2.isEmpty()) {
// throw new IllegalArgumentException("XML结构错误：value值和text值不能同时存在");
// }
// text = text1;
// } else {
// text = text2;
// }
// _AutoLoanId._id = text == null || (text.length() == 0 || (text.length() == 2 &&
// text.contains(JsonParserConstant.DOUBLE_LINE))) ? null : Integer.parseInt(text);attrNeedCnt--;
// stack--;
//
// }
// else {
// while (stack != 1) {
// if (nextTagIgnoreAnything(xr) == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else {
// stack--;
// }
// }
// }
// int event = nextTagButIgnoreEndDocument(xr);
// if (event == XMLStreamConstants.START_ELEMENT) {
// stack++;
// } else if (event == XMLStreamConstants.END_ELEMENT) {
// stack--;
// } else if (event == XMLStreamConstants.END_DOCUMENT) {
// if (stack != 1) {
// throw new IllegalArgumentException("XML结构错误");
// }
// stack--;
// }
//
// }
// }
//
// public void parseFillProperty(String inputJson) throws Exception {
// JsonParser jsonParser = JsonParserConstant.FACTORY.createParser(inputJson);
// try {
// fillInputRoot(jsonParser);
// } finally {
// jsonParser.close();
// }
// }
//
// private void fillInputRoot(JsonParser jsonParser) throws Exception {
// while (jsonParser.currentToken() == null){
// jsonParser.nextToken();
// }
// if(jsonParser.currentToken() != JsonToken.START_OBJECT){
// jsonParser.skipChildren();
// return;
// }
// int attrNeedCnt = 2;
// while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
// String fieldName = jsonParser.getCurrentName();
// if(Objects.equals(jsonParser.currentToken(), JsonToken.FIELD_NAME)){
// jsonParser.nextToken();
// }
// if(attrNeedCnt <= 0) {
// if(fieldName == null){
// break;
// }
// jsonParser.skipChildren();
// continue;
// }
// if (fieldName == null) {
// jsonParser.nextToken();
// if(Objects.equals(jsonParser.currentToken(), JsonToken.END_OBJECT)){
// break;
// }
// }
// else if ("AutoLoanId".equals(fieldName)) {
// this._AutoLoanId = new rawData$AutoLoanId();
// fillRawDataAutoLoanId(this._AutoLoanId, jsonParser);
// attrNeedCnt--;
// } else if ("zxx_application".equals(fieldName)) {
// this._zxx_application = new rawData$zxx_application();
// fillRawDataZxx_application(this._zxx_application, jsonParser);
// attrNeedCnt--;
// }
// else {
// jsonParser.skipChildren();
// }
// }
// }
//
// private void fillRawDataZxx_application(rawData$zxx_application _zxx_application, JsonParser jsonParser) throws
// Exception {
// while (jsonParser.currentToken() == null){
// jsonParser.nextToken();
// }
// if(jsonParser.currentToken() != JsonToken.START_OBJECT){
// jsonParser.skipChildren();
// return;
// }
// int attrNeedCnt = 1;
// while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
// String fieldName = jsonParser.getCurrentName();
// if(Objects.equals(jsonParser.currentToken(), JsonToken.FIELD_NAME)){
// jsonParser.nextToken();
// }
// if(attrNeedCnt <= 0) {
// if(fieldName == null){
// break;
// }
// jsonParser.skipChildren();
// continue;
// }
// if (fieldName == null) {
// jsonParser.nextToken();
// if(Objects.equals(jsonParser.currentToken(), JsonToken.END_OBJECT)){
// break;
// }
// }
// else if ("name".equals(fieldName)) {
// _zxx_application._name = jsonParser.getText();
// attrNeedCnt--;
// }
// else {
// jsonParser.skipChildren();
// }
// }
// }
//
// private void fillRawDataAutoLoanId(rawData$AutoLoanId _AutoLoanId, JsonParser jsonParser) throws Exception {
// while (jsonParser.currentToken() == null){
// jsonParser.nextToken();
// }
// if(jsonParser.currentToken() != JsonToken.START_OBJECT){
// jsonParser.skipChildren();
// return;
// }
// int attrNeedCnt = 1;
// while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
// String fieldName = jsonParser.getCurrentName();
// if(Objects.equals(jsonParser.currentToken(), JsonToken.FIELD_NAME)){
// jsonParser.nextToken();
// }
// if(attrNeedCnt <= 0) {
// if(fieldName == null){
// break;
// }
// jsonParser.skipChildren();
// continue;
// }
// if (fieldName == null) {
// jsonParser.nextToken();
// if(Objects.equals(jsonParser.currentToken(), JsonToken.END_OBJECT)){
// break;
// }
// }
// else if ("id".equals(fieldName)) {
// _AutoLoanId._id = jsonParser.getText() == null || (jsonParser.getText().length() == 0 ||
// JsonParserConstant.NULL.equals(jsonParser.getText()) || (jsonParser.getText().length() == 2 &&
// jsonParser.getText().contains(JsonParserConstant.DOUBLE_LINE))) ? null : Integer.parseInt(jsonParser.getText());
// attrNeedCnt--;
// }
// else {
// jsonParser.skipChildren();
// }
// }
// }
//
// }
// , com.wisecotech.strategy.rawData$zxx_application.java=
// package com.wisecotech.strategy;
//
// import java.io.Serializable;
// import javax.xml.bind.annotation.XmlAccessType;
// import javax.xml.bind.annotation.XmlAccessorType;
// import javax.xml.bind.annotation.XmlAttribute;
// import javax.xml.bind.annotation.XmlRootElement;
// import javax.xml.bind.annotation.XmlType;
// import com.fasterxml.jackson.annotation.JsonProperty;
//
// @XmlAccessorType(XmlAccessType.FIELD)
// @XmlType(name = "")
// @XmlRootElement(name = "rawData$zxx_application")
// public class rawData$zxx_application implements Serializable
// {
//
// @XmlAttribute(name = "name")
// @JsonProperty("name")
// public String _name;
//
// public String nullSafe_name() {
// return this._name;
// }
//
// }
// }
