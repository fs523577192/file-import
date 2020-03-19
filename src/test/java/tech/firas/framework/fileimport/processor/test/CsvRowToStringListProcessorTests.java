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
package tech.firas.framework.fileimport.processor.test;

import java.util.List;

import com.opencsv.RFC4180Parser;
import org.junit.Assert;
import org.junit.Test;

import tech.firas.framework.fileimport.DataFileContext;
import tech.firas.framework.fileimport.DataRowContext;
import tech.firas.framework.fileimport.DefaultPlainTextDataFileReader;
import tech.firas.framework.fileimport.FixedNumberHeaderRowJudge;
import tech.firas.framework.fileimport.RowType;
import tech.firas.framework.fileimport.processor.CsvRowToStringListProcessor;
import tech.firas.framework.fileimport.processor.DataFileProcessor;
import tech.firas.framework.fileimport.processor.SetRowTypeProcessor;
import tech.firas.framework.fileimport.test.AbstractTests;

public class CsvRowToStringListProcessorTests extends AbstractTests {

    @Test
    public void testTxt() throws Exception {
        final CsvRowToStringListProcessor toStringListProcessor = new CsvRowToStringListProcessor();
        toStringListProcessor.setCsvParser(new RFC4180Parser());
        toStringListProcessor.setNextProcessor(new TxtTestProcessor(5));

        final FixedNumberHeaderRowJudge<String> rowJudge = new FixedNumberHeaderRowJudge<>();
        rowJudge.setNumberOfHeaderRows(1);
        final SetRowTypeProcessor<String> setRowTypeProcessor = new SetRowTypeProcessor<>();
        setRowTypeProcessor.setDataRowJudge(rowJudge);
        setRowTypeProcessor.setNextProcessor(toStringListProcessor);

        final DefaultPlainTextDataFileReader reader = new DefaultPlainTextDataFileReader();
        reader.setDataFileProcessor(setRowTypeProcessor);
        reader.readDataFile("src/test/resources/default_plain_text_data_file.txt", null);
    }

    @Test
    public void testCsv() throws Exception {
        final CsvRowToStringListProcessor toStringListProcessor = new CsvRowToStringListProcessor();
        toStringListProcessor.setCsvParser(new RFC4180Parser());
        toStringListProcessor.setNextProcessor(new CsvTestProcessor(5));

        final FixedNumberHeaderRowJudge<String> rowJudge = new FixedNumberHeaderRowJudge<>();
        rowJudge.setNumberOfHeaderRows(1);
        final SetRowTypeProcessor<String> setRowTypeProcessor = new SetRowTypeProcessor<>();
        setRowTypeProcessor.setDataRowJudge(rowJudge);
        setRowTypeProcessor.setNextProcessor(toStringListProcessor);

        final DefaultPlainTextDataFileReader reader = new DefaultPlainTextDataFileReader();
        reader.setDataFileProcessor(setRowTypeProcessor);
        reader.readDataFile("src/test/resources/default_plain_text_data_file.csv", null);
    }

    private static class TxtTestProcessor implements DataFileProcessor<List<String>> {

        private final int totalRows;

        private TxtTestProcessor(final int totalRows) {
            this.totalRows = totalRows;
        }

        @Override
        public void beforeProcessFile(final String filePath) throws Exception {
        }

        @Override
        public void afterProcessFile(final DataFileContext dataFileContext) throws Exception {
        }

        @Override
        public DataRowContext<List<String>> processRow(final DataRowContext<List<String>> row)
                throws Exception {
            switch (row.getRowNumber()) {
                case 1:
                    Assert.assertEquals(RowType.HEADER, row.getType());
                    break;
                case 2:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final List<String> a1 = row.getRow();
                    assertFirst2Rows(a1);
                    row.getDataFileContext().setAttachment(a1);
                    break;
                case 3:
                    final Object a = row.getDataFileContext().getAttachment();
                    Assert.assertTrue(a instanceof List);

                    Assert.assertEquals(RowType.DATA, row.getType());
                    final List<String> a2 = row.getRow();
                    assertFirst2Rows(a2);
                    break;
                case 4:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final List<String> a3 = row.getRow();
                    Assert.assertNotNull(a3);
                    Assert.assertEquals("false", a3.get(0));
                    Assert.assertEquals("", a3.get(2));
                    Assert.assertEquals("0", a3.get(3));
                    Assert.assertEquals("", a3.get(4));
                    Assert.assertEquals("0.001", a3.get(5));
                    Assert.assertEquals("", a3.get(6));
                    Assert.assertEquals("cdeCDE 098!@#$%^&*()-=_+[]\\{}|;':\"<>?./", a3.get(7));
                    Assert.assertEquals("2000-02-29 23:59:59", a3.get(8));
                    break;
                case 5:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final List<String> a4 = row.getRow();
                    Assert.assertNotNull(a4);
                    Assert.assertEquals("false", a4.get(0));
                    Assert.assertEquals("false", a4.get(2));
                    Assert.assertEquals("+0", a4.get(3));
                    Assert.assertEquals("-0", a4.get(4));
                    Assert.assertEquals("-0.001", a4.get(5));
                    Assert.assertEquals(6, a4.size());
                    break;
                default:
                    if (row.getRowNumber() < 1 && row.getRowNumber() > this.totalRows) {
                        Assert.fail("Row number is expected to be 1 - " + this.totalRows + ": " + row.getRowNumber());
                    }
            }
            return row;
        }

