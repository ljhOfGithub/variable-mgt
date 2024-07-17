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
package com.wiseco.var.process.app.server.commons.test;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import com.wiseco.var.process.app.server.exception.VariableMgtBusinessServiceException;
import com.wiseco.var.process.app.server.exception.VariableMgtErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * POI+SAX方式解析Excel
 *
 * @author wangxianli
 * @since 2022/6/6
 */
@Slf4j
public class TestExcelEventReaderUtil {

    public static final String M_D_YYYY = "m/d/yyyy";
    public static final String YYYY_MM_DD = "yyyy/mm/dd";
    public static final String YYYY_M_D = "yyyy/m/d";

    /**
     * 读取excel文件
     *
     * @param is                   文件流
     * @param processRowsInterface 处理行数据的接口
     */
    public void readExcel(InputStream is, ProcessRowsInterface processRowsInterface) {

        XlsxReader xlsxR = new XlsxReader();
        try (OPCPackage pkg = OPCPackage.open(is)) {

            XSSFReader xssfReader = new XSSFReader(pkg);
            xlsxR.process(xssfReader, processRowsInterface);

        } catch (Exception e) {
            e.printStackTrace();
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, e.getMessage());
        }
    }

    /**
     * 读取excel文件
     *
     * @param filePath             文件路径
     * @param processRowsInterface 处理行数据的接口
     */
    public void readExcel(String filePath, ProcessRowsInterface processRowsInterface) {
        XlsxReader xlsxR = new XlsxReader();
        try (OPCPackage pkg = OPCPackage.open(filePath)) {
            XSSFReader xssfReader = new XSSFReader(pkg);
            xlsxR.process(xssfReader, processRowsInterface);
        } catch (Exception e) {
            e.printStackTrace();
            throw new VariableMgtBusinessServiceException(VariableMgtErrorCode.COMMON_FILE_READE_ERROR, e.getMessage());
        }

    }

    /**
     * 单元格中的数据可能的类型
     */
    enum CellType {
        /**
         * 空值
         */
        NULL,
        /**
         * 日期类型
         */
        DATE,
        /**
         * INLINESTR
         */
        INLINESTR,
        /**
         * 数字类型
         */
        NUMERIC,
        /**
         * 字符串类型
         */
        STRING,
        /**
         * 公式类型
         */
        FORMULA,
        /**
         * 布尔值类型
         */
        BOOLEAN,
        /**
         * 错误类型
         */
        ERROR
    }

    /**
     * 处理行数据的接口
     */
    public interface ProcessRowsInterface {

        /**
         * 设置行数据
         *
         * @param sheetName  工作薄名称
         * @param sheetIndex 工作薄索引
         * @param curRow     当前行，不包装第一行，第一默认为标题
         * @param titles     当前行的单元格Map集合，key为单元格所对应的标题，value为单元格内容
         * @param cellMap    当前行的单元格Map集合，key为单元格所对应的标题，value为单元格内容
         */
        void setRows(String sheetName, int sheetIndex, int curRow, Map<Integer, String> titles, Map<String, Object> cellMap);
    }

    /**
     * 读取excel2007版本
     */
    public static class XlsxReader extends DefaultHandler {

        private final DataFormatter formatter = new DataFormatter();
        /**
         * 单元格元素
         */
        private static final String CELL_ELEMENT_C = "c";
        private static final String CELL_ELEMENT_T = "t";
        private static final String CELL_ELEMENT_V = "v";
        private static final String CELL_ELEMENT_S = "s";
        private static final String CELL_ELEMENT_ROW = "row";
        /**
         * 单元格类型
         */
        private static final String CELL_TYPE_B = "b";
        private static final String CELL_TYPE_E = "e";
        private static final String CELL_TYPE_INLINE_STR = "inlineStr";
        private static final String CELL_TYPE_S = "s";
        private static final String CELL_TYPE_STR = "str";
        /**
         * 共享字符串表
         */
        private SharedStringsTable sst;
        /**
         * 标题行Map集合
         */
        private Map<Integer, String> titles = new LinkedHashMap<>();
        /**
         * 上一次的索引值
         */
        private String lastIndex;
        /**
         * 工作表索引
         */
        private int sheetIndex = 0;
        /**
         * sheet名称
         */
        private String sheetName = "";
        /**
         * 一行内cell集合
         */
        private Map<String, Object> cellMap = new LinkedHashMap<>();
        /**
         * 判断整行是否为空行的标记
         */
        private boolean flag = false;
        /**
         * 当前行
         */
        private int curRow = 0;
        /**
         * 当前列
         */
        private int curCol = 0;
        /**
         * T元素标识
         */
        private boolean isT;
        /**
         * 判断上一单元格是否为文本空单元格
         */
        private boolean startElementFlag = true;
        private boolean endElementFlag = false;
        private boolean charactersFlag = false;
        /**
         * 单元格数据类型，默认为字符串类型
         */
        private CellType nextCellType = CellType.STRING;
        /**
         * 单元格日期格式的索引
         */
        private short formatIndex;
        /**
         * 日期格式字符串
         */
        private String formatString;
        /**
         * 定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等
         */
        private String preRef = null, ref = null;
        /**
         * 定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格
         */
        private String maxRef = null;
        /**
         * 单元格样式表
         */
        private StylesTable stylesTable;
        /**
         * 处理行数据的接口
         */
        private ProcessRowsInterface processRowsInterface;

        /**
         * 处理数据
         * @param xssfReader XSSFReader对象
         * @param processRowsInterface 处理行数据的接口
         * @throws Exception 异常
         */
        public void process(XSSFReader xssfReader, ProcessRowsInterface processRowsInterface) throws Exception {

            this.processRowsInterface = processRowsInterface;

            stylesTable = xssfReader.getStylesTable();
            SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();
            // xml读取类
            XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            this.sst = sharedStringsTable;
            parser.setContentHandler(this);
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            // 遍历sheet
            while (sheets.hasNext()) {
                titles = new LinkedHashMap<>();
                cellMap = new LinkedHashMap<>();
                // 标记初始行为第一行
                curRow = 0;
                sheetIndex++;
                // sheets.next()和sheets.getSheetName()不能换位置，否则sheetName报错
                InputStream sheet = sheets.next();

                sheetName = sheets.getSheetName();
                InputSource sheetSource = new InputSource(sheet);

                // 解析excel的每条记录，在这个过程中startElement()、characters()、endElement()这三个函数会依次执行
                parser.parse(sheetSource);
                sheet.close();

            }
        }

        /**
         * 重写DefaultHandler的startElement方法，该方法第一个执行
         *
         * @param uri        名称空间URI，如果元素没有名称空间URI，或者没有执行名称空间处理，则为空字符串。
         * @param localName  本地名称(没有前缀)，如果没有执行名称空间处理，则为空字符串。
         * @param qName      限定名(带前缀)，如果没有限定名，则使用空字符串。
         * @param attributes 附加到元素的属性。如果没有属性，则为空属性对象。
         * @throws SAXException 异常
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // c => 单元格
            if (CELL_ELEMENT_C.equals(qName)) {
                // 前一个单元格的位置
                if (Objects.isNull(preRef)) {
                    preRef = attributes.getValue("r");
                } else {
                    // 中部文本空单元格标识 ‘endElementFlag’
                    // 判断前一次是否为文本空字符串，true则表明不是文本空字符串，false表明是文本空字符串跳过把空字符串的位置赋予preRef
                    if (endElementFlag) {
                        preRef = ref;
                    }
                }
                // 当前单元格的位置
                ref = attributes.getValue("r");
                // 首部文本空单元格标识 ‘startElementFlag’
                // 判断前一次，即首部是否为文本空字符串，true则表明不是文本空字符串，false表明是文本空字符串,且已知当前格，即第二格带“B”标志，则ref赋予preRef
                // 上一个单元格为文本空单元格，执行下面的，使ref=preRef；flag为true表明该单元格之前有数据值，即该单元格不是首部空单元格，则跳过
                if (!startElementFlag && !flag) {
                    // 这里只有上一个单元格为文本空单元格，且之前的几个单元格都没有值才会执行
                    preRef = ref;
                }
                // 设定单元格类型
                this.setNextCellType(attributes);
                endElementFlag = false;
                charactersFlag = false;
                startElementFlag = false;
            }
            // 当元素为t时
            isT = CELL_ELEMENT_T.equals(qName);
            // 置空
            lastIndex = "";
        }

        /**
         * 重写DefaultHandler的characters方法，该方法第二个执行
         * 得到单元格对应的索引值或是内容值
         * <pre>
         * 如果单元格类型是字符串、INLINESTR、数字、日期，lastIndex则是索引值
         * 如果单元格类型是布尔值、错误、公式，lastIndex则是内容值
         * </pre>
         *
         * @param ch     字符数组
         * @param start  字符数组中的起始位置。
         * @param length 要从字符数组中使用的字符数。
         * @throws SAXException 异常
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            startElementFlag = true;
            charactersFlag = true;
            lastIndex += new String(ch, start, length);
        }

        /**
         * 重写DefaultHandler的endElement方法，该方法第三个执行
         *
         * @param uri       名称空间URI，如果元素没有名称空间URI，或者没有执行名称空间处理，则为空字符串。
         * @param localName 本地名称(没有前缀)，如果没有执行名称空间处理，则为空字符串。
         * @param qName     限定名(带前缀)，如果没有限定名，则使用空字符串。
         * @throws SAXException 异常
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            // t元素也包含字符串
            boolean isMaster = true;
            int headerLine = 1;
            Set<Integer> setInt = new HashSet<>();
            if (!sheetName.equals(TestTableEnum.MASTER.getMessage()) && !sheetName.equals(TestTableEnum.EXPECT.getMessage())) {
                isMaster = false;
                headerLine = MagicNumbers.TWO;
                setInt.add(0);
                setInt.add(1);
                setInt.add(MagicNumbers.TWO);
            } else {
                setInt.add(0);
                setInt.add(1);
            }
            if (isT || CELL_ELEMENT_V.equals(qName)) {
                String value = "";
                if (isT) {
                    // 将单元格内容加入cellMap中，在这之前先去掉字符串前后的空白符
                    value = lastIndex.trim();

                } else if (CELL_ELEMENT_V.equals(qName)) {
                    // v => 单元格的值，如果单元格是字符串，则v标签的值为该字符串在SST中的索引
                    // 根据索引值获取对应的单元格值
                    value = this.getDataValue(lastIndex.trim());
                }
                extracted(isMaster, value);
                curCol++;
                endElementFlag = true;
                if (isT) {

                    isT = false;
                    // 如果里面某个单元格含有值，则标识该行不为空行
                    if (Objects.nonNull(value) && value.length() != 0) {
                        flag = true;
                    }
                } else if (CELL_ELEMENT_V.equals(qName)) {
                    // 如果里面某个单元格含有值，则标识该行不为空行
                    if (Objects.nonNull(value) && value.length() != 0) {
                        flag = true;
                    }
                }
            } else {
                // 如果标签名称为row，这说明已到行尾，调用setRows()方法
                if (CELL_ELEMENT_ROW.equals(qName)) {
                    // 默认第一行为表头，以该行单元格数目为最大数目
                    if (curRow == headerLine) {
                        maxRef = ref;
                    }
                    // 补全一行尾部可能缺失的单元格
                    if (Objects.nonNull(maxRef)) {
                        int len = MagicNumbers.MINUS_INT_1;
                        // 前一单元格，true则不是文本空字符串，false则是文本空字符串
                        if (charactersFlag) {
                            len = countNullCell(maxRef, ref);
                        } else {
                            len = countNullCell(maxRef, preRef);
                        }
                        for (int i = 0; i <= len; i++) {
                            if (titles.size() > 0 && !StringUtils.isEmpty(titles.get(curCol))) {
                                cellMap.put(titles.get(curCol), "");
                            }
                            curCol++;
                        }
                    }
                    // 该行不为空行且该行不是第一行，则设置（第一行为列名，不需要）
                    if (flag && !setInt.contains(curRow)) {
                        processRowsInterface.setRows(sheetName, sheetIndex, curRow, titles, cellMap);
                        cellMap = new LinkedHashMap<>();
                    }
                    curRow++;
                    curCol = 0;
                    preRef = null;
                    ref = null;
                    flag = false;
                }
            }
        }

        private void extracted(boolean isMaster, String value) {
            if (isMaster) {
                if (curRow == 0) {
                    log.info("第一行是中文描述，跳过");
                } else if (curRow == 1) {
                    // 表头,补全标题行单元格之间的空单元格
                    if (!ref.equals(preRef)) {
                        int len = countNullCell(ref, preRef);
                        for (int i = 0; i < len; i++) {
                            titles.put(curCol, "空标题" + curCol);
                            curCol++;
                        }
                        // ref等于preRef，且以B或者C...开头，表明首部为空格
                    } else {
                        int len = countNullCell(ref, "A");
                        for (int i = 0; i < len; i++) {
                            titles.put(curCol, "空标题" + curCol);
                            curCol++;
                        }
                    }
                    titles.put(curCol, value);
                } else {
                    // 补全单元格之间的空单元格
                    fillEmpty(value);
                }
            } else {
                if (curRow == 0) {
                    sheetName = value;
                    //附表全路径、名称
                } else if (curRow == 1) {
                    log.info("curRow == 1. ==> skip");
                } else if (curRow == NumberUtils.INTEGER_TWO) {
                    // 补全标题行单元格之间的空单元格
                    if (!ref.equals(preRef)) {
                        int len = countNullCell(ref, preRef);
                        for (int i = 0; i < len; i++) {
                            titles.put(curCol, "空标题" + curCol);
                            curCol++;
                        }
                        // ref等于preRef，且以B或者C...开头，表明首部为空格
                    } else {
                        int len = countNullCell(ref, "A");
                        for (int i = 0; i < len; i++) {
                            titles.put(curCol, "空标题" + curCol);
                            curCol++;
                        }
                    }
                    titles.put(curCol, value);
                } else {
                    // 补全单元格之间的空单元格
                    fillEmpty(value);
                }
            }
        }

        private void fillEmpty(String value) {
            if (!ref.equals(preRef)) {
                int len = countNullCell(ref, preRef);
                for (int i = 0; i < len; i++) {
                    cellMap.put(titles.get(curCol), "");
                    curCol++;
                }
                // ref等于preRef，且以B或者C...开头，表明首部为空格
            } else {
                cellMap.put(titles.get(curCol), "");
                int len = countNullCell(ref, "A");
                for (int i = 0; i < len; i++) {
                    cellMap.put(titles.get(curCol), "");
                    curCol++;
                }
            }
            cellMap.put(titles.get(curCol), value);
        }

        /**
         * 处理数据类型
         *
         * @param attributes 属性
         */
        public void setNextCellType(Attributes attributes) {
            // cellType为空，则表示该单元格类型为数字
            nextCellType = CellType.NUMERIC;
            formatIndex = MagicNumbers.MINUS_INT_1;
            formatString = null;
            // 单元格类型
            String cellType = attributes.getValue(CELL_ELEMENT_T);
            String cellStyleStr = attributes.getValue(CELL_ELEMENT_S);
            // 处理布尔值
            if (CELL_TYPE_B.equals(cellType)) {
                nextCellType = CellType.BOOLEAN;
                // 处理错误
            } else if (CELL_TYPE_E.equals(cellType)) {
                nextCellType = CellType.ERROR;
            } else if (CELL_TYPE_INLINE_STR.equals(cellType)) {
                nextCellType = CellType.INLINESTR;
                // 处理字符串
            } else if (CELL_TYPE_S.equals(cellType)) {
                nextCellType = CellType.STRING;
                // 处理公式
            } else if (CELL_TYPE_STR.equals(cellType)) {
                nextCellType = CellType.FORMULA;
            }
            // 处理日期
            if (Objects.nonNull(cellStyleStr)) {
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                formatIndex = style.getDataFormat();
                formatString = style.getDataFormatString();
                if (formatString.contains(M_D_YYYY) || formatString.contains(YYYY_MM_DD) || formatString.contains(YYYY_M_D)) {
                    nextCellType = CellType.DATE;
                    formatString = "yyyy-MM-dd hh:mm:ss";
                }
                if (Objects.isNull(formatString)) {
                    nextCellType = CellType.NULL;
                    formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
                }
            }
        }

        /**
         * 对解析出来的数据进行类型处理
         *
         * @param value 单元格的值
         *              value代表解析：BOOL的为0或1， ERROR的为内容值，FORMULA的为内容值，INLINESTR的为索引值需转换为内容值，
         *              SSTINDEX的为索引值需转换为内容值， NUMBER为内容值，DATE为内容值
         * @return String
         */
        private String getDataValue(String value) {
            String thisStr = "";
            // 这几个的顺序不能随便交换，交换了很可能会导致数据错误
            switch (nextCellType) {
                // 布尔值
                case BOOLEAN:
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;
                // 错误
                case ERROR:
                    thisStr = "\"ERROR:" + value + '"';
                    break;
                // 公式
                case FORMULA:
                    thisStr = '"' + value + '"';
                    break;
                case INLINESTR:
                    XSSFRichTextString rtsi = new XSSFRichTextString(value);
                    thisStr = rtsi.toString();
                    rtsi = null;
                    break;
                // 字符串
                case STRING:
                    String sstIndex = value;
                    try {
                        int idx = Integer.parseInt(sstIndex);

                        XSSFRichTextString rtss = (XSSFRichTextString) sst.getItemAt(idx);
                        thisStr = rtss.toString();
                        // 有些字符串是文本格式的，但内容却是日期
                        rtss = null;
                    } catch (NumberFormatException ex) {
                        thisStr = value;
                    }
                    break;
                // 数字
                case NUMERIC:
                    if (Objects.nonNull(formatString)) {
                        thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();
                    } else {
                        BigDecimal bigDecimal = new BigDecimal(value);
                        value = bigDecimal.stripTrailingZeros().toPlainString();

                        thisStr = value;
                    }
                    thisStr = thisStr.replace("_", "").trim();
                    break;
                // 日期
                case DATE:
                    thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
                    // 对日期字符串作特殊处理，去掉T
                    thisStr = thisStr.replace("T", " ");
                    break;
                default:
                    thisStr = " ";
                    break;
            }
            return thisStr;
        }

        /**
         * 得到两个单元格之间的空单元格数量
         *
         * @param ref    当前单元格位置
         * @param preRef 前一个单元格位置
         * @return int
         */
        private int countNullCell(String ref, String preRef) {
            // excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
            String xfd = ref.replaceAll("\\d+", "");
            String xfd1 = preRef.replaceAll("\\d+", "");

            xfd = fillChar(xfd, MagicNumbers.THREE, '@', true);
            xfd1 = fillChar(xfd1, MagicNumbers.THREE, '@', true);

            char[] letter = xfd.toCharArray();
            char[] letter1 = xfd1.toCharArray();
            int res = (letter[0] - letter1[0]) * MagicNumbers.INT_26 * MagicNumbers.INT_26 + (letter[1] - letter1[1]) * MagicNumbers.INT_26 + (letter[MagicNumbers.TWO] - letter1[MagicNumbers.TWO]);
            return res - 1;
        }

        private String fillChar(String str, int len, char let, boolean isPre) {
            int strLen = str.length();
            if (strLen < len) {
                if (isPre) {
                    for (int i = 0; i < (len - strLen); i++) {
                        str = let + str;
                    }
                } else {
                    for (int i = 0; i < (len - strLen); i++) {
                        str = str + let;
                    }
                }
            }
            return str;
        }

    }

}
