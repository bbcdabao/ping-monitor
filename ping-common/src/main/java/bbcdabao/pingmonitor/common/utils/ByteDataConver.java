/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package bbcdabao.pingmonitor.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Zookeeper data conver utils
 */
public class ByteDataConver {

    @FunctionalInterface
    public static interface IConvertToByte<T> {
        byte[] getData(T param);
    }

    @FunctionalInterface
    public static interface IConvertFromByte<T> {
        T getValue(byte[] param);
    }

    private static class Holder {
        private static final ByteDataConver INSTANCE = new ByteDataConver();
    }

    public static ByteDataConver getInstance() {
        return Holder.INSTANCE;
    }

    private ByteDataConver() {
    }

    public IConvertToByte<Integer> getConvertToByteForInteger() {
        return param -> {
            return Integer.toString(param).getBytes();
        };
    }

    public IConvertFromByte<Integer> getConvertFromByteForInteger() {
        return param -> {
            return Integer.valueOf(new String(param));
        };
    }

    public IConvertToByte<Long> getConvertToByteForLong() {
        return param -> {
            return Long.toString(param).getBytes();
        };
    }

    public IConvertFromByte<Long> getConvertFromByteForLong() {
        return param -> {
            return Long.valueOf(new String(param));
        };
    }

    public IConvertToByte<String> getConvertToByteForString() {
        return param -> {
            return param.getBytes(StandardCharsets.UTF_8);
        };
    }

    public IConvertFromByte<String> getConvertFromByteForString() {
        return param -> {
            return new String(param,public class ByteDataConver {

}
 StandardCharsets.UTF_8);
        };
    }

    /**
     * true -> 1, false -> 0
     * @return
     */
    public IConvertToByte<Boolean> getConvertToByteForBoolean() {
        return param -> {
            return new byte[]{(byte) (param ? 1 : 0)};
        };
    }

    /**
     * 1 -> true, 0 -> false
     * @return
     */
    public IConvertFromByte<Boolean> getConvertFromByteForBoolean() {
        return param -> {
            return param[0] == 1;
        };
    }

    public IConvertToByte<Properties> getConvertToByteForProperties() {
        return param -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                param.store(baos, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return baos.toByteArray();
        };
    }

    public IConvertFromByte<Properties> getConvertFromByteForProperties() {
        return param -> {
            Properties properties = new Properties();
            try {
                properties.load(new ByteArrayInputStream(param));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return properties;
        };
    }
}
