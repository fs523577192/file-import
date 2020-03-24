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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Reads and processes a set of data files that are in the same directory and should be processed
 * by the same DataFileReader and the same DataFileProcessors
 *
 * @param <R>  the type of a row (String for a plain text data file, or Row for an Excel file, ...)
 */
public class DefaultDataFileImporterBase<R> implements Callable<ImportContext> {

    private static final Logger logger = Logger.getLogger(DefaultDataFileImporterBase.class.getName());

    private AbstractDataFileReader<R> dataFileReader;

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

    public AbstractDataFileReader<R> getDataFileReader() {
        return dataFileReader;
    }

    public void setDataFileReader(final AbstractDataFileReader<R> dataFileReader) {
        this.dataFileReader = dataFileReader;
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

    protected void beforeAllImport(final List<File> filesToImport) throws Exception {
        logger.finer("Going to import " + filesToImport.size() + " files in " + this.baseDirectory);
    }

    protected DataFileContext importOneFile(final String filePath) throws Exception {
        return this.dataFileReader.readDataFile(filePath, null);
    }

    protected void afterAllImport(final Map<String, DataFileContext> dataFileContextMap) throws Exception {
        logger.finer(dataFileContextMap.size() + " files in " + this.baseDirectory + " are imported");
    }
}
