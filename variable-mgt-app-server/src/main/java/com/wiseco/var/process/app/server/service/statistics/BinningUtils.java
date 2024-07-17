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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xupei
 */
@Slf4j
public class BinningUtils {
    /**
     * 计算分箱切分点 数值型
     *
     * @param values  数列
     * @param numBins 切分个数
     * @return 切分点
     */
    public static List<Double> calculateDoubleSplits(List<Double> values, int numBins) {
        HashSet<Double> uniqueValues = new HashSet<>(values);

        int numUniqueValues = uniqueValues.size();
        int actualNumSplits = Math.min(numUniqueValues, numBins);
        if (actualNumSplits < numBins) {
            final List<Double> uniqueList = new ArrayList<>(uniqueValues);
            Collections.sort(uniqueList);
            return uniqueList;
        }

        List<Double> copy = new ArrayList<>(values);
        Collections.sort(copy);
        int binSize = copy.size() / numBins;
        List<Double> splits = new ArrayList<>();
        for (int i = 0; i < numBins - 1; i++) {
            // 计算当前子集的起始索引
            int startIndex = i * binSize;
            // 计算当前子集的结束索引（不包括）
            int endIndex = (i + 1) * binSize;
            // 当前子集的最小值即为起始索引处的值
            double minVal = copy.get(startIndex);
            // 当前子集的最大值即为结束索引减一处的值
            double maxVal = copy.get(endIndex - 1);
            // 取最小值和最大值的平均值作为切分点
            double v = (minVal + maxVal) / MagicNumbers.TWO;
            // 四舍五入
            v = Math.round(v * MagicNumbers.DOUBLE_10000) / MagicNumbers.DOUBLE_10000;
            splits.add(v);
        }
        log.info("splits:{}", splits);
        return splits;
    }

    /**
     * 计算分箱切分点 字符串型
     *
     * @param values  数列
     * @param numBins 切分个数
     * @return 切分点
     */
    public static List<Set<String>> calculateStringSplits(List<String> values, int numBins) {
        List<Set<String>> splits = new ArrayList<>();
        HashSet<String> uniqueValues = new HashSet<>(values);
        int numUniqueValues = uniqueValues.size();
        int actualNumSplits = Math.min(numUniqueValues, numBins);
        if (actualNumSplits < numBins) {
            for (String uniqueValue : uniqueValues) {
                splits.add(Collections.singleton(uniqueValue));
            }
            return splits;
        }

        List<String> copy = new ArrayList<>(values);
        Collections.sort(copy);
        int binSize = copy.size() / numBins;

        Set<String> preSet = new HashSet<>();
        for (int i = 0; i < numBins - 1; i++) {
            // 计算当前子集的起始索引
            int startIndex = i * binSize;
            // 计算当前子集的结束索引（不包括）
            int endIndex = (i + 1) * binSize;
            final List<String> subList = copy.subList(startIndex, endIndex);
            final HashSet<String> subSet = new HashSet<>(subList);
            subSet.removeAll(preSet);
            if (subSet.isEmpty()) {
                continue;
            }
            preSet = subSet;
            splits.add(subSet);
        }
        return splits;
    }

    /**
     * 定义分箱
     *
     * @param values      数据
     * @param breakpoints 分箱切点
     * @return 分箱的索引
     */
    public static List<Integer> binningDouble(List<Double> values, List<Double> breakpoints) {
        Collections.sort(breakpoints);
        return values.stream().map(value -> {
            // 遍历分箱切点列表
            for (int i = 0; i < breakpoints.size(); i++) {
                double point = breakpoints.get(i);

                // 如果给定的数值小于等于当前分箱切点，则返回当前分箱的索引
                if (value <= point) {
                    return i;
                }
            }
            // 如果遍历完所有分箱切点都没有找到合适的分箱，则返回最后一个分箱的索引
            return breakpoints.size();
        }).collect(Collectors.toList());
    }

    /**
     * 定义分箱
     *
     * @param values      数据
     * @param breakpoints 分箱切点
     * @return 分箱的索引
     */
    public static List<Integer> binningString(List<String> values, List<Set<String>> breakpoints) {
        return values.stream().map(value -> {
            // 遍历分箱切点列表
            for (int i = 0; i < breakpoints.size(); i++) {
                Set<String> cut = breakpoints.get(i);
                if (cut.contains(value)) {
                    return i;
                }
            }
            // 如果遍历完所有分箱切点都没有找到合适的分箱，则返回最后一个分箱的索引
            return breakpoints.size();
        }).collect(Collectors.toList());
    }

    /**
     * 计算每个分箱的占比
     *
     * @param bins 分箱的索引
     * @param binSize 分箱数量
     * @return 分箱占比
     */
    public static List<Double> calculateProportion(List<Integer> bins, int binSize) {
        final Map<Integer, Long> collect = bins.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        // 如果有缺的分箱占比，赋值0
        if (collect.size() < binSize) {
            for (int i = 0; i < binSize; i++) {
                collect.putIfAbsent(i, 0L);
            }
        }
        final Map<Integer, Long> sortedMap = collect.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        log.info("bins:{}", sortedMap);
        final List<Double> per = sortedMap.values().stream().map(value -> value == 0 ? MagicNumbers.DOUBLE_0_00001 : value / (double) bins.size()).collect(Collectors.toList());
        log.info("per:{}", per);
        return per;
    }

    /**
     * 计算每个分箱的WOE值
     *
     * @param binningIndex 分箱的索引
     * @param target       标签值
     * @return 分箱的WOE值
     */
    public static double calculateIv(List<Integer> binningIndex, List<Integer> target) {
        // 每个分箱中正例的数量
        Map<Integer, Integer> positiveCountMap = new HashMap<>(MagicNumbers.SIXTEEN);
        // 每个分箱中负例的数量
        Map<Integer, Integer> negativeCountMap = new HashMap<>(MagicNumbers.SIXTEEN);

        int totalPositives = 0;
        int totalNegatives = 0;

        // 统计每个分箱中正例和负例的数量
        for (int i = 0; i < binningIndex.size(); i++) {
            int index = binningIndex.get(i);
            int label = target.get(i);

            if (label == 0) {
                positiveCountMap.put(index, positiveCountMap.getOrDefault(index, 0) + 1);
                totalPositives++;
            } else {
                negativeCountMap.put(index, negativeCountMap.getOrDefault(index, 0) + 1);
                totalNegatives++;
            }
        }

        double iv = 0.0;
        if (totalPositives == 0 || totalNegatives == 0) {
            return iv;
        }
        // 计算每个分箱的IV值
        for (Map.Entry<Integer, Integer> index : positiveCountMap.entrySet()) {
            int positiveCount = positiveCountMap.get(index.getKey());
            int negativeCount = negativeCountMap.getOrDefault(index.getKey(), 0);
            if (positiveCount != 0 && negativeCount != 0) {
                log.info("positiveCount: " + positiveCount + " negativeCount: " + negativeCount);
                double p = (double) positiveCount / totalPositives;
                double q = (double) negativeCount / totalNegatives;
                double binIv = (p - q) * Math.log(p / q);
                log.info("binIV: " + index.getKey() + " " + binIv);
                iv += binIv;
            }
        }
        return iv;
    }

}
