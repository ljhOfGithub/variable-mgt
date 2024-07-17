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
import com.wiseco.var.process.app.server.commons.test.dto.TestExcelDto;
import com.wiseco.var.process.app.server.enums.test.TestTableEnum;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 策略测试、组件测试Excel导出
 *
 * @author wangxianli
 * @since 2022/3/29
 */
public class TestExcelUtils {

    private static final String INT_TYPE = "int";
    private static final String DOUBLE_TYPE = "double";
    private static final String DATE_TYPE = "date";
    private static final String DATETIME_TYPE = "datetime";
    private static final String DELIMITER = ".";

    /**
     * 生成带有数据的 Excel workbook
     * <p>
     * 1个 DTO 对应一个工作簿页
     *
     * @param list 存储 workbook 信息的 DTO list
     * @return Excel workbook
     * @throws ParseException Excel 解析异常
     */
    public static SXSSFWorkbook getExportExcelWb(List<TestExcelDto> list) throws ParseException {

        // 第一步，新建 workbook 对象
        // XSSFWorkbook 对应一个 Excel 文件
        SXSSFWorkbook wb = new SXSSFWorkbook();
        wb.setCompressTempFiles(false);

        for (TestExcelDto testExcelDto : list) {
            // 从 DTO 文件获取 sheet 名称
            String sheetName = testExcelDto.getSheetName();
            // 第二步，在workbook中添加一个sheet，对应Excel中的页
            Sheet sheet = wb.createSheet(sheetName);
            // 向 sheet 写入 DTO 数据
            fillWorksheet(wb, sheet, testExcelDto);
        }

        return wb;
    }

