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

import java.util.logging.Logger;

import tech.firas.framework.fileimport.processor.DataFileProcessor;
import tech.firas.framework.fileimport.util.CloseableIterator;

/**
 * For reading every row in a data file and pass the row to a DataFileProcessor
 *
 * @param <R> the type of a row (String for a plain text data file, or Row for an Excel file, ...)
 */
public abstract class AbstractDataFileReader<R> {

    private static final Logger logger = Logger.getLogger(AbstractDataFileReader.class.getName());

    private DataFileProcessor<R> dataFileProcessor;

    /**
     * Read the data from a data file
     *
     * @param filePath    the path of the data file
     * @param parameters  for example the sheet name of an Excel
     * @return  a DataFileContext containing the information about the file
     * @throws Exception  if an error occurs when processing the file
     */
    public DataFileContext readDataFile(final String filePath, final Object parameters) throws Exception {
        if (null == this.dataFileProcessor) {
            throw new IllegalStateException("dataFileProcessor not set");
        }

        try (CloseableIterator<R> iterator = this.getRowIterator(filePath, parameters)) {
            final DataFileContext dataFileContext = new DataFileContext(filePath);

            dataFileProcessor.beforeProcessFile(filePath);
            int rowNumber = 0;
            while (iterator.hasNext()) {
                rowNumber += 1;
                this.dataFileProcessor.processRow(new DataRowContext<>(dataFileContext, rowNumber,
                        iterator.next(), RowType.UNKNOWN));
            }
            logger.finer("Processed " + dataFileContext.getDataRowCount() + " row(s) of data in " + filePath +
                    ", header row count: " + dataFileContext.getHeaderRowCount() +
                    ", footer row count: " + dataFileContext.getFooterRowCount());
            this.dataFileProcessor.afterProcessFile(dataFileContext);
            return dataFileContext;
        }
    }

    protected abstract CloseableIterator<R> getRowIterator(
            final String filePath, final Object parameters) throws Exception;

    public DataFileProcessor<R> getDataFileProcessor() {
        return dataFileProcessor;
    }

    public void setDataFileProcessor(DataFileProcessor<R> dataFileProcessor) {
        this.dataFileProcessor = dataFileProcessor;
    }
}
