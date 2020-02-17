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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.springframework.core.convert.converter.Converter;

/**
 *
 * @param <R>  the type of a row (String for a plain text data file, or Row for an Excel file, ...)
 * @param <T>  the type of the Java object that every data row is to be converted to
 */
public abstract class DefaultDataFileImporterBase<R, T> implements Callable<ImportContext> {

    private static final Logger logger = Logger.getLogger(DefaultDataFileImporterBase.class.getName());

    private DataFileReader<R> dataFileReader;

    /**
     * The converter used to convert the data row into a Java object
     */
    private Converter<R, T> rowToJavaObjectConverter;

    /**
     * The path of the directory in which this class searches for the files to import
     */
    private String baseDirectory;

    /**
     * The pattern of the name of the files in {@code baseDirectory} that this class import.
     *
     * {@link java.util.regex.Matcher#find} is used instead of {@link java.util.regex.Matcher#matches}.
     */
    private String fileNamePattern;

    @Override
    public ImportContext call() {
        final File baseDirFile = new File(this.baseDirectory);
        if (!baseDirFile.exists() || !baseDirFile.isDirectory()) {
            final ImportContext result = new ImportContext(null);
            result.setMessageForFailure(this.baseDirectory + " is not a directory");
            return result;
        }

        final Pattern pattern = Pattern.compile(this.fileNamePattern);
        final File[] filesToImport = baseDirFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.isFile() && pattern.matcher(file.getName()).find();
            }
        });
        if (null == filesToImport || filesToImport.length <= 0) {
            final ImportContext result = new ImportContext(Collections.unmodifiableMap(new HashMap<String, DataFileContext>()));
            result.setMessageForSuccess("No file need to be imported");
            return result;
        }

        try {
            this.beforeAllImport(Collections.unmodifiableList(Arrays.asList(filesToImport)));
            final Map<String, DataFileContext> dataFileContextMap = new HashMap<>();
            for (final File file : filesToImport) {
                final String filePath = file.getCanonicalPath();
                final DataFileContext dataFileContext = this.importOneFile(filePath);
                dataFileContextMap.put(filePath, dataFileContext);
            }
            this.afterAllImport(dataFileContextMap);

            final ImportContext result = new ImportContext(dataFileContextMap);
            result.setMessageForSuccess(dataFileContextMap.size() + " file(s) imported");
            return result;
        } catch (Exception ex) {
            final ImportContext result = new ImportContext(null);
            result.setMessageForFailure(ex.getMessage());
            return result;
        }
    }

    public DataFileReader<R> getDataFileReader() {
        return dataFileReader;
    }

    public void setDataFileReader(final DataFileReader<R> dataFileReader) {
        this.dataFileReader = dataFileReader;
    }

    public Converter<R, T> getRowToJavaObjectConverter() {
        return this.rowToJavaObjectConverter;
    }

    public void setRowToJavaObjectConverter(final Converter<R, T> converter) {
        if (null == converter) {
            throw new IllegalArgumentException("rowToJavaObjectConverter must not be null");
        }
        this.rowToJavaObjectConverter = converter;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(final String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public String getFileNamePattern() {
        return fileNamePattern;
    }

    public void setFileNamePattern(final String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    protected void beforeAllImport(final List<File> filesToImport) {
        logger.finer("Going to import " + filesToImport.size() + " files in " + this.baseDirectory);
    }

    protected DataFileContext importOneFile(final String filePath) throws IOException {
        this.beforeOneImport(filePath);

        final DataFileContext dataFileContext = new DataFileContext();
        this.processOneFile(filePath, dataFileContext, this.dataFileReader.readDataFile(filePath));

        this.afterOneFileProcessed(filePath, dataFileContext);
        return dataFileContext;
    }

    /**
     * You can do something to prevent importing the smae file repeatedly,
     * save the file information into DB, or output some logs here.
     *
     * @param filePath  the canonical file path of the file to be imported
     */
    protected void beforeOneImport(final String filePath) {
        logger.finer("Going to import " + filePath);
    }

    protected void processOneFile(final String filePath, final DataFileContext dataFileContext,
            final Iterator<DataRowContext<R>> rowIterator) {
        logger.finer(filePath + " is opened for import");
        while (rowIterator.hasNext()) {
            final DataRowContext<R> dataRowContext = rowIterator.next();
            switch (dataRowContext.getType()) {
                case DATA:
                    dataFileContext.setDataRowCount(dataFileContext.getDataRowCount() + 1);
                    break;
                case HEADER:
                    dataFileContext.setHeaderRowCount(dataFileContext.getHeaderRowCount() + 1);
                    break;
                case FOOTER:
                    dataFileContext.setFooterRowCount(dataFileContext.getFooterRowCount() + 1);
                    break;
            }
            processOneRow(filePath, dataFileContext, dataRowContext);
        }
    }

    /**
     * You can save every data row read from the data file here.
     * @param filePath  the canonical file path of the file to be imported
     * @param dataFileContext  the information of the imported data file
     * @param row  the row information
     */
    protected abstract void processOneRow(final String filePath, final DataFileContext dataFileContext,
            DataRowContext<R> row);

    /**
     * You can archive / delete the imported file, commit the DB transaction,
     * save the import status of the file, or output some logs here.
     *
     * @param filePath  the canonical file path of the file to be imported
     * @param dataFileContext  the information of the imported data file
     */
    protected void afterOneFileProcessed(final String filePath, final DataFileContext dataFileContext) {
        logger.finer("Imported " + dataFileContext.getDataRowCount() + " row(s) of data in " + filePath +
                ", header row count: " + dataFileContext.getHeaderRowCount() +
                ", footer row count: " + dataFileContext.getFooterRowCount());
    }

    protected void afterAllImport(final Map<String, DataFileContext> dataFileContextMap) {
        logger.finer(dataFileContextMap.size() + " files in " + this.baseDirectory + " are imported");
    }
}
