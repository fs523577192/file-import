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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import tech.firas.framework.fileimport.DataFileContext;
import tech.firas.framework.fileimport.DataRowContext;
import tech.firas.framework.fileimport.FixedNumberHeaderRowJudge;
import tech.firas.framework.fileimport.RowType;
import tech.firas.framework.fileimport.processor.DataFileProcessor;
import tech.firas.framework.fileimport.processor.SetRowTypeProcessor;
import tech.firas.framework.fileimport.test.AbstractTests;

public class SetRowTypeProcessorTests extends AbstractTests {

    private static final Random random = new Random();

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 1000; i += 1) {
            final int n = random.nextInt(10);

            final SetRowTypeProcessor<String> processor = new SetRowTypeProcessor<>();
            processor.setDataRowJudge(new FixedNumberHeaderRowJudge<String>(n));
            processor.setNextProcessor(new TestProcessor(n));

            final DataFileContext context = new DataFileContext("test" + i);
            processor.beforeProcessFile(context.getFilePath());
            for (int j = 0; j < 100; j += 1) {
                processor.processRow(new DataRowContext<>(context, j + 1,
                        getRandomColumnValue(), RowType.UNKNOWN));
            }
            processor.afterProcessFile(context);
        }
    }

    private String getRandomColumnValue() {
        final StringBuilder builder = new StringBuilder();
        for (int i = random.nextInt(10 + 1); i > 0; i -= 1) {
            int c = random.nextInt(127 - (int) ' ') + (int) ' ';
            builder.append((char) c);
        }
        return builder.toString();
    }

    private static class TestProcessor implements DataFileProcessor<String> {

        private int n;

        private TestProcessor(final int n) {
            this.n = n;
        }

        @Override
        public void beforeProcessFile(final String filePath) throws Exception {
        }

        @Override
        public void afterProcessFile(final DataFileContext dataFileContext) throws Exception {
        }

        @Override
        public DataRowContext<String> processRow(final DataRowContext<String> dataRowContext) throws Exception {
            Assert.assertEquals(dataRowContext.getRowNumber() > n ? RowType.DATA : RowType.HEADER,
                    dataRowContext.getType());
            return dataRowContext;
        }
    }
}
