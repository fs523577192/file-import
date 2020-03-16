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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

import tech.firas.framework.fileimport.processor.DataFileProcessor;
import tech.firas.framework.fileimport.util.CloseableIterator;

public class DefaultPlainTextDataFileReader extends PlainTextDataFileReader {

    private static final Logger logger = Logger.getLogger(DefaultPlainTextDataFileReader.class.getName());

    private DataFileProcessor dataFileProcessor;

    @Override
    protected CloseableIterator<String> getRowIterator(
            final String filePath, final Object parameters) throws IOException {
        logger.finer("Going to open " + filePath);
        final Scanner scanner = (null == this.charset) ?
                new Scanner(Paths.get(filePath)) :
                new Scanner(Paths.get(filePath), this.charset);
        return new MyIterator(filePath, scanner);
    }

    private static class MyIterator implements CloseableIterator<String> {

        private final String filePath;
        private final Scanner scanner;

        private boolean scannerClosed = false;

        private MyIterator(final String filePath, final Scanner scanner) {
            this.filePath = filePath;
            this.scanner = scanner;
            logger.finer(filePath + " opened");
        }

        @Override
        public boolean hasNext() {
            if (this.scannerClosed) {
                return false;
            }

            if (!this.scanner.hasNextLine()) {
                this.close();
                return false;
            } else {
                return true;
            }
        }

        @Override
        public String next() {
            if (this.scannerClosed) {
                throw new NoSuchElementException();
            }
            return this.scanner.nextLine();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            this.scanner.close();
            this.scannerClosed = true;
            logger.finer("Scanner for " + this.filePath + " closed");
        }
    }
}
