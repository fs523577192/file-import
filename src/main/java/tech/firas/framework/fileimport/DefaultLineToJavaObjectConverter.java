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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import tech.firas.framework.bean.FieldSetter;
import tech.firas.framework.bean.ObjectType;

public class DefaultLineToJavaObjectConverter<T> implements Converter<String, T> {

    private static final Logger logger = Logger.getLogger(DefaultLineToJavaObjectConverter.class.getName());

    private static final Pattern defaultPattern = Pattern.compile(",");
    private static final List<String> emptyFieldNames = Arrays.asList(new String[0]);
    private static final FieldSetter.Configuration setterConf = new FieldSetter.Configuration();

    static {
        setterConf.setAllowDirectlySetField(true);
    }

    private static final ConversionService defaultConversionService = new DefaultConversionService();


    private final Constructor<T> constructor;

    @SuppressWarnings("unchecked")
    private FieldSetter<T>[] fieldSetters = new FieldSetter[0];

    /**
     * The pattern used to separate the columns in every data row
     */
    private Pattern seperatorPattern = defaultPattern;

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


    @SuppressWarnings("unchecked")
    public DefaultLineToJavaObjectConverter(final String className)
            throws ClassNotFoundException, NoSuchMethodException {
        this((Class<T>) Class.forName(className));
    }

    private DefaultLineToJavaObjectConverter(final Class<T> clazz) throws NoSuchMethodException {
        this.constructor = clazz.getConstructor();
    }

    public static <T> DefaultLineToJavaObjectConverter<T> ofClass(final Class<T> clazz) throws NoSuchMethodException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }
        return new DefaultLineToJavaObjectConverter<T>(clazz);
    }


    public Pattern getSeperatorPattern() {
        return seperatorPattern;
    }

    public void setSeperatorPattern(final String seperatorPattern) {
        setSeperatorPattern(Pattern.compile(seperatorPattern));
    }
    public void setSeperatorPattern(final Pattern seperatorPattern) {
        this.seperatorPattern = seperatorPattern;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(final List<String> fieldNames) {
        this.fieldNames = fieldNames;
        setFieldSettersByFieldNames(fieldNames);
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

    public ConversionService getConversionService() {
        return conversionService;
    }

    public void setConversionService(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public T convert(final String s) {
        final String[] columns = this.seperatorPattern.split(s, -1);
        try {
            final T result = constructor.newInstance();

            for (int i = 0; i < columns.length && i < this.fieldSetters.length; i += 1) {
                if (null == this.fieldSetters[i]) {
                    logger.finer("The " + (i + 1) + " column corresponds to no FieldSetter");
                    continue;
                }
                final Class<?> targetType = this.fieldSetters[i].getParameterType();
                if (String.class.equals(targetType)) {
                    this.fieldSetters[i].set(result, columns[i]);
                }

                final Class<?> targetObjectType = ObjectType.getObjectType(targetType);
                if (this.conversionService.canConvert(String.class, targetObjectType)) {
                    this.fieldSetters[i].set(result, this.conversionService.convert(columns[i], targetObjectType));
                }
            }
            return result;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + constructor.getDeclaringClass().getName());
        }
    }
}
