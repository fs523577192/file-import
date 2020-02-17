package tech.firas.framework.fileimport;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import tech.firas.framework.bean.ObjectType;

/**
 * <p>For CSV (Comma Seperated Values) data files</p>
 *
 * <ul>
 *     <li>The columns in a row in a CSV file is seperated by commas.</li>
 *     <li>If the column value contains one or more commas, the column is quoted by double quotes (").</li>
 *     <li>If the column is quoted by double quotes and the column value contains one or more double quotes,
 *         the double quotes are escaped to double double quotes (" =&gt; "").</li>
 * </ul>
 * <p><b>Notice: </b>to avoid complicated logic, if a column begins with a double quote, this converter
 *    consider the column as quoted. And if this converter find no ending double quote for a quoted column,
 *    it throws an IllegalArgumentException.</p>
 *
 * <p>You can specify the mapping from the index of the column to the field of the Java bean by setting
 *    {@code fieldNames}.</p>
 *
 * @param <T>  the type of the Java object that every data row is to be converted to
 */
public class CsvRowToJavaObjectConverter<T> extends ToJavaObjectConverterBase<T> {

    private static final Logger logger = Logger.getLogger(CsvRowToJavaObjectConverter.class.getName());


    @SuppressWarnings("unchecked")
    public CsvRowToJavaObjectConverter(final String className)
            throws ClassNotFoundException, NoSuchMethodException {
        this((Class<T>) Class.forName(className));
    }

    private CsvRowToJavaObjectConverter(final Class<T> clazz) throws NoSuchMethodException {
        super(clazz.getConstructor());
    }

    public static <T> CsvRowToJavaObjectConverter<T> ofClass(final Class<T> clazz) throws NoSuchMethodException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }
        return new CsvRowToJavaObjectConverter<>(clazz);
    }

    @Override
    public T convert(final String source) {
        final int rowLength = source.length();
        try {
            final T result = this.constructor.newInstance();

            int columnIndex = -1;

            State state = State.FIELD_START;
            int i = 0; // index of characters in source
            while (i < rowLength) {
                switch (state) {
                    case FIELD_START:
                        if (source.charAt(i) == '"') {
                            // to avoid complicated logic, if a column begins with a double quote,
                            // consider the column as quoted
                            i += 1;
                            state = State.IN_QUOTE;
                        } else {
                            final int nextComma = source.indexOf(',', i);
                            columnIndex += 1;
                            if (columnIndex >= this.fieldSetters.length) {
                                return result;
                            }

                            if (nextComma < 0) { // no comma anymore, row end
                                setField(result, columnIndex, source.substring(i));
                                return result;
                            } else {
                                setField(result, columnIndex, source.substring(i, nextComma));
                                i = nextComma + 1;
                            }
                        }
                        break;
                    case IN_QUOTE:
                        int start = i;
                        while (true) {
                            final int nextQuote = source.indexOf('"', start);
                            if (nextQuote < 0) { // no quote anymore
                                throw new IllegalArgumentException("The beginning quote at index " +
                                        i + " does not match an end");
                            }
                            final int nextDoubleQuote = source.indexOf("\"\"", start);
                            if (nextQuote == nextDoubleQuote) { // meet '""'
                                start = nextQuote + 2;
                            } else { // column end
                                columnIndex += 1;
                                if (columnIndex >= this.fieldSetters.length) {
                                    return result;
                                }
                                setField(result, columnIndex, source.substring(i, nextQuote).replace("\"\"", "\""));
                                i = nextQuote + 1;
                                state = State.FIELD_END;
                                break;
                            }
                        }
                        break;
                    default: // FIELD_END
                        if (source.charAt(i) != ',') {
                            throw new IllegalArgumentException("Expect a comma at index " + i +
                                    " but is is '" + source.charAt(i) + '\'');
                        } else {
                            i += 1;
                            state = State.FIELD_START;
                        }
                }
            }
            return result;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + constructor.getDeclaringClass().getName());
        }
    }

    private void setField(final T result, final int columnIndex, final String column)
            throws InvocationTargetException, IllegalAccessException {
        if (null == this.fieldSetters[columnIndex]) {
            logger.finer("The " + (columnIndex + 1) + " column corresponds to no FieldSetter");
            return;
        }
        final Class<?> targetType = this.fieldSetters[columnIndex].getParameterType();
        if (String.class.equals(targetType)) {
            this.fieldSetters[columnIndex].set(result, column);
            return;
        }

        final Class<?> targetObjectType = ObjectType.getObjectType(targetType);
        if (this.getConversionService().canConvert(String.class, targetObjectType)) {
            this.fieldSetters[columnIndex].set(result, this.getConversionService().convert(column, targetObjectType));
        }
    }

    private enum State {
        FIELD_START,
        IN_QUOTE,
        FIELD_END
    }
}
