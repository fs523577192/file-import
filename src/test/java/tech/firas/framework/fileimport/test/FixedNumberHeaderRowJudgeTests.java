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
package tech.firas.framework.fileimport.test;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import tech.firas.framework.fileimport.DataRowJudge;
import tech.firas.framework.fileimport.FixedNumberHeaderRowJudge;
import tech.firas.framework.fileimport.RowType;

public class FixedNumberHeaderRowJudgeTests extends AbstractTests {

    private static final Random random = new Random();

    @Test
    public void test() {
        for (int i = 0; i < 1000; i += 1) {
            final int n = random.nextInt(10);
            final FixedNumberHeaderRowJudge<String> judge = new FixedNumberHeaderRowJudge<>();
            judge.setNumberOfHeaderRows(n);
            testAssertion(judge, n);
        }
    }

    private void testAssertion(final DataRowJudge<String> judge, final int n) {
        for (int j = 1; j <= n; j += 1) {
            Assert.assertEquals(RowType.HEADER, judge.test(j, "", j == 1 ? null : RowType.HEADER));
        }
        for (int j = n + 1; j < 100; j += 1) {
            Assert.assertEquals(RowType.DATA, judge.test(j, "", j == n + 1 ? RowType.HEADER : RowType.DATA));
        }
    }
}
