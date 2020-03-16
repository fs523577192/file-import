package tech.firas.framework.fileimport;

/**
 * For reading every row in a plain text data file
 */
public abstract class PlainTextDataFileReader extends AbstractDataFileReader<String> {

    protected String charset;

    public String getCharset() {
        return charset;
    }

    /**
     * <p>Tell the reader to read the data file with the specific character set</p>
     *
     * <p>If `charset` is null, then read with the operating system's default character set</p>
     *
     * @param charset  the name of the character set used to read the text data file
     */
    public void setCharset(final String charset) {
        this.charset = charset;
    }
}
