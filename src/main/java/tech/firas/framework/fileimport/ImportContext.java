package tech.firas.framework.fileimport;

import java.util.Map;

public class ImportContext {

    /**
     * Key is the name / path of imported data file
     */
    private Map<String, DataFileContext> dataFileContextMap;

    private boolean successful;

    private String message;


    public ImportContext(final Map<String, DataFileContext> dataFileContextMap) {
        this.dataFileContextMap = dataFileContextMap;
    }


    public Map<String, DataFileContext> getDataFileContextMap() {
        return dataFileContextMap;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessageForSuccess(final String message) {
        this.successful = true;
        this.message = message;
    }

    public void setMessageForFailure(final String message) {
        this.successful = false;
        this.message = message;
    }
}
