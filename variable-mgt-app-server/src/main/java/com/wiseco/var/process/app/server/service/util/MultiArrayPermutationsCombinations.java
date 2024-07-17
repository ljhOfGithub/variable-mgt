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
package com.wiseco.var.process.app.server.service.util;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

public class MultiArrayPermutationsCombinations {
    /*public static void main(String[] args) {
        //        String[][] arrays = {{"a","h"}, {"b", "c"},{"d","l"}};
        //
        //        List<List<String>> permutations = generateMultiArrayPermutations(arrays);
        //        for (List<String> permutation : permutations) {
        //        }

        // 阿甘测试
        // p1
        List<UserSingleInput> p1List = new ArrayList<>();
        p1List.add(UserSingleInput.builder().code("p1-input1").name("p1-input1").value("p1-input1").belongParam("p1").build());
        p1List.add(UserSingleInput.builder().code("p1-input2").name("p1-input2").value("p1-input2").belongParam("p1").build());
        // p2
        List<UserSingleInput> p2List = new ArrayList<>();
        p2List.add(UserSingleInput.builder().code("p2-input1").name("p2-input1").value("p2-input1").belongParam("p2").build());
        p2List.add(UserSingleInput.builder().code("p2-input2").name("p2-input2").value("p2-input2").belongParam("p2").build());
        // p3
        List<UserSingleInput> p3List = new ArrayList<>();
        p3List.add(UserSingleInput.builder().code("p3-input1").name("p3-input1").value("p3-input1").belongParam("p3").build());
        p3List.add(UserSingleInput.builder().code("p3-input2").name("p3-input2").value("p3-input2").belongParam("p3").build());

        Object[][] arrays = {p1List.toArray(), p2List.toArray(), p3List.toArray()};
        List<List<Object>> permutations = generateMultiArrayPermutations(arrays);
        for (List<Object> permutation : permutations) {
        }

    }*/

    /**
     * generateMultiArrayPermutations
     * @param objects objects二维数组
     * @return permutations
     */
    public static List<List<Object>> generateMultiArrayPermutations(Object[][] objects) {
        List<List<Object>> permutations = new ArrayList<>();
        generateMultiArrayPermutationsHelper(objects, 0, new ArrayList<>(), permutations);
        return permutations;
    }

    private static void generateMultiArrayPermutationsHelper(Object[][] arrays, int currentIndex, List<Object> currentPermutation,
                                                             List<List<Object>> permutations) {
        if (currentIndex == arrays.length) {
            permutations.add(new ArrayList<>(currentPermutation));
            return;
        }

        Object[] currentArray = arrays[currentIndex];
        for (int i = 0; i < currentArray.length; i++) {
            currentPermutation.add(currentArray[i]);
            generateMultiArrayPermutationsHelper(arrays, currentIndex + 1, currentPermutation, permutations);
            currentPermutation.remove(currentPermutation.size() - 1);
        }
    }

    @Data
    @Builder
    @ToString
    static class UserSingleInput {
        private String name;
        private String code;
        private String value;
        private String belongParam;
    }

}
