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

import java.util.Objects;

public class BeanForTest {

    private boolean aaAa;
    private Boolean ccCc;
    private int ddDd;
    private Integer eeEe;
    private double ffFf;
    private Double ggGg;
    private String hhHh;

    public boolean isAaAa() {
        return aaAa;
    }

    public void setAaAa(final boolean aaAa) {
        this.aaAa = aaAa;
    }

    public Boolean getCcCc() {
        return ccCc;
    }

    public void setCcCc(final Boolean ccCc) {
        this.ccCc = ccCc;
    }

    public int getDdDd() {
        return ddDd;
    }

    public void setDdDd(final int ddDd) {
        this.ddDd = ddDd;
    }

    public Integer getEeEe() {
        return eeEe;
    }

    public void setEeEe(final Integer eeEe) {
        this.eeEe = eeEe;
    }

    public double getFfFf() {
        return ffFf;
    }

    public void setFfFf(final double ffFf) {
        this.ffFf = ffFf;
    }

    public Double getGgGg() {
        return ggGg;
    }

    public void setGgGg(final Double ggGg) {
        this.ggGg = ggGg;
    }

    public String getHhHh() {
        return hhHh;
    }

    public void setHhHh(final String hhHh) {
        this.hhHh = hhHh;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeanForTest a = (BeanForTest) o;
        return aaAa == a.aaAa &&
                ddDd == a.ddDd &&
                Double.compare(a.ffFf, ffFf) == 0 &&
                Objects.equals(ccCc, a.ccCc) &&
                Objects.equals(eeEe, a.eeEe) &&
                Objects.equals(ggGg, a.ggGg) &&
                Objects.equals(hhHh, a.hhHh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aaAa, ccCc, ddDd, eeEe, ffFf, ggGg, hhHh);
    }

    @Override
    public String toString() {
        return "BeanForTest{" +
                "aaAa=" + aaAa +
                ", ccCc=" + ccCc +
                ", ddDd=" + ddDd +
                ", eeEe=" + eeEe +
                ", ffFf=" + ffFf +
                ", ggGg=" + ggGg +
                ", hhHh='" + hhHh + '\'' +
                '}';
    }
}
