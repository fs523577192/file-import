package tech.firas.framework.fileimport.processor;

import java.util.List;

import org.springframework.core.convert.converter.Converter;

import tech.firas.framework.fileimport.DataRowContext;

public class StringListToJavaBeanProcessor<T> extends AbstractChainedFileProcessor<List<String>, T> {

    private Converter<List<String>, T> stringListToJavaBeanConverter;

    public Converter<List<String>, T> getStringListToJavaBeanConverter() {
        return stringListToJavaBeanConverter;
    }

    public void setStringListToJavaBeanConverter(final Converter<List<String>, T> stringListToJavaBeanConverter) {
        this.stringListToJavaBeanConverter = stringListToJavaBeanConverter;
    }

    @Override
    public DataRowContext<List<String>> processRow(final DataRowContext<List<String>> dataRowContext) throws Exception {
        if (null == this.stringListToJavaBeanConverter) {
            throw new IllegalStateException("stringListToJavaBeanConverter is null");
        }
        ensureNextProcessNotNull();

        final T obj = this.stringListToJavaBeanConverter.convert(dataRowContext.getRow());

        final DataRowContext<T> newContext = new DataRowContext<>(dataRowContext.getDataFileContext(),
                dataRowContext.getRowNumber(), obj, dataRowContext.getType());
        this.getNextProcessor().processRow(newContext);
        return dataRowContext;
    }
}
