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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import tech.firas.framework.bean.ObjectType;

/**
 * <p>For converting the plain text data line simply seperated by a pattern (e.g. a comma, a vertical bar, ...).</p>
 *
 * <p>The default pattern is a comma.</p>
 *
 * <p>You can specify the mapping from the index of the column to the field of the Java bean by setting
 *    {@code fieldNames}.</p>
 *
 * @param <T>  the type of the Java object that every data line is to be converted to
 */
public class DefaultStringLineToJavaObjectConverter<T> extends ToJavaObjectConverterBase<T> {

    private static final Logger logger = Logger.getLogger(DefaultStringLineToJavaObjectConverter.class.getName());

    private static final Pattern defaultPattern = Pattern.compile(",");

    /**
     * The pattern used to separate the columns in every data row
     */
    private Pattern seperatorPattern = defaultPattern;


    @SuppressWarnings("unchecked")
    public DefaultStringLineToJavaObjectConverter(final String className)
            throws ClassNotFoundException, NoSuchMethodException {
        this((Class<T>) Class.forName(className));
    }

    private DefaultStringLineToJavaObjectConverter(final Class<T> clazz) throws NoSuchMethodException {
        super(clazz.getConstructor());
    }

    public static <T> DefaultStringLineToJavaObjectConverter<T> ofClass(final Class<T> clazz)
            throws NoSuchMethodException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }
        return new DefaultStringLineToJavaObjectConverter<>(clazz);
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
                    continue;
                }

                final Class<?> targetObjectType = ObjectType.getObjectType(targetType);
                if (this.getConversionService().canConvert(String.class, targetObjectType)) {
                    this.fieldSetters[i].set(result, this.getConversionService().convert(columns[i], targetObjectType));
                }
            }
            return result;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + constructor.getDeclaringClass().getName());
        }
    }
}
