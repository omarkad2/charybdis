/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.charybdis.test.entities;

import com.github.charybdis.model.annotation.Udt;
import com.github.charybdis.model.annotation.UdtField;
import java.util.Objects;

@Udt(keyspace = "test_keyspace", name = "test_extra_udt")
public class TestExtraUdt {

  @UdtField
  private Integer intValue;

  @UdtField
  private Double doubleValue;

  public TestExtraUdt() {
  }

  public TestExtraUdt(final Integer intValue, final Double doubleValue) {
    this.intValue = intValue;
    this.doubleValue = doubleValue;
  }

  public Integer getIntValue() {
    return intValue;
  }

  public void setIntValue(final Integer intValue) {
    this.intValue = intValue;
  }

  public Double getDoubleValue() {
    return doubleValue;
  }

  public void setDoubleValue(final Double doubleValue) {
    this.doubleValue = doubleValue;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestExtraUdt)) {
      return false;
    }
    final TestExtraUdt extraUdt = (TestExtraUdt) o;
    return Objects.equals(intValue, extraUdt.intValue) && Objects.equals(doubleValue, extraUdt.doubleValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(intValue, doubleValue);
  }

  @Override
  public String toString() {
    return "TestExtraUdt{" + "intValue=" + intValue + ", doubleValue=" + doubleValue + '}';
  }
}
