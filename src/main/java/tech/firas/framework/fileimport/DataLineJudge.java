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

/**
 * For judging whether one line in the data file is a line of header, a line of data or a line of footer
 * @param <T>  the type of a line (String for a plain text data file, or Row for an Excel file, ...)
 */
public interface DataLineJudge<T> {

    /**
     * Judges whether one line in the data file is a line of header, a line of data or a line of footer
     * @param lineNumber  the line number of the first line of the data file is 1
     * @param line  the line content corresponding to the line number
     * @param previousLineType  the type of the previous line
     * @return  the type of the input line
     */
    LineType test(int lineNumber, T line, LineType previousLineType);
}
