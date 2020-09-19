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
    Cache<Integer, String> cache = inMemoryCacheManager.createCache("test_cache", new CacheConfiguration<>(Integer.class, String.class));
    assertThat(cache).isNotNull();
  }

  @Test
  void createCache_should_throw_exception_when_cache_already_exist() {
    inMemoryCacheManager.createCache("test_cache", new CacheConfiguration<>(Integer.class, String.class));

    assertThatExceptionOfType(CacheException.class)
        .isThrownBy(() -> inMemoryCacheManager.createCache("test_cache", new CacheConfiguration<>(Integer.class, String.class)))
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
        .isThrownBy(() -> inMemoryCacheManager.createCache("test_cache", new CacheConfiguration<>(Integer.class, String.class)))
        .withMessage("CacheManager InMemoryCacheManager is already closed");
  }

  @Test
  void getCache() {
    inMemoryCacheManager.createCache("test_cache", new CacheConfiguration<>(Integer.class, String.class));
    //For now we ignore type parameters
    Cache<Object, Object> cache = inMemoryCacheManager.getCache("test_cache");
    assertThat(cache).isNotNull();
  }

  @Test
  void getCache_with_types() {
    inMemoryCacheManager.createCache("test_cache", new CacheConfiguration<>(Integer.class, String.class));

    Cache<Integer, String> cache = inMemoryCacheManager.getCache("test_cache", Integer.class, String.class);
    assertThat(cache).isNotNull();

    assertThatExceptionOfType(ClassCastException.class)
        .isThrownBy(() -> inMemoryCacheManager.getCache("test_cache", String.class, String.class))
        .withMessage("Cache has key type java.lang.Integer, but getCache() called with key type java.lang.String");

    assertThatExceptionOfType(ClassCastException.class)
        .isThrownBy(() -> inMemoryCacheManager.getCache("test_cache", Integer.class, Long.class))
        .withMessage("Cache has value type java.lang.String, but getCache() called with value type java.lang.Long");
  }

  @Test
  void getCacheNames() {
    inMemoryCacheManager.createCache("test_cache1", new CacheConfiguration<>(Integer.class, String.class));
    inMemoryCacheManager.createCache("test_cache2", new CacheConfiguration<>(Integer.class, String.class));

    Iterable<String> cacheNames = inMemoryCacheManager.getCacheNames();
    assertThat(cacheNames).containsExactlyInAnyOrder("test_cache1", "test_cache2");
  }

  @Test
  void destroyCache() {
    Cache<Integer, String> cache = inMemoryCacheManager.createCache("test_cache", new CacheConfiguration<>(Integer.class, String.class));

    inMemoryCacheManager.destroyCache("test_cache");
    assertThat(cache.isClosed()).isTrue();
  }

  @Test
  void close() {
    Cache<Object, Object> cache1 = inMemoryCacheManager.createCache("test_cache1", new CacheConfiguration<>(Object.class, Object.class));
    Cache<Object, Object> cache2 = inMemoryCacheManager.createCache("test_cache2", new CacheConfiguration<>(Object.class, Object.class));

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
