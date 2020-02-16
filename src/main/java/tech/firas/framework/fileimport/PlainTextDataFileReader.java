package tech.firas.framework.fileimport;

/**
 * For reading the data in a plain text data file
 * @param <T>  the type of the Java object that every data line is to be converted to
 */
public interface PlainTextDataFileReader<T> extends DataFileReader<T, String> {

    /**
     * <p>Tell the reader to read the data file with the specific character set</p>
     *
     * <p>If `charset` is null, then read with the operating system's default character set</p>
     *
     * @param charset  the name of the character set used to read the text data file
     */
    void setCharset(String charset);
}
