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

/**
 * For reading every row in a data file
 * @param <R>  the type of a row (String for a plain text data file, or Row for an Excel file, ...)
 */
public interface DataFileReader<R> {

    /**
     * Set the DataRowJudge for this reader
     * @param dataRowJudge  the DataRowJudge for this reader
     */
    void setDataRowJudge(DataRowJudge<R> dataRowJudge);

    /**
     * Read the data (ignoring headers and footers) from a data file
     * @param filePath  the path of the data file
     * @return  an Iterator of every row in the data file
     * @throws IOException  if fails to read the rows in the data file
     */
    Iterator<DataRowContext<R>> readDataFile(String filePath) throws IOException;
}
