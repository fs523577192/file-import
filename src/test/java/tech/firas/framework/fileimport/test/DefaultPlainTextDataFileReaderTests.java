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

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import tech.firas.framework.fileimport.DefaultLineToJavaObjectConverter;
import tech.firas.framework.fileimport.DefaultPlainTextDataFileReader;
import tech.firas.framework.fileimport.FixedNumberHeaderLineJudge;

public class DefaultPlainTextDataFileReaderTests {

    @Test
    public void test() throws NoSuchMethodException, ClassNotFoundException, IOException {
        final FixedNumberHeaderLineJudge<String> judge = new FixedNumberHeaderLineJudge<>();
        judge.setNumberOfHeaderLines(1);

        final DefaultLineToJavaObjectConverter<BeanForTest> converter =
                new DefaultLineToJavaObjectConverter<>(BeanForTest.class.getName());
        converter.setFieldNames(Arrays.asList("aaAa", "bbBb", "ccCc", "ddDd", "eeEe", "ffFf", "ggGg", "hhHh"));

        final DefaultPlainTextDataFileReader<BeanForTest> dataFileReader = new DefaultPlainTextDataFileReader<>();
        dataFileReader.setDataLineJudge(judge);
        dataFileReader.setLineToJavaObjectConverter(converter);
        Iterator<BeanForTest> iterator = dataFileReader.readDataFile("src/test/resources/default_plain_text_data_file.txt");

        Assert.assertTrue(iterator.hasNext());
        final BeanForTest a1 = iterator.next();
        Assert.assertNotNull(a1);
        Assert.assertTrue(a1.isAaAa());
        Assert.assertEquals(Boolean.TRUE, a1.getCcCc());
        Assert.assertEquals(Integer.MIN_VALUE, a1.getDdDd());
        Assert.assertEquals(Integer.valueOf(Integer.MAX_VALUE), a1.getEeEe());
        Assert.assertEquals(12345678.09, a1.getFfFf(), 1e-8);
        Assert.assertEquals(Double.valueOf("-12345678.09"), a1.getGgGg());
        Assert.assertEquals("cdeCDE 123!@#$%^&*()-=_+[]\\{}|;':\"<>?./", a1.getHhHh());

        Assert.assertTrue(iterator.hasNext());
        final BeanForTest a2 = iterator.next();
        Assert.assertEquals(a1, a2);

        Assert.assertTrue(iterator.hasNext());
        final BeanForTest a3 = iterator.next();
        Assert.assertNotNull(a3);
        Assert.assertFalse(a3.isAaAa());
        Assert.assertNull(a3.getCcCc());
        Assert.assertEquals(0, a3.getDdDd());
        Assert.assertNull(a3.getEeEe());
        Assert.assertEquals(0.001, a3.getFfFf(), 1e-8);
        Assert.assertNull(a3.getGgGg());
        Assert.assertEquals("cdeCDE 098!@#$%^&*()-=_+[]\\{}|;':\"<>?./", a3.getHhHh());

        Assert.assertTrue(iterator.hasNext());
        final BeanForTest a4 = iterator.next();
        Assert.assertNotNull(a4);
        Assert.assertFalse(a4.isAaAa());
        Assert.assertEquals(Boolean.FALSE, a4.getCcCc());
        Assert.assertEquals(0, a4.getDdDd());
        Assert.assertEquals(Integer.valueOf(0), a4.getEeEe());
        Assert.assertEquals(-0.001, a4.getFfFf(), 1e-8);
        Assert.assertNull(a4.getGgGg());
        Assert.assertNull(a4.getHhHh());

        Assert.assertFalse(iterator.hasNext());
    }
}
