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

import org.springframework.core.convert.converter.Converter;

public class DefaultPlainTextDataFileReader<T> implements PlainTextDataFileReader<T> {

    private String charset;

    private DataLineJudge<String> dataLineJudge;

    private Converter<String, T> lineToJavaObjectConverter;

    @Override
    public void setCharset(final String charset) {
        this.charset = charset;
    }

    @Override
    public void setDataLineJudge(final DataLineJudge<String> dataLineJudge) {
        this.dataLineJudge = dataLineJudge;
    }

    @Override
    public void setLineToJavaObjectConverter(final Converter<String, T> converter) {
        this.lineToJavaObjectConverter = converter;
    }

    public String getCharset() {
        return this.charset;
    }

    public DataLineJudge getDataLineJudge() {
        return this.dataLineJudge;
    }

    public Converter<String, T> getLineToJavaObjectConverter() {
        return this.lineToJavaObjectConverter;
    }

    @Override
    public Iterator<T> readDataFile(final String filePath) throws IOException {
        final Scanner scanner = (null == this.charset) ?
                new Scanner(new FileInputStream(filePath)) :
                new Scanner(new FileInputStream(filePath), this.charset);
        return new MyIterator<>(scanner, this.dataLineJudge, this.lineToJavaObjectConverter);
    }

    private static class MyIterator<T> implements Iterator<T> {

        private final Scanner scanner;
        private final DataLineJudge<String> dataLineJudge;
        private final Converter<String, T> converter;

        private LineType previousLineType;
        private int lineNumber = 0;
        private String currentLine;
        private boolean hasReadCurrent = true;
        private boolean scannerClosed = false;

        private MyIterator(final Scanner scanner, final DataLineJudge<String> dataLineJudge,
                final Converter<String, T> converter) {
            if (null == dataLineJudge) {
                throw new IllegalArgumentException("dataLineJudge must not be null");
            }
            if (null == converter) {
                throw new IllegalArgumentException("lineToJavaObjectConverter must not be null");
            }

            this.scanner = scanner;
            this.dataLineJudge = dataLineJudge;
            this.converter = converter;
        }

        @Override
        public boolean hasNext() {
            if (!this.hasReadCurrent) {
                return true;
            }
            if (this.scannerClosed) {
                return false;
            }

            while (true) {
                if (!this.scanner.hasNextLine()) {
                    this.scanner.close();
                    this.scannerClosed = true;
                    return false;
                }

                final String line = this.scanner.nextLine();
                this.lineNumber += 1;
                final LineType lineType = this.dataLineJudge.test(this.lineNumber, line, this.previousLineType);
                this.previousLineType = lineType;
                if (LineType.DATA.equals(lineType)) {
                    this.currentLine = line;
                    this.hasReadCurrent = false;
                    break;
                }
            }
            return true;
        }

        @Override
        public T next() {
            if (this.hasReadCurrent) {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("There is no next data");
                }
            }
            this.hasReadCurrent = true;
            return this.converter.convert(this.currentLine);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
