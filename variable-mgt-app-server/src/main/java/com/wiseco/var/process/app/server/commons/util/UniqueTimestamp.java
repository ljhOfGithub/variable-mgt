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

import com.wiseco.var.process.app.server.commons.constant.MagicNumbers;

/**
 * @author liaody
 * @Description
 * @create 2021/11/19
 */
public class UniqueTimestamp {
    public static final char PRECISION_MILLISECOND = 'S';
    public static final char PRECISION_SECOND = 's';
    public static final char PRECISION_MINUTE = 'm';
    private char precision = 'S';
    private long ts = MagicNumbers.MINUS_LONG_1;

    /**
     * constructor
     */
    public UniqueTimestamp() {

    }

    /**
     * UniqueTimestamp
     * 
     * @param prec char
     */
    public UniqueTimestamp(char prec) {
        if ((prec != PRECISION_MILLISECOND) && (prec != PRECISION_SECOND) && (prec != PRECISION_MINUTE)) {
            throw new IllegalArgumentException("Illegal argument for UniqueTimestamp. Only 'S', 's','m' can be used.");
        }
        this.precision = prec;
    }

    /**
     * getUniqueTimestamp
     * 
     * @return long
     */
    public synchronized long getUniqueTimestamp() {
        if (this.ts == MagicNumbers.MINUS_LONG_1) {
            this.ts = getCurrentTimeValue();
        } else {
            long t = getCurrentTimeValue();
            if (t <= this.ts) {
                this.ts += 1L;
            } else {
                this.ts = t;
            }
        }
        return getReturnValue(this.ts);
    }

    private long getCurrentTimeValue() {
        long ts1;
        switch (this.precision) {
            case 'S':
            default:
                ts1 = System.currentTimeMillis();
                break;
            case 's':
                ts1 = System.currentTimeMillis() / MagicNumbers.LONG_1000;
                break;
            case 'm':
                ts1 = System.currentTimeMillis() / MagicNumbers.LONG_60000;
                break;
        }
        return ts1;
    }

    private long getReturnValue(long ts) {
        switch (this.precision) {
            case 'S':
            case 's':
                return ts * MagicNumbers.LONG_1000;
            default:
                return ts;
        }
    }
}
