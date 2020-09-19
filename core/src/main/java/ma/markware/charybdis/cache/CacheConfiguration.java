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

import javax.cache.configuration.Configuration;

/**
 * Cache configuration
 *
 * @param <K> cache key type
 * @param <V> cache value type
 *
 * @author Oussama Markad
 */
public class CacheConfiguration<K, V> implements Configuration<K, V> {

  private final Class<K> keyType;
  private final Class<V> valueType;

  public CacheConfiguration(final Class<K> keyType, final Class<V> valueType) {
    this.keyType = keyType;
    this.valueType = valueType;
  }

  @Override
  public Class<K> getKeyType() {
    return keyType;
  }

  @Override
  public Class<V> getValueType() {
    return valueType;
  }

  @Override
  public boolean isStoreByValue() {
    return true;
  }
}