    /**
     * 创建 Excel 工作簿页
     * <p>
     * 1 workbook sheet 只能有1个 table
     *
     * @param wb           Excel XSSFWorkbook 工作簿对象
     * @param sheet        待写入的工作簿页
     * @param testExcelDto 存储当前页信息的 DTO 对象
     * @throws ParseException Excel 解析异常
     */
    private static void fillWorksheet(SXSSFWorkbook wb, Sheet sheet, TestExcelDto testExcelDto) throws ParseException {
        List<String> titleList = testExcelDto.getTitleList();
        List<String> keyList = testExcelDto.getKeyList();
        // 标题格式
        CellStyle headStyle = wb.createCellStyle();
        headStyle.setAlignment(HorizontalAlignment.LEFT);
        // 声明行对象
        Row row = null;
        // 声明列对象
        Cell cell = null;
        // 行游标
        int rowStart = 0;
        // 第三步，在 sheet 中添加页名称 (第0行) tableName
        String tableName = testExcelDto.getTableName();
        // 页名称 tableName 用于规避 Excel sheetName 31字符长度限制 (仅出现于附表, 不出现于主表)
        if (!TestTableEnum.MASTER.getMessage().equals(tableName) && !TestTableEnum.EXPECT.getMessage().equals(tableName)
                && !TestTableEnum.TEST_RESULT.getMessage().equals(tableName)) {
            row = sheet.createRow(rowStart);
            cell = row.createCell(0);
            cell.setCellValue(tableName);
            cell.setCellStyle(headStyle);
            rowStart += 1;
        }
        // ***** 创建中文标题 title *****
        // 标题列宽度记录 Map
        Map<Integer, Integer> maxWidth = new HashMap<>(MagicNumbers.TEN);
        row = sheet.createRow(rowStart);
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            // 去除结果 key 预期值前缀 (TestTableEnum.EXPECT)
            if (title.startsWith(TestTableEnum.EXPECT.getCode())) {
                title = title.substring(title.indexOf(DELIMITER) + 1);
            }
            cell = row.createCell(i);
            cell.setCellValue(title);
            cell.setCellStyle(headStyle);
            if (titleList.get(i).startsWith(TestTableEnum.EXPECT.getCode())) {
                sheet.addMergedRegion(new CellRangeAddress(rowStart, rowStart, i, i + 1));
                // 组合title: 记录拼接 title 的一半宽度2次
                int titleLen = title.getBytes(StandardCharsets.UTF_8).length;
                if (titleLen > MagicNumbers.INT_100) {
                    titleLen = MagicNumbers.INT_100;
                }
                maxWidth.put(i, titleLen * MagicNumbers.INT_128 + MagicNumbers.INT_256);
                maxWidth.put(i + 1, titleLen * MagicNumbers.INT_128 + MagicNumbers.INT_256);
                i++;
            } else {
                int titleLen = cell.getStringCellValue().getBytes(StandardCharsets.UTF_8).length;
                if (titleLen > MagicNumbers.INT_100) {
                    titleLen = MagicNumbers.INT_100;
                }
                maxWidth.put(i, titleLen * MagicNumbers.INT_256 + MagicNumbers.INT_512);
            }
        }
        // ***** 创建英文标题 key, 设定标题宽度 *****
        rowStart += 1;
        row = sheet.createRow(rowStart);
        for (int i = 0; i < keyList.size(); i++) {
            cell = row.createCell(i);
            String index = keyList.get(i);
            cell.setCellValue(index);
            cell.setCellStyle(headStyle);
            // 更新并设定列宽度，宽度值为标题单元格最大宽度
            int titleLen = index.getBytes(StandardCharsets.UTF_8).length;
            if (titleLen > MagicNumbers.INT_100) {
                titleLen = MagicNumbers.INT_100;
            }
            int len = titleLen * MagicNumbers.INT_256 + MagicNumbers.INT_512;
            sheet.setColumnWidth(i, Math.max(len, maxWidth.get(i)));
        }
        //填充内容数据
        fillExcelContent(wb, sheet, testExcelDto, rowStart);
    }

    /**
     * 填充内容
     *
     * @param wb
     * @param sheet
     * @param testExcelDto
     * @param rowStart
     * @throws ParseException
     */
    private static void fillExcelContent(SXSSFWorkbook wb, Sheet sheet, TestExcelDto testExcelDto, int rowStart) throws ParseException {
        // ***** 表格是否填充内容判断 *****
        List<List<String>> valueList = testExcelDto.getValueList();
        if (valueList == null || valueList.isEmpty()) {
            return;
        }
        // 创建"居左"格式
        CellStyle styleCenter = wb.createCellStyle();
        styleCenter.setAlignment(HorizontalAlignment.LEFT);
        // ***** 表格类型处理, 声明行对象,列对象*****
        Row row = null;
        Cell cell = null;
        List<String> typeList = testExcelDto.getTypeList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CellStyle dateCellStyle = wb.createCellStyle();
        short dfs = wb.createDataFormat().getFormat("yyyy-mm-dd");
        dateCellStyle.setDataFormat(dfs);
        CellStyle dateTimeCellStyle = wb.createCellStyle();
        short dtfs = wb.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss");
        dateTimeCellStyle.setDataFormat(dtfs);
        // ***** 填充数据 *****
        rowStart += 1;
        for (int i = 0; i < valueList.size(); i++) {
            int rowNum = i + rowStart;
            row = sheet.createRow(rowNum);
            List<String> list = valueList.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (StringUtils.isEmpty(list.get(j))) {
                    cell = row.createCell(j, CellType.STRING);
                    cell.setCellValue("");
                } else if (INT_TYPE.equals(typeList.get(j))) {
                    if (TestValidateUtil.isNumeric(list.get(j))) {
                        cell = row.createCell(j);
                        cell.setCellValue(Integer.parseInt(list.get(j)));
                    } else {
                        cell = row.createCell(j, CellType.STRING);
                        cell.setCellValue(list.get(j));
                    }
                } else if (DOUBLE_TYPE.equals(typeList.get(j))) {
                    if (TestValidateUtil.isDouble(list.get(j))) {
                        cell = row.createCell(j);
                        cell.setCellValue(Double.parseDouble(list.get(j)));
                    } else {
                        cell = row.createCell(j, CellType.STRING);
                        cell.setCellValue(list.get(j));
                    }
                } else if (DATE_TYPE.equals(typeList.get(j))) {
                    Date parsedDate;
                    if (NumberUtils.isDigits(list.get(j))) {
                        // 待转换日期为纯数字 (毫秒): 转换 String 为 Long 后生成日期
                        parsedDate = new Date(Long.parseLong(list.get(j)));
                    } else {
                        parsedDate = sdf.parse(list.get(j));
                    }
                    cell = row.createCell(j, CellType.NUMERIC);
                    cell.setCellValue(parsedDate);
                    cell.setCellStyle(dateCellStyle);
                } else if (DATETIME_TYPE.equals(typeList.get(j))) {
                    if (list.get(j).contains(",")) {
                        //基础类型数组，逗号分开，按字符串处理
                        cell = row.createCell(j, CellType.STRING);
                        cell.setCellValue(list.get(j));
                    } else {
                        Date parsedDate;
                        parsedDate = NumberUtils.isDigits(list.get(j)) ? new Date(Long.parseLong(list.get(j))) : sdtf.parse(list.get(j));
                        cell = row.createCell(j, CellType.NUMERIC);
                        cell.setCellValue(parsedDate);
                        cell.setCellStyle(dateTimeCellStyle);
                    }
                } else {
                    cell = row.createCell(j, CellType.STRING);
                    cell.setCellValue(list.get(j));
                }
            }
        }
    }

    /**
     * 设置响应头部
     * @param response HttpServletResponse对象
     * @param fileName 文件名
     * @throws Exception 异常
     */
    public static void setResponseHeader(HttpServletResponse response, String fileName) throws Exception {
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");

    }

}
