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
import java.util.Iterator;

import org.springframework.core.convert.converter.Converter;

/**
 * For reading the data in a data file
 * @param <T>  the type of the Java object that every data line is to be converted to
 * @param <R>  the type of a line (String for a plain text data file, or Row for an Excel file, ...)
 */
public interface DataFileReader<T, R> {

    /**
     * Set the DataLineJudge for this reader
     * @param dataLineJudge  the DataLineJudge for this reader
     */
    void setDataLineJudge(DataLineJudge<R> dataLineJudge);

    /**
     * Set the converter used to convert the data row into a Java object
     * @param converter  the converter used to convert the data row into a Java object
     */
    void setLineToJavaObjectConverter(Converter<R, T> converter);

    /**
     * Read the data (ignoring headers and footers) from a data file
     * @param filePath  the path of the data file
     * @return  an Iterator of every data item in the data file
     * @throws IOException  if fails to read the data line
     */
    Iterator<T> readDataFile(String filePath) throws IOException;
}
