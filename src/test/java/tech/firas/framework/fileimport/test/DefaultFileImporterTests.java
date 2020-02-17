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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tech.firas.framework.fileimport.CsvRowToJavaObjectConverter;
import tech.firas.framework.fileimport.DataFileContext;
import tech.firas.framework.fileimport.DataRowContext;
import tech.firas.framework.fileimport.DefaultDataFileImporterBase;
import tech.firas.framework.fileimport.DefaultPlainTextDataFileReader;
import tech.firas.framework.fileimport.DefaultStringRowToJavaObjectConverter;
import tech.firas.framework.fileimport.FixedNumberHeaderRowJudge;
import tech.firas.framework.fileimport.ImportContext;
import tech.firas.framework.fileimport.RowType;

public class DefaultFileImporterTests {

    @Test
    public void test() throws NoSuchMethodException, ClassNotFoundException {
        final List<String> fieldNames = Arrays.asList("aaAa", "bbBb", "ccCc", "ddDd", "eeEe", "ffFf", "ggGg", "hhHh");

        final FixedNumberHeaderRowJudge<String> judge = new FixedNumberHeaderRowJudge<>();
        judge.setNumberOfHeaderRows(1);

        final DefaultStringRowToJavaObjectConverter<BeanForTest> converter =
                new DefaultStringRowToJavaObjectConverter<>(BeanForTest.class.getName());
        converter.setFieldNames(fieldNames);

        final DefaultPlainTextDataFileReader dataFileReader = new DefaultPlainTextDataFileReader();
        dataFileReader.setDataRowJudge(judge);

        final DefaultDataFileImporterBase<String, BeanForTest> importer = new TestDataFileImporter(5);
        importer.setBaseDirectory("src/test/resources");
        importer.setFileNamePattern("default_plain_text_data_file\\.txt");
        importer.setDataFileReader(dataFileReader);
        importer.setRowToJavaObjectConverter(converter);
        testFileImporter(importer);

        final CsvRowToJavaObjectConverter<BeanForTest> csvConverter =
                new CsvRowToJavaObjectConverter<>(BeanForTest.class.getName());
        csvConverter.setFieldNames(fieldNames);

        final DefaultDataFileImporterBase<String, BeanForTest> csvImporter = new TestCsvFileImporter();
        csvImporter.setBaseDirectory("src/test/resources");
        csvImporter.setFileNamePattern("default_plain_text_data_file\\.csv");
        csvImporter.setDataFileReader(dataFileReader);
        csvImporter.setRowToJavaObjectConverter(csvConverter);
        testFileImporter(csvImporter);
    }

    private void testFileImporter(final DefaultDataFileImporterBase<String, BeanForTest> importer) {
        final ImportContext importContext = importer.call();
        Assert.assertNotNull(importContext);
        Assert.assertTrue(importContext.isSuccessful());
        Assert.assertEquals("1 file(s) imported", importContext.getMessage());
    }

    private static class TestDataFileImporter extends DefaultDataFileImporterBase<String, BeanForTest> {

        private final int totalRows;
        TestDataFileImporter(final int totalRows) {
            this.totalRows = totalRows;
        }

