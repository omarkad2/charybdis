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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.cache.Cache;
import ma.markware.charybdis.cache.LRUCache.LRUCacheEntry;
import org.junit.jupiter.api.Test;

class LRUCacheTest {

  @Test
  void get() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 1, 1);
    cache.put(0, "test0");

    assertThat(cache.get(0)).isEqualTo("test0");
  }

  @Test
  @SuppressWarnings("unchecked")
  void get_should_evict_least_recently_used_when_max_capacity_exceeded() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");
    cache.put(1, "test1");

    // this would make (1, "test1") the least recently used
    cache.get(0);

    cache.put(2, "test2");

    Iterator<Cache.Entry<Integer, String>> iterator = cache.iterator();

    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(0, "test0"));
    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(2, "test2"));
    assertThat(iterator.hasNext()).isFalse();
  }

  @Test
  void get_should_throw_exception_when_cache_closed() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 1, 1);
    cache.put(0, "test0");
    cache.close();

    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> cache.get(0))
        .withMessage("Cache LRUCache is already closed");
  }

  @Test
  @SuppressWarnings("unchecked")
  void put() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 3, 3);
    cache.put(0, "test0");
    cache.put(1, "test1");
    cache.put(2, "test2");

    Iterator<Cache.Entry<Integer, String>> iterator = cache.iterator();

    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(0, "test0"));
    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(1, "test1"));
    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(2, "test2"));
    assertThat(iterator.hasNext()).isFalse();
  }

  @Test
  @SuppressWarnings("unchecked")
  void put_should_evict_least_recently_used_value_when_max_capacity_exceeded() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 3, 3);
    cache.put(0, "test0");
    cache.put(1, "test1");
    cache.put(2, "test2");
    cache.put(3, "test3");

    Iterator<Cache.Entry<Integer, String>> iterator = cache.iterator();

    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(1, "test1"));
    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(2, "test2"));
    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(3, "test3"));
    assertThat(iterator.hasNext()).isFalse();
  }

  @Test
  void containsKey() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 1, 1);
    cache.put(0, "test0");

    assertThat(cache.containsKey(0)).isTrue();
  }

  @Test
  void containsKey_should_return_false_when_element_not_found() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 1, 1);
    cache.put(0, "test0");
    cache.put(1, "test1");

    assertThat(cache.containsKey(0)).isFalse();
  }

  @Test
  void getAndPut() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    assertThat(cache.getAndPut(0, "test0")).isNull();
    assertThat(cache.getAndPut(0, "test1")).isEqualTo("test0");
  }

  @Test
  void getAll() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");
    cache.put(1, "test1");
    cache.put(2, "test2");

    Map<Integer, String> cachedValues = cache.getAll(new HashSet<>(asList(0, 1, 2)));

    assertThat(cachedValues.entrySet()).extracting(Map.Entry::getKey, Map.Entry::getValue).containsExactly(
        tuple(1, "test1"),
        tuple(2, "test2")
    );
  }

  @Test
  void iterator() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");
    cache.put(1, "test1");

    Iterator<Cache.Entry<Integer, String>> iterator = cache.iterator();
    iterator.next();
    iterator.remove();
    iterator.next();
    iterator.remove();
    assertThat(iterator.hasNext()).isFalse();
  }

  @Test
  @SuppressWarnings("unchecked")
  void putAll() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.putAll(ImmutableMap.of(0, "test0", 1, "test1", 2, "test2"));

    Iterator<Cache.Entry<Integer, String>> iterator = cache.iterator();

    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(1, "test1"));
    assertThat(iterator.next()).isEqualTo(new LRUCacheEntry(2, "test2"));
    assertThat(iterator.hasNext()).isFalse();
  }

  @Test
  void putIfAbsent() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.putIfAbsent(0, "test0");
    assertThat(cache.get(0)).isEqualTo("test0");

    cache.putIfAbsent(0, "test1");
    assertThat(cache.get(0)).isEqualTo("test0");
  }

  @Test
  void remove() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");
    assertThat(cache.get(0)).isEqualTo("test0");

    cache.remove(0);
    assertThat(cache.get(0)).isNull();
  }

  @Test
  void remove_with_value() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");
    assertThat(cache.get(0)).isEqualTo("test0");

    // No-op
    assertThat(cache.remove(0, "test1")).isFalse();
    assertThat(cache.get(0)).isEqualTo("test0");

    assertThat(cache.remove(0, "test0")).isTrue();
    assertThat(cache.get(0)).isNull();
  }

  @Test
  void getAndRemove() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");

    assertThat(cache.getAndRemove(0)).isEqualTo("test0");
    assertThat(cache.get(0)).isNull();
  }

  @Test
  void replace() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");

    assertThat(cache.replace(0, "test1")).isTrue();
    assertThat(cache.get(0)).isEqualTo("test1");

    assertThat(cache.replace(1, "test2")).isFalse();
  }

  @Test
  void replace_with_value() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");

    assertThat(cache.replace(0, "test1", "test2")).isFalse();
    assertThat(cache.get(0)).isEqualTo("test0");

    assertThat(cache.replace(0, "test0", "test2")).isTrue();
    assertThat(cache.get(0)).isEqualTo("test2");
  }

  @Test
  void getAndReplace() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");

    assertThat(cache.getAndReplace(0, "test1")).isEqualTo("test0");
    assertThat(cache.get(0)).isEqualTo("test1");
  }

  @Test
  void removeAll() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");

    cache.removeAll();
    assertThat(cache.containsKey(0)).isFalse();
  }

  @Test
  void removeAll_with_subset() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");

    cache.removeAll(Collections.singleton(1));
    assertThat(cache.containsKey(0)).isTrue();

    cache.removeAll(Collections.singleton(0));
    assertThat(cache.containsKey(0)).isFalse();
  }

  @Test
  void clear() {
    LRUCache<Integer, String> cache = new LRUCache<>("test_cache", 2, 2);
    cache.put(0, "test0");

    cache.clear();
    assertThat(cache.containsKey(0)).isFalse();
  }

  @Test
  void getName() {
    assertThat(new LRUCache<>("test_cache", 2, 2).getName()).isEqualTo("test_cache");
  }

  @Test
  void close() {
    LRUCache<Object, Object> cache = new LRUCache<>("test_cache", 2, 2);
    cache.close();
    assertThat(cache.isClosed()).isTrue();
  }

  @Test
  void getCacheManager() {
    assertThat(new LRUCache<>("test_cache", 2, 2).getCacheManager()).isEqualTo(InMemoryCacheManager.INSTANCE);
  }

  @Test
  void unwrap() {
    LRUCache<Object, Object> cache = new LRUCache<>("test_cache", 2, 2);
    assertThat(cache.unwrap(LRUCache.class)).isEqualTo(cache);

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> cache.unwrap(Integer.class))
        .withMessage("Cannot unwrap to class java.lang.Integer");
  }

  // Not supported methods
  @Test
  void invoke() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> new LRUCache<>("test_cache", 3, 3).invoke(null, null, new Object[]{}))
        .withMessage("LRUCache invoke method unsupported");
  }

  @Test
  void invokeAll() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> new LRUCache<>("test_cache", 3, 3).invokeAll(null, null, new Object[]{}))
        .withMessage("LRUCache invokeAll method unsupported");
  }

  @Test
  void loadAll() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> new LRUCache<>("test_cache", 3, 3).loadAll(null, false, null))
        .withMessage("LRUCache loadAll method unsupported");
  }

  @Test
  void registerCacheEntryListener() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> new LRUCache<>("test_cache", 3, 3).registerCacheEntryListener(null))
        .withMessage("LRUCache registerCacheEntryListener method unsupported");
  }

  @Test
  void deregisterCacheEntryListener() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> new LRUCache<>("test_cache", 3, 3).deregisterCacheEntryListener(null))
        .withMessage("LRUCache deregisterCacheEntryListener method unsupported");
  }
}
