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

import static java.lang.String.format;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Least recently used cache.
 *
 * @param <K> key type
 * @param <V> value type
 *
 * @author Oussama Markad
 */
class LRUCache<K, V> implements Cache<K, V> {

  private static final Logger log = LoggerFactory.getLogger(LRUCache.class);

  private final String name;
  private final ConcurrentLinkedHashMap<K, V> cache;
  private final AtomicBoolean isClosed;
  private final Configuration<K, V> cacheConfig;

  LRUCache(final String name, final int initialCacheCapacity, final int maxCacheCapacity, final Configuration<K, V> cacheConfig) {
    this.name = name;
    this.cache = new ConcurrentLinkedHashMap.Builder<K, V>()
        .initialCapacity(initialCacheCapacity)
        .maximumWeightedCapacity(maxCacheCapacity)
        .listener((key, value) -> log.info("Cache Eviction : [key: {}, value: {}]", key, value))
        .build();
    this.isClosed = new AtomicBoolean(false);
    this.cacheConfig = cacheConfig;
  }

  @Override
  public V get(final K k) {
    ensureOpen();
    return cache.get(k);
  }

  @Override
  public Map<K, V> getAll(final Set<? extends K> set) {
    ensureOpen();
    if (set == null) {
      // Should throw NPE
      return null;
    }
    return set.stream().filter(cache::containsKey)
              .collect(Collectors.toMap(setItem -> setItem, cache::get));
  }

  @Override
  public boolean containsKey(final K k) {
    ensureOpen();
    return cache.containsKey(k);
  }

  @Override
  public void put(final K k, final V v) {
    ensureOpen();
    cache.put(k, v);
  }

  @Override
  public V getAndPut(final K k, final V v) {
    ensureOpen();
    V v1 = cache.get(k);
    cache.put(k, v);
    return v1;
  }

  @Override
  public void putAll(final Map<? extends K, ? extends V> map) {
    ensureOpen();
    cache.putAll(map);
  }

  @Override
  public boolean putIfAbsent(final K k, final V v) {
    ensureOpen();
    return cache.putIfAbsent(k, v) != null;
  }

  @Override
  public boolean remove(final K k) {
    ensureOpen();
    return cache.remove(k) != null;
  }

  @Override
  public boolean remove(final K k, final V v) {
    ensureOpen();
    return cache.remove(k, v);
  }

  @Override
  public V getAndRemove(final K k) {
    ensureOpen();
    return cache.remove(k);
  }

  @Override
  public boolean replace(final K k, final V v, final V v1) {
    ensureOpen();
    return cache.replace(k, v, v1);
  }

  @Override
  public boolean replace(final K k, final V v) {
    ensureOpen();
    if (cache.containsKey(k)) {
      cache.put(k, v);
      return true;
    }
    return false;
  }

  @Override
  public V getAndReplace(final K k, final V v) {
    ensureOpen();
    V v1 = cache.get(k);
    replace(k, v);
    return v1;
  }

  @Override
  public void removeAll(final Set<? extends K> set) {
    ensureOpen();
    if (set != null) {
      set.forEach(cache::remove);
    }
  }

  @Override
  public void removeAll() {
    ensureOpen();
    cache.clear();
  }

  @Override
  public void clear() {
    ensureOpen();
    cache.clear();
  }

  @Override
  public <C extends Configuration<K, V>> C getConfiguration(final Class<C> clazz) {
    if (clazz.isInstance(cacheConfig)) {
      return clazz.cast(this.cacheConfig);
    }
    throw new IllegalArgumentException(format("The configuration class %s is not supported by this implementation", clazz));
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public CacheManager getCacheManager() {
    return InMemoryCacheManager.INSTANCE;
  }

  @Override
  public void close() {
    isClosed.set(true);
  }

  @Override
  public boolean isClosed() {
    return isClosed.get();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(final Class<T> clazz) {
    if (LRUCache.class.isAssignableFrom(clazz)) {
      return (T) this;
    }
    throw new IllegalArgumentException("Cannot unwrap to " + clazz);
  }

  @Override
  @Nonnull
  public Iterator<Entry<K, V>> iterator() {
    ensureOpen();
    Iterator<LRUCacheEntry<K, V>> iterator =
        cache.entrySet()
             .stream()
             .map(entry -> new LRUCacheEntry<>(entry.getKey(), entry.getValue()))
             .iterator();

    return new Iterator<Entry<K, V>>() {
      LRUCacheEntry<K, V> current;

      @Override
      public boolean hasNext() {
        ensureOpen();
        return iterator.hasNext();
      }

      @Override
      public Entry<K, V> next() {
        ensureOpen();
        LRUCacheEntry<K, V> next = iterator.next();
        current = next;
        return next;
      }

      @Override
      public void remove() {
        ensureOpen();
        if (current == null) {
          throw new IllegalStateException("Cannot remove element from iterator");
        }
        cache.remove(current.getKey());
        current = null;
      }
    };
  }

  private void ensureOpen() {
    if (isClosed()) {
      throw new IllegalStateException("Cache LRUCache is already closed");
    }
  }

  @Override
  public <T> T invoke(final K k, final EntryProcessor<K, V, T> entryProcessor, final Object... objects) throws EntryProcessorException {
    throw new UnsupportedOperationException("LRUCache invoke method unsupported");
  }

  @Override
  public <T> Map<K, EntryProcessorResult<T>> invokeAll(final Set<? extends K> set, final EntryProcessor<K, V, T> entryProcessor,
      final Object... objects) {
    throw new UnsupportedOperationException("LRUCache invokeAll method unsupported");
  }

  @Override
  public void loadAll(final Set<? extends K> set, final boolean b, final CompletionListener completionListener) {
    throw new UnsupportedOperationException("LRUCache loadAll method unsupported");
  }

  @Override
  public void registerCacheEntryListener(final CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
    throw new UnsupportedOperationException("LRUCache registerCacheEntryListener method unsupported");
  }

  @Override
  public void deregisterCacheEntryListener(final CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
    throw new UnsupportedOperationException("LRUCache deregisterCacheEntryListener method unsupported");
  }

  static class LRUCacheEntry<K, V> implements Cache.Entry<K, V> {

    private final K key;
    private final V value;

    LRUCacheEntry(final K key, final V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> clazz) {
      if (clazz == LRUCacheEntry.class) {
        return (T) this;
      }
      throw new IllegalArgumentException("Cannot unwrap to " + clazz);
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof LRUCacheEntry)) {
        return false;
      }
      final LRUCacheEntry<?, ?> that = (LRUCacheEntry<?, ?>) o;
      return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, value);
    }
  }
}
