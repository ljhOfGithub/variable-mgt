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
package com.wiseco.var.process.app.server.service.statistics;

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;
import com.wiseco.var.process.app.server.commons.util.CsvUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xupei
 */
@Slf4j
public class AlgorithmService {

    public static final int BIN_NUMBER = 10;

    private static final String DATABASE = "D:\\数据集\\creditcard67.csv";

    private static final String AUTO_LOANS = "D:\\数据集\\AutoLoans.csv";

    /**
     * 分位数计算
     * @param data 数据列
     * @param percentile 百分比
     * @return 分位数
     */
    public static Double calculatePercentile(List<Double> data, double percentile) {
        // 数据为空，则直接返回
        if (data == null || data.isEmpty()) {
            return null;
        }
        // 对数据进行排序
        Collections.sort(data);

        // 计算分位数的索引位置
        double index = percentile * (data.size() - 1) + 1;
        if (index >= data.size()) {
            return data.get(data.size() - 1);
        }

        // 如果索引位置是整数，则直接返回该位置的值
        if (index % 1 == 0) {
            return data.get((int) index - 1);
        }

        // 如果索引位置是小数，则通过线性插值计算分位数
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);
        if (upperIndex >= data.size()) {
            return data.get(lowerIndex);
        }
        double lowerValue = data.get(lowerIndex - 1);
        double upperValue = data.get(upperIndex - 1);
        return lowerValue + (index - lowerIndex) * (upperValue - lowerValue);
    }

    /**
     * 计算列iv-字符串
     * @param target Y列
     * @param data 数据列
     * @return iv值
     */
    public static Double calculateStringIv(List<Integer> target, List<String> data) {
        if (data == null || data.isEmpty() || target == null || target.isEmpty()) {
            return null;
        }
        if (data.size() != target.size()) {
            return null;
        }
        final List<Set<String>> splits = BinningUtils.calculateStringSplits(data, BIN_NUMBER);
        log.info("splits:{}", splits);
        final List<Integer> binningIndex = BinningUtils.binningString(data, splits);
        return calculateIv(target, binningIndex);
    }

    /**
     * 计算列iv-数值
     * @param target Y列
     * @param data 数据列
     * @return iv值
     */
    public static Double calculateDoubleIv(List<Integer> target, List<Double> data) {
        if (data == null || data.isEmpty() || target == null || target.isEmpty()) {
            return null;
        }
        if (data.size() != target.size()) {
            return null;
        }
        List<Double> splits = BinningUtils.calculateDoubleSplits(data, BIN_NUMBER);
        log.info("splits:{}", splits);
        final List<Integer> binningIndex = BinningUtils.binningDouble(data, splits);
        return calculateIv(target, binningIndex);
    }

    private static double calculateIv(List<Integer> target, List<Integer> binningIndex) {
        final Map<Integer, Long> binningSize = binningIndex.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        log.info("binningSize:{}", binningSize);
        double iv = BinningUtils.calculateIv(binningIndex, target);
        log.info("iv:{}", iv);
        return iv;
    }

    /**
     * 计算PSI指标-字符串
     * @param data1 基准数列
     * @param data2 目标数列
     * @return PSI指标
     */
    public static Double calculateStringPsi(List<String> data1, List<String> data2) {
        if (CollectionUtils.isEmpty(data1) || data1.size() < MagicNumbers.INT_10) {
            return null;
        }
        if (CollectionUtils.isEmpty(data2) || data2.size() < MagicNumbers.INT_10) {
            return null;
        }
        final List<Set<String>> splits = BinningUtils.calculateStringSplits(data1, BIN_NUMBER);
        final List<Integer> binningIndex1 = BinningUtils.binningString(data1, splits);
        final List<Integer> binningIndex2 = BinningUtils.binningString(data2, splits);
        final int binSize = splits.size() + 1;
        final List<Double> proportions1 = BinningUtils.calculateProportion(binningIndex1, binSize);
        final List<Double> proportions2 = BinningUtils.calculateProportion(binningIndex2, binSize);
        return calculatePsi(proportions1, proportions2);
    }

    /**
     * 计算PSI指标-数值
     * @param data1 基准数列
     * @param data2 目标数列
     * @return PSI指标
     */
    public static Double calculateDoublePsi(List<Double> data1, List<Double> data2) {
        if (CollectionUtils.isEmpty(data1) || data1.size() < MagicNumbers.INT_10) {
            return null;
        }
        if (CollectionUtils.isEmpty(data2) || data2.size() < MagicNumbers.INT_10) {
            return null;
        }
        final List<Double> splits = BinningUtils.calculateDoubleSplits(data1, BIN_NUMBER);
        final List<Integer> binningIndex1 = BinningUtils.binningDouble(data1, splits);
        final List<Integer> binningIndex2 = BinningUtils.binningDouble(data2, splits);
        final int binSize = splits.size() + 1;
        final List<Double> proportions1 = BinningUtils.calculateProportion(binningIndex1, binSize);
        final List<Double> proportions2 = BinningUtils.calculateProportion(binningIndex2, binSize);
        return calculatePsi(proportions1, proportions2);
    }

    /**
     * 计算PSI指标
     * @param proportions1 基准分箱占比
     * @param proportions2 目标分箱占比
     * @return PSI指标
     */
    private static Double calculatePsi(List<Double> proportions1, List<Double> proportions2) {
        Double psi = 0.0;
        for (int i = 0; i < proportions1.size(); i++) {
            double p1 = proportions1.get(i);
            double p2 = proportions2.get(i);
            psi += (p2 - p1) * Math.log(p2 / p1);
        }
        return psi;
    }

    /**
     * testIV
     * @param type 数据类型
     */
    private static void testIv(String type) {
        final Map<String, List<String>> csvColumns = CsvUtils.readCsvColumns(getFilePath(AUTO_LOANS), "target", "cbFICO");
        List<String> target = csvColumns.get("target");
        List<String> data = csvColumns.get("cbFICO");
        List<Integer> targetInt = new ArrayList<>();
        for (String s : target) {
            targetInt.add(Integer.parseInt(s));
        }
        if (type != null) {
            List<Double> dataD = new ArrayList<>();
            for (String s : data) {
                dataD.add(Double.parseDouble(s));
            }
            final double iv = calculateDoubleIv(targetInt, dataD);
            log.info("IV: {}", iv);
        } else {
            final double iv = calculateStringIv(targetInt, data);
            log.info("IV: {}", iv);
        }
    }

    private static void testPsi() {
        final List<Double> data1 = CsvUtils.readCsvDoubleColumnFrom(getFilePath(DATABASE), "V1", "data_date", "2023/11/6", true);
        final List<Double> data2 = CsvUtils.readCsvDoubleColumnFrom(getFilePath(DATABASE), "V1", "data_date", "2023/11/6", false);
        final double v = calculateDoublePsi(data1, data2);
        log.info("PSI: {}", v);
    }

    private static String getFilePath(String path) {
        return path;
    }

}
