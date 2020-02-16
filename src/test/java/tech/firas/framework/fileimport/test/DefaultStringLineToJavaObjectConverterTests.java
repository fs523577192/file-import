/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.firas.framework.fileimport.test;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import tech.firas.framework.fileimport.DefaultStringLineToJavaObjectConverter;

public class DefaultStringLineToJavaObjectConverterTests {

    private static final Random random = new Random();

    @Test
    public void test() throws NoSuchMethodException, ClassNotFoundException {
        final DefaultStringLineToJavaObjectConverter<BeanForTest> converter =
                new DefaultStringLineToJavaObjectConverter<>(BeanForTest.class.getName());
        converter.setFieldNames(Arrays.asList("aaAa", "bbBb", "ccCc", "ddDd", "eeEe", "ffFf", "ggGg", "hhHh"));

        for (int i = 65536; i > 0; i -= 1) {
            final BeanForTest a1 = getAForTest();
            final String line = a1.isAaAa() + "," + getRandomColumnValue() + "," +
                    a1.getCcCc() + "," + a1.getDdDd() + "," + a1.getEeEe() + "," + a1.getFfFf() + "," +
                    a1.getGgGg() + "," + a1.getHhHh() + "," + getRandomColumnValue();
            final BeanForTest a2 = converter.convert(line);
            Assert.assertEquals(a1, a2);

            final BeanForTest a3 = converter.convert(a1.isAaAa() + "," + getRandomColumnValue() + "," +
                    a1.getCcCc() + "," + a1.getDdDd() + "," + a1.getEeEe() + "," + a1.getFfFf());
            Assert.assertNotNull(a3);
            Assert.assertNull(a3.getGgGg());
            Assert.assertNull(a3.getHhHh());
            a3.setGgGg(a1.getGgGg());
            a3.setHhHh(a1.getHhHh());
            Assert.assertEquals(a1, a3);
        }
    }

    private BeanForTest getAForTest() {
        final BeanForTest result = new BeanForTest();
        result.setAaAa(random.nextBoolean());
        result.setCcCc(random.nextBoolean());
        result.setDdDd(random.nextInt());
        result.setEeEe(random.nextInt());
        result.setFfFf(random.nextDouble());
        result.setGgGg(random.nextDouble());
        result.setHhHh(getRandomColumnValue());
        return result;
    }

    private String getRandomColumnValue() {
        final StringBuilder builder = new StringBuilder();
        for (int i = random.nextInt(10 + 1); i > 0; i -= 1) {
            int c = random.nextInt(127 - 1 - (int) ' ') + (int) ' ';
            builder.append(c >= (int) ',' ? (char) (c + 1) : (char) c);
        }
        return builder.toString();
    }
}
