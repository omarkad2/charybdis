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

import com.github.charybdis.model.annotation.Frozen;
import com.github.charybdis.model.annotation.Udt;
import com.github.charybdis.model.annotation.UdtField;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Udt(keyspace = "test_keyspace", name = "test_udt")
public class TestUdt {

  @UdtField
  private @Frozen TestNestedUdt udtNested;

  @UdtField
  private int number;

  @UdtField
  private String value;

  @UdtField
  private List<@Frozen TestNestedUdt> udtNestedList;

  @UdtField
  private Set<@Frozen List<TestNestedUdt>> udtNestedNestedSet;

  @UdtField
  private Map<TestEnum, @Frozen List<TestNestedUdt>> udtNestedMap;



  public TestUdt() {
  }

  public TestUdt(final int number, final String value, final List<@Frozen TestNestedUdt> udtNestedList,
      final Set<@Frozen List<TestNestedUdt>> udtNestedNestedSet, final Map<TestEnum, @Frozen List<TestNestedUdt>> udtNestedMap,
      final TestNestedUdt udtNested) {
    this.number = number;
    this.value = value;
    this.udtNestedList = udtNestedList;
    this.udtNestedNestedSet = udtNestedNestedSet;
    this.udtNestedMap = udtNestedMap;
    this.udtNested = udtNested;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(final int number) {
    this.number = number;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public List<TestNestedUdt> getUdtNestedList() {
    return udtNestedList;
  }

  public void setUdtNestedList(final List<TestNestedUdt> udtNestedList) {
    this.udtNestedList = udtNestedList;
  }

  public Set<List<TestNestedUdt>> getUdtNestedNestedSet() {
    return udtNestedNestedSet;
  }

  public void setUdtNestedNestedSet(final Set<List<TestNestedUdt>> udtNestedNestedSet) {
    this.udtNestedNestedSet = udtNestedNestedSet;
  }

  public Map<TestEnum, List<TestNestedUdt>> getUdtNestedMap() {
    return udtNestedMap;
  }

  public void setUdtNestedMap(final Map<TestEnum, List<TestNestedUdt>> udtNestedMap) {
    this.udtNestedMap = udtNestedMap;
  }

  public TestNestedUdt getUdtNested() {
    return udtNested;
  }

  public void setUdtNested(final TestNestedUdt udtNested) {
    this.udtNested = udtNested;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestUdt)) {
      return false;
    }
    final TestUdt testUdt = (TestUdt) o;
    return number == testUdt.number && Objects.equals(udtNested, testUdt.udtNested) && Objects.equals(value, testUdt.value) && Objects.equals(
        udtNestedList, testUdt.udtNestedList) && Objects.equals(udtNestedNestedSet, testUdt.udtNestedNestedSet) && Objects.equals(udtNestedMap,
                                                                                                                                  testUdt.udtNestedMap);
  }

  @Override
  public int hashCode() {
    return Objects.hash(udtNested, number, value, udtNestedList, udtNestedNestedSet, udtNestedMap);
  }

  @Override
  public String toString() {
    return "TestUdt{" + "udtNested=" + udtNested + ", number=" + number + ", value='" + value + '\'' + ", udtNestedList=" + udtNestedList
        + ", udtNestedNestedSet=" + udtNestedNestedSet + ", udtNestedMap=" + udtNestedMap + '}';
  }
}
