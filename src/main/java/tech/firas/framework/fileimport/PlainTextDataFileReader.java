package tech.firas.framework.fileimport;

/**
 * For reading every row in a plain text data file
 */
public interface PlainTextDataFileReader extends DataFileReader<String> {

    /**
     * <p>Tell the reader to read the data file with the specific character set</p>
     *
     * <p>If `charset` is null, then read with the operating system's default character set</p>
     *
     * @param charset  the name of the character set used to read the text data file
     */
    void setCharset(String charset);
}