        private void assertFirst2Rows(final List<String> row) {
            Assert.assertNotNull(row);
            Assert.assertTrue(row.size() >= 9);
            Assert.assertEquals("true", row.get(0));
            Assert.assertEquals("true", row.get(2));
            Assert.assertEquals(Integer.toString(Integer.MIN_VALUE), row.get(3));
            Assert.assertEquals(Integer.toString(Integer.MAX_VALUE), row.get(4));
            Assert.assertEquals("12345678.09", row.get(5));
            Assert.assertEquals("-12345678.09", row.get(6));
            Assert.assertEquals("cdeCDE 123!@#$%^&*()-=_+[]\\{}|;':\"<>?./", row.get(7));
            Assert.assertEquals("1970-01-01 00:00:00", row.get(8));
        }
    }

    private static final class CsvTestProcessor extends TxtTestProcessor {

        private CsvTestProcessor(final int totalRows) {
            super(totalRows);
        }

        @Override
        public DataRowContext<List<String>> processRow(final DataRowContext<List<String>> row)
                throws Exception {
            switch (row.getRowNumber()) {
                case 6:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final List<String> a1 = row.getRow();
                    assert6thOr7thRows(a1);
                    row.getDataFileContext().setAttachment(a1);
                    break;
                case 7:
                    final Object a = row.getDataFileContext().getAttachment();
                    Assert.assertTrue(a instanceof List);

                    Assert.assertEquals(RowType.DATA, row.getType());
                    final List<String> a2 = row.getRow();
                    assert6thOr7thRows(a2);
                    break;
                case 8:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final List<String> a3 = row.getRow();
                    Assert.assertNotNull(a3);
                    Assert.assertEquals("false", a3.get(0));
                    Assert.assertEquals("", a3.get(2));
                    Assert.assertEquals("0", a3.get(3));
                    Assert.assertEquals("", a3.get(4));
                    Assert.assertEquals("0.001", a3.get(5));
                    Assert.assertEquals("", a3.get(6));
                    Assert.assertEquals("cdeCDE 098!@#$%^&*()-=_+[]\\{}|;':\"<>?,./", a3.get(7));
                    Assert.assertEquals("1999-12-31 23:59:59", a3.get(8));
                    break;
                case 9:
                    Assert.assertEquals(RowType.DATA, row.getType());
                    final List<String> a4 = row.getRow();
                    Assert.assertNotNull(a4);
                    Assert.assertEquals("false", a4.get(0));
                    Assert.assertEquals("false", a4.get(2));
                    Assert.assertEquals("+0", a4.get(3));
                    Assert.assertEquals("-0", a4.get(4));
                    Assert.assertEquals("-0.001", a4.get(5));
                    Assert.assertEquals(6, a4.size());
                    break;
                default:
                    return super.processRow(row);
            }
            return row;
        }

        private void assert6thOr7thRows(final List<String> row) {
            Assert.assertNotNull(row);
            Assert.assertTrue(row.size() >= 9);
            Assert.assertEquals("true", row.get(0));
            Assert.assertEquals("true", row.get(2));
            Assert.assertEquals(Integer.toString(Integer.MIN_VALUE), row.get(3));
            Assert.assertEquals(Integer.toString(Integer.MAX_VALUE), row.get(4));
            Assert.assertEquals("12345678.09", row.get(5));
            Assert.assertEquals("-12345678.09", row.get(6));
            Assert.assertEquals("cdeCDE 123!@#$%^&*()-=_+[]\\{}|;':\"<>?,./", row.get(7));
            Assert.assertEquals("2020-02-29 12:30:30", row.get(8));
        }
    }
}
