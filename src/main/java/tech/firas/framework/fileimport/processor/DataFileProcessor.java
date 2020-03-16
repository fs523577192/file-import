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
package tech.firas.framework.fileimport.processor;

import tech.firas.framework.fileimport.DataFileContext;
import tech.firas.framework.fileimport.DataRowContext;

public interface DataFileProcessor<R> {

    /**
     * You can do something like preventing importing the same file repeatedly,
     * saving the file information into DB, beginning a transaction, or outputting some logs here.
     *
     * @param filePath  the canonical file path of the file to be imported
     * @throws Exception  if an error occurs
     */
    void beforeProcessFile(String filePath) throws Exception;

    /**
     * You can do something like archiving / deleting the imported file, committing the DB transaction,
     * saving the import status of the file, or outputting some logs here.
     *
     * @param dataFileContext  the information of the imported data file
     * @throws Exception  if an error occurs
     */
    void afterProcessFile(DataFileContext dataFileContext) throws Exception;

    /**
     * You can save every data row read from the data file here.
     * @param dataRowContext  the information of the data row to process
     * @return  a DataRowContext containing the information about the row
     * @throws Exception  if an error occurs when processing the row
     */
    DataRowContext<R> processRow(DataRowContext<R> dataRowContext) throws Exception;
}
