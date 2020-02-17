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
package tech.firas.framework.fileimport;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DefaultPlainTextDataFileReader implements PlainTextDataFileReader {

    private String charset;

    private DataRowJudge<String> dataRowJudge;

    @Override
    public void setCharset(final String charset) {
        this.charset = charset;
    }

    @Override
    public void setDataRowJudge(final DataRowJudge<String> dataRowJudge) {
        this.dataRowJudge = dataRowJudge;
    }

    public String getCharset() {
        return this.charset;
    }

    public DataRowJudge getDataRowJudge() {
        return this.dataRowJudge;
    }

    @Override
    public Iterator<DataRowContext<String>> readDataFile(final String filePath) throws IOException {
        final Scanner scanner = (null == this.charset) ?
                new Scanner(new FileInputStream(filePath)) :
                new Scanner(new FileInputStream(filePath), this.charset);
        return new MyIterator(scanner, this.dataRowJudge);
    }

    private static class MyIterator implements Iterator<DataRowContext<String>> {

        private final Scanner scanner;
        private final DataRowJudge<String> dataRowJudge;

        private RowType previousRowType;
        private int rowNumber = 0;
        private boolean scannerClosed = false;

        private MyIterator(final Scanner scanner, final DataRowJudge<String> dataRowJudge) {
            if (null == dataRowJudge) {
                throw new IllegalArgumentException("dataRowJudge must not be null");
            }

            this.scanner = scanner;
            this.dataRowJudge = dataRowJudge;
        }

        @Override
        public boolean hasNext() {
            if (this.scannerClosed) {
                return false;
            }

            if (!this.scanner.hasNextLine()) {
                this.scanner.close();
                this.scannerClosed = true;
                return false;
            } else {
                return true;
            }
        }

        @Override
        public DataRowContext<String> next() {
            if (this.scannerClosed) {
                throw new NoSuchElementException();
            }
            final String row = this.scanner.nextLine();
            this.rowNumber += 1;
            final RowType currentRowType = this.dataRowJudge.test(this.rowNumber, row, this.previousRowType);
            this.previousRowType = currentRowType;
            return new DataRowContext<>(this.rowNumber, row, currentRowType);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