        @Override
        protected void processOneRow(final String filePath, final DataFileContext dataFileContext,
                final DataRowContext<String> row) {
            switch (row.getRowNumber()) {
                case 1:
                    Assert.assertEquals(RowType.HEADER, row.getType());
                    break;
                case 2:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final BeanForTest a1 = super.getRowToJavaObjectConverter().convert(row.getRow());
                    Assert.assertNotNull(a1);
                    Assert.assertTrue(a1.isAaAa());
                    Assert.assertEquals(Boolean.TRUE, a1.getCcCc());
                    Assert.assertEquals(Integer.MIN_VALUE, a1.getDdDd());
                    Assert.assertEquals(Integer.valueOf(Integer.MAX_VALUE), a1.getEeEe());
                    Assert.assertEquals(12345678.09, a1.getFfFf(), 1e-8);
                    Assert.assertEquals(Double.valueOf("-12345678.09"), a1.getGgGg());
                    Assert.assertEquals("cdeCDE 123!@#$%^&*()-=_+[]\\{}|;':\"<>?./", a1.getHhHh());
                    dataFileContext.setAttachment(a1);
                    break;
                case 3:
                    final Object a = dataFileContext.getAttachment();
                    Assert.assertTrue(a instanceof BeanForTest);

                    Assert.assertEquals(RowType.DATA, row.getType());
                    final BeanForTest a2 = super.getRowToJavaObjectConverter().convert(row.getRow());
                    Assert.assertEquals(a, a2);
                    break;
                case 4:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final BeanForTest a3 = super.getRowToJavaObjectConverter().convert(row.getRow());
                    Assert.assertNotNull(a3);
                    Assert.assertFalse(a3.isAaAa());
                    Assert.assertNull(a3.getCcCc());
                    Assert.assertEquals(0, a3.getDdDd());
                    Assert.assertNull(a3.getEeEe());
                    Assert.assertEquals(0.001, a3.getFfFf(), 1e-8);
                    Assert.assertNull(a3.getGgGg());
                    Assert.assertEquals("cdeCDE 098!@#$%^&*()-=_+[]\\{}|;':\"<>?./", a3.getHhHh());
                    break;
                case 5:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final BeanForTest a4 = super.getRowToJavaObjectConverter().convert(row.getRow());
                    Assert.assertNotNull(a4);
                    Assert.assertFalse(a4.isAaAa());
                    Assert.assertEquals(Boolean.FALSE, a4.getCcCc());
                    Assert.assertEquals(0, a4.getDdDd());
                    Assert.assertEquals(Integer.valueOf(0), a4.getEeEe());
                    Assert.assertEquals(-0.001, a4.getFfFf(), 1e-8);
                    Assert.assertNull(a4.getGgGg());
                    Assert.assertNull(a4.getHhHh());
                    break;
                default:
                    if (row.getRowNumber() < 1 && row.getRowNumber() > this.totalRows) {
                        Assert.fail("Row number is expected to be 1 - " + this.totalRows + ": " + row.getRowNumber());
                    }
            }
        }
    }

    private static final class TestCsvFileImporter extends TestDataFileImporter {

        private TestCsvFileImporter() {
            super(9);
        }

        @Override
        protected void processOneRow(final String filePath, final DataFileContext dataFileContext,
                final DataRowContext<String> row) {
            switch (row.getRowNumber()) {
                case 6:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final BeanForTest a1 = super.getRowToJavaObjectConverter().convert(row.getRow());
                    Assert.assertNotNull(a1);
                    Assert.assertTrue(a1.isAaAa());
                    Assert.assertEquals(Boolean.TRUE, a1.getCcCc());
                    Assert.assertEquals(Integer.MIN_VALUE, a1.getDdDd());
                    Assert.assertEquals(Integer.valueOf(Integer.MAX_VALUE), a1.getEeEe());
                    Assert.assertEquals(12345678.09, a1.getFfFf(), 1e-8);
                    Assert.assertEquals(Double.valueOf("-12345678.09"), a1.getGgGg());
                    Assert.assertEquals("cdeCDE 123!@#$%^&*()-=_+[]\\{}|;':\"<>?,./", a1.getHhHh());
                    dataFileContext.setAttachment(a1);
                    break;
                case 7:
                    final Object a = dataFileContext.getAttachment();
                    Assert.assertTrue(a instanceof BeanForTest);

                    Assert.assertEquals(RowType.DATA, row.getType());
                    final BeanForTest a2 = super.getRowToJavaObjectConverter().convert(row.getRow());
                    Assert.assertEquals(a, a2);
                    break;
                case 8:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final BeanForTest a3 = super.getRowToJavaObjectConverter().convert(row.getRow());
                    Assert.assertNotNull(a3);
                    Assert.assertFalse(a3.isAaAa());
                    Assert.assertNull(a3.getCcCc());
                    Assert.assertEquals(0, a3.getDdDd());
                    Assert.assertNull(a3.getEeEe());
                    Assert.assertEquals(0.001, a3.getFfFf(), 1e-8);
                    Assert.assertNull(a3.getGgGg());
                    Assert.assertEquals("cdeCDE 098!@#$%^&*()-=_+[]\\{}|;':\"<>?,./", a3.getHhHh());
                    break;
                case 9:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final BeanForTest a4 = super.getRowToJavaObjectConverter().convert(row.getRow());
                    Assert.assertNotNull(a4);
                    Assert.assertFalse(a4.isAaAa());
                    Assert.assertEquals(Boolean.FALSE, a4.getCcCc());
                    Assert.assertEquals(0, a4.getDdDd());
                    Assert.assertEquals(Integer.valueOf(0), a4.getEeEe());
                    Assert.assertEquals(-0.001, a4.getFfFf(), 1e-8);
                    Assert.assertNull(a4.getGgGg());
                    Assert.assertNull(a4.getHhHh());
                    break;
                default:
                    super.processOneRow(filePath, dataFileContext, row);
            }
        }
    }
}
