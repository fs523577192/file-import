package tech.firas.framework.fileimport.processor;

import tech.firas.framework.fileimport.DataFileContext;

/**
 * The processor that will pass the row to the next DataFileProcessor
 * @param <R>  the type of the input row
 * @param <T>  the type of the row to pass to the next DataFileProcessor
 */
public abstract class AbstractChainedFileProcessor<R, T> implements DataFileProcessor<R> {

    private DataFileProcessor<T> nextProcessor;

    public DataFileProcessor<T> getNextProcessor() {
        return nextProcessor;
    }

    public void setNextProcessor(final DataFileProcessor<T> nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    /**
     * By default, call the next processor's beforeProcessFile method
     * @param filePath  the canonical file path of the file to be imported
     * @throws Exception  if the next processor's method throws an Exception
     */
    @Override
    public void beforeProcessFile(final String filePath) throws Exception {
        this.nextProcessor.beforeProcessFile(filePath);
    }

    /**
     * By default, call the next processor's afterProcessFile method
     * @param dataFileContext  the information of the imported data file
     * @throws Exception  if the next processor's method throws an Exception
     */
    @Override
    public void afterProcessFile(final DataFileContext dataFileContext) throws Exception {
        this.nextProcessor.afterProcessFile(dataFileContext);
    }

    protected void ensureNextProcessNotNull() {
        if (null == this.nextProcessor) {
            throw new IllegalStateException("nextProcessor is null");
        }
    }
}
