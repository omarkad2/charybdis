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

package ma.markware.charybdis.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CacheConfigurationTest {

  private final CacheConfiguration<String, Integer> cacheConfiguration = new CacheConfiguration<>(String.class, Integer.class);

  @Test
  void getKeyType() {
    assertThat(cacheConfiguration.getKeyType()).isEqualTo(String.class);
  }

  @Test
  void getValueType() {
    assertThat(cacheConfiguration.getValueType()).isEqualTo(Integer.class);
  }

  @Test
  void isStoreByValue() {
    assertThat(cacheConfiguration.isStoreByValue()).isTrue();
  }
}
