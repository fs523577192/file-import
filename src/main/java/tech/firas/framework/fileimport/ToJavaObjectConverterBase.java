package tech.firas.framework.fileimport;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import tech.firas.framework.bean.FieldSetter;

public abstract class ToJavaObjectConverterBase<T> implements Converter<String, T> {

    private static final Logger logger = Logger.getLogger(ToJavaObjectConverterBase.class.getName());

    private static final List<String> emptyFieldNames = Arrays.asList(new String[0]);
    private static final FieldSetter.Configuration setterConf = new FieldSetter.Configuration();

    static {
        setterConf.setAllowDirectlySetField(true);
    }

    private static final ConversionService defaultConversionService = new DefaultConversionService();


    protected final Constructor<T> constructor;

    @SuppressWarnings("unchecked")
    protected FieldSetter<T>[] fieldSetters = new FieldSetter[0];

    /**
     * A list of field names
     *
     * This maps the column index to the field of the Java bean
     */
    private List<String> fieldNames = emptyFieldNames;

    /**
     * For converting the String columns in the data file to the fields of the Java bean
     *
     * The fields of the Java bean may be of various type, e.g. int, boolean, double, BigInteger, BigDecimal, ...
     */
    private ConversionService conversionService = defaultConversionService;


    protected ToJavaObjectConverterBase(final Constructor<T> constructor) {
        this.constructor = constructor;
    }


    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(final List<String> fieldNames) {
        this.fieldNames = fieldNames;
        setFieldSettersByFieldNames(fieldNames);
    }

    public ConversionService getConversionService() {
        return conversionService;
    }

    public void setConversionService(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @SuppressWarnings("unchecked")
    private void setFieldSettersByFieldNames(final List<String> fieldNames) {
        this.fieldSetters = new FieldSetter[fieldNames.size()];

        int i = 0;
        for (final String fieldName : fieldNames) {
            for (Class<? super T> clazz = constructor.getDeclaringClass(); null != clazz && !Object.class.equals(clazz);
                 clazz = clazz.getSuperclass()) {
                try {
                    final FieldSetter<T> fieldSetter = new FieldSetter<>(clazz, clazz.getDeclaredField(fieldName), setterConf);
                    this.fieldSetters[i] = fieldSetter;
                    break;
                } catch (NoSuchFieldException e) {
                    logger.log(Level.FINEST, clazz.getName() + " has no field named " + fieldName, e);
                } catch (NoSuchMethodException e) {
                    logger.log(Level.FINER, "Fail to create FieldGetter for " + fieldName +
                            " of " + clazz.getName(), e);
                }
            }
            if (logger.isLoggable(Level.FINER) && null == this.fieldSetters[i]) {
                logger.finer("Fail to find the field \"" + fieldName + "\" in " + constructor.getDeclaringClass().getName());
            }
            i += 1;
        }
    }
}
