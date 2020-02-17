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
 * For judging whether one row in the data file is a row of header, a row of data or a row of footer
 * @param <T>  the type of a row (String for a plain text data file, or Row for an Excel file, ...)
 */
public interface DataRowJudge<T> {

    /**
     * Judges whether one row in the data file is a row of header, a row of data or a row of footer
     * @param rowNumber  the row number of the first row of the data file is 1
     * @param row  the row content corresponding to the row number
     * @param previousRowType  the type of the previous row
     * @return  the type of the input row
     */
    RowType test(int rowNumber, T row, RowType previousRowType);
}
