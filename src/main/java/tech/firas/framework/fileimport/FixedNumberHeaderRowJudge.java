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
 * For judging the type of the rows in a data file that has a fixed number of header rows
 * @param <T>  the type of a row (String for a plain text data file, or Row for an Excel file, ...)
 */
public class FixedNumberHeaderRowJudge<T> implements DataRowJudge<T> {

    private int numberOfHeaderRows;

    public int getNumberOfHeaderRows() {
        return numberOfHeaderRows;
    }

    public void setNumberOfHeaderRows(int numberOfHeaderRows) {
        this.numberOfHeaderRows = numberOfHeaderRows;
    }

    @Override
    public RowType test(final int rowNumber, final T row, final RowType previousRowType) {
        return rowNumber <= numberOfHeaderRows ? RowType.HEADER : RowType.DATA;
    }
}
