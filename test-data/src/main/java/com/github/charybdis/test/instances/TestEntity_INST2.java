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

package com.github.charybdis.test.instances;

import com.github.charybdis.test.entities.TestEntity;
import com.github.charybdis.test.entities.TestEnum;
import com.github.charybdis.test.entities.TestExtraUdt;
import com.github.charybdis.test.entities.TestNestedUdt;
import com.github.charybdis.test.entities.TestUdt;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TestEntity_INST2 {

  public final static UUID id = UUID.randomUUID();
  public final static Instant date = Instant.now().plus(10, ChronoUnit.DAYS);
  public final static List<String> list = Arrays.asList("test1", "test2");
  public final static Set<Integer> se = null;
  public final static Map<String, String> map = null;
  public final static List<List<Integer>>  nestedList = null;
  public final static Set<List<Integer>>  nestedSet = null;
  public final static Map<String, Map<Integer, String>>  nestedMap = null;
  public final static TestEnum enumValue = null;
  public final static List<Set<TestEnum>> enumNestedList = null;
  public final static Map<Integer, TestEnum> enumMap = null;
  public final static List<TestEnum> enumList = null;
  private final static TestNestedUdt nestedUdt1 = new TestNestedUdt("nestedName1", "nestedValue1", null);
  public final static TestUdt udt = new TestUdt(2, "test2", null, null, null, nestedUdt1);
  public final static TestExtraUdt extraUdt = null;
  public final static List<TestUdt>  udtList = null;
  public final static Set<TestUdt>  udtSet = null;
  public final static Map<Integer, TestUdt>  udtMap = null;
  public final static List<List<TestUdt>> udtNestedList = null;
  public final static boolean flag = false;
  public final static TestEntity entity2 = new TestEntity(id, date, udt, list, se, map, nestedList, nestedSet, nestedMap, enumValue, enumList, enumMap, enumNestedList, extraUdt, udtList, udtSet, udtMap, udtNestedList, flag);
}
