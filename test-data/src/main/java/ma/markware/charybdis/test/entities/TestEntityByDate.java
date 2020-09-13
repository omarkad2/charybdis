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

package ma.markware.charybdis.test.entities;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import ma.markware.charybdis.model.annotation.ClusteringKey;
import ma.markware.charybdis.model.annotation.PartitionKey;
import ma.markware.charybdis.model.annotation.Table;
import ma.markware.charybdis.model.option.ConsistencyLevel;
import ma.markware.charybdis.model.option.SerialConsistencyLevel;

@Table(keyspace = "test_keyspace", name = "test_entity_by_date", readConsistency = ConsistencyLevel.TWO,
    writeConsistency = ConsistencyLevel.TWO, serialConsistency = SerialConsistencyLevel.SERIAL)
public class TestEntityByDate extends TestSuperEntity {

  public TestEntityByDate() {
  }

  public TestEntityByDate(final Instant date, final TestUdt udt, final List<String> list, final Boolean flag) {
    this.date = date;
    this.udt = udt;
    this.list = list;
    this.flag = flag;
  }

  @Override
  @PartitionKey
  public Instant getDate() {
    return date;
  }

  @Override
  public void setDate(final Instant date) {
    this.date = date;
  }

  @Override
  @ClusteringKey
  public TestUdt getUdt() {
    return udt;
  }

  @Override
  public void setUdt(final TestUdt udt) {
    this.udt = udt;
  }

  @Override
  @ClusteringKey(index = 1)
  public List<String> getList() {
    return list;
  }

  @Override
  public void setList(final List<String> list) {
    this.list = list;
  }

  public Boolean isFlag() {
    return flag;
  }

  @Override
  public void setFlag(final Boolean flag) {
    this.flag = flag;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TestEntityByDate)) {
      return false;
    }
    final TestEntityByDate that = (TestEntityByDate) o;
    return Objects.equals(date, that.date) && Objects.equals(udt, that.udt) && Objects.equals(list, that.list) && Objects.equals(flag, that.flag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, udt, list, flag);
  }

  @Override
  public String toString() {
    return "TestEntityByDate{" + "date=" + date + ", udt=" + udt + ", list=" + list + ", flag=" + flag + '}';
  }
}
