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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Properties;
import javax.cache.Cache;
import javax.cache.CacheException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryCacheManagerTest {

  private InMemoryCacheManager inMemoryCacheManager;

  @BeforeEach
  void setup() {
    inMemoryCacheManager = new InMemoryCacheManager();
  }

  @Test
  void getCachingProvider() {
    assertThat(inMemoryCacheManager.getCachingProvider()).isNull();
  }

  @Test
  void getURI() {
    assertThat(inMemoryCacheManager.getURI()).isNull();
  }

  @Test
  void getClassLoader() {
    assertThat(inMemoryCacheManager.getClassLoader()).isNull();
  }

  @Test
  void getProperties() {
    assertThat(inMemoryCacheManager.getProperties()).isEqualTo(new Properties());
  }

  @Test
  void createCache() {
    Cache<Object, Object> cache = inMemoryCacheManager.createCache("test_cache", null);
    assertThat(cache).isNotNull();
  }

  @Test
  void createCache_should_throw_exception_when_cache_already_exist() {
    inMemoryCacheManager.createCache("test_cache", null);

    assertThatExceptionOfType(CacheException.class)
        .isThrownBy(() -> inMemoryCacheManager.createCache("test_cache", null))
        .withMessage("A cache named 'test_cache' already exists");
  }

  @Test
  void createCache_should_throw_exception_when_input_name_null() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> inMemoryCacheManager.createCache(null, null))
        .withMessage("cache name cannot be null");
  }

  @Test
  void createCache_should_throw_exception_when_cache_manager_closed() {
    inMemoryCacheManager.close();
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> inMemoryCacheManager.createCache("test_cache", null))
        .withMessage("CacheManager InMemoryCacheManager is already closed");
  }

  @Test
  void getCache() {
    inMemoryCacheManager.createCache("test_cache", null);
    //For now we ignore type parameters
    Cache<Object, Object> cache = inMemoryCacheManager.getCache("test_cache");
    assertThat(cache).isNotNull();
  }

  @Test
  void getCache_with_types() {
    inMemoryCacheManager.createCache("test_cache", null);
    //For now we ignore type parameters
    Cache<Integer, String> cache = inMemoryCacheManager.getCache("test_cache", Integer.class, String.class);
    assertThat(cache).isNotNull();
  }

  @Test
  void getCacheNames() {
    inMemoryCacheManager.createCache("test_cache1", null);
    inMemoryCacheManager.createCache("test_cache2", null);

    Iterable<String> cacheNames = inMemoryCacheManager.getCacheNames();
    assertThat(cacheNames).containsExactlyInAnyOrder("test_cache1", "test_cache2");
  }

  @Test
  void destroyCache() {
    Cache<Object, Object> cache = inMemoryCacheManager.createCache("test_cache", null);

    inMemoryCacheManager.destroyCache("test_cache");
    assertThat(cache.isClosed()).isTrue();
  }

  @Test
  void close() {
    Cache<Object, Object> cache1 = inMemoryCacheManager.createCache("test_cache1", null);
    Cache<Object, Object> cache2 = inMemoryCacheManager.createCache("test_cache2", null);

    inMemoryCacheManager.close();

    assertThat(inMemoryCacheManager.isClosed()).isTrue();
    assertThat(cache1.isClosed()).isTrue();
    assertThat(cache2.isClosed()).isTrue();
  }

  @Test
  void unwrap() {
    assertThat(inMemoryCacheManager.unwrap(InMemoryCacheManager.class)).isEqualTo(inMemoryCacheManager);

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> inMemoryCacheManager.unwrap(Integer.class))
        .withMessage("Cannot unwrap to class java.lang.Integer");
  }
}
