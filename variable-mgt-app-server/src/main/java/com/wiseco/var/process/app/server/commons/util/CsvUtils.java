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
package com.wiseco.var.process.app.server.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xupei
 */
@Slf4j
public class CsvUtils {

    /**
     * 读取csv文件，指定读取的列
     *
     * @param csvFile csv文件
     * @param columns 列名
     * @return key:列名 value:列值
     */
    public static Map<String, List<String>> readCsvColumns(String csvFile, String... columns) {
        Map<String, List<String>> ret = new HashMap<>(columns.length);
        final CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
        try (Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(csvFile)), StandardCharsets.UTF_8)); CSVParser csvParser = new CSVParser(reader, format)) {
            for (String column : columns) {
                ret.put(column, new java.util.ArrayList<>());
            }
            for (CSVRecord csvRecord : csvParser) {
                for (String column : columns) {
                    ret.get(column).add(csvRecord.get(column));
                }
            }
            return ret;
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return Collections.emptyMap();
    }

    /**
     * 读取csv文件，指定读取的列
     *
     * @param csvFile csv文件
     * @param columnName 列名
     * @return key:列名 value:列值
     */
    public static List<Double> readCsvDoubleColumn(String csvFile, String columnName) {
        List<Double> ret = new ArrayList<>();
        final CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
        try (Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(csvFile)), StandardCharsets.UTF_8)); CSVParser csvParser = new CSVParser(reader, format)) {
            for (CSVRecord csvRecord : csvParser) {
                ret.add(NumberUtils.createDouble(csvRecord.get(columnName)));
            }
            return ret;
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return Collections.emptyList();
    }

    /**
     * 读取csv文件，指定读取的列
     *
     * @param csvFile csv文件
     * @param columnName 列名
     * @param filterColumnName 列名
     * @param filterValue 列名
     * @param op 是否相等
     * @return key:列名 value:列值
     */
    public static List<Double> readCsvDoubleColumnFrom(String csvFile, String columnName, String filterColumnName, String filterValue, boolean op) {
        List<Double> ret = new ArrayList<>();
        final CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
        try (Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(csvFile)), StandardCharsets.UTF_8)); CSVParser csvParser = new CSVParser(reader, format)) {
            for (CSVRecord csvRecord : csvParser) {
                if (op) {
                    if (csvRecord.get(filterColumnName).equals(filterValue)) {
                        ret.add(NumberUtils.createDouble(csvRecord.get(columnName)));
                    }
                } else {
                    if (!csvRecord.get(filterColumnName).equals(filterValue)) {
                        ret.add(NumberUtils.createDouble(csvRecord.get(columnName)));
                    }
                }
            }
            return ret;
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return Collections.emptyList();
    }
}
