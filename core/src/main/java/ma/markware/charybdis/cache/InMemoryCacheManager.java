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

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal use only. Our main cache manager
 *
 * @author Oussama Markad
 */
class InMemoryCacheManager implements CacheManager {

  static final InMemoryCacheManager INSTANCE = new InMemoryCacheManager();

  private static final Logger log = LoggerFactory.getLogger(InMemoryCacheManager.class);
  private static final int CACHE_INITIAL_CAPACITY = 200;
  private static final int CACHE_MAX_CAPACITY = 500;

  private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();
  private final AtomicBoolean isClosed = new AtomicBoolean(false);

  @Override
  public CachingProvider getCachingProvider() {
    return null;
  }

  @Override
  public URI getURI() {
    return null;
  }

  @Override
  public ClassLoader getClassLoader() {
    return null;
  }

  @Override
  public Properties getProperties() {
    return new Properties();
  }

  @Override
  public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(final String name, final C configuration) {
    ensureOpen();
    checkNotNull(name, "cache name cannot be null");
    checkNotNull(configuration, "cache configuration cannot be null");
    if (caches.containsKey(name)) {
      throw new CacheException(format("A cache named '%s' already exists", name));
    }
    LRUCache<K, V> newCache = new LRUCache<>(name, CACHE_INITIAL_CAPACITY, CACHE_MAX_CAPACITY, configuration);
    caches.put(name, newCache);
    return newCache;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <K, V> Cache<K, V> getCache(final String name, final Class<K> keyType, final Class<V> valueType) {
    ensureOpen();
    checkNotNull(keyType, "key class can not be null");
    checkNotNull(valueType, "value class can not be null");
    Cache cache = caches.get(name);
    Configuration configuration = cache.getConfiguration(CacheConfiguration.class);
    Class actualKeyType = configuration.getKeyType();
    Class actualValueType = configuration.getValueType();
    if (keyType != actualKeyType) {
      throw new ClassCastException("Cache has key type " + actualKeyType.getName()
                                       + ", but getCache() called with key type " + keyType.getName());
    }
    if (valueType != actualValueType) {
      throw new ClassCastException("Cache has value type " + actualValueType.getName()
                                       + ", but getCache() called with value type " + valueType.getName());
    }
    return cache;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <K, V> Cache<K, V> getCache(final String name) {
    ensureOpen();
    checkNotNull(name, "cache name cannot be null");
    return caches.get(name);
  }

  @Override
  public Iterable<String> getCacheNames() {
    ensureOpen();
    return caches.keySet();
  }

  @Override
  public void destroyCache(final String name) {
    ensureOpen();
    checkNotNull(name, "cache name cannot be null");
    Cache cache = caches.get(name);
    if (cache != null) {
      cache.close();
      caches.remove(name);
    }
  }

  @Override
  public void enableManagement(final String cacheName, final boolean enabled) {
  }

  @Override
  public void enableStatistics(final String cacheName, final boolean enabled) {
  }

  @Override
  public void close() {
    if (isClosed.compareAndSet(false, true)) {
      caches.values().forEach(Cache::close);
    }
  }

  @Override
  public boolean isClosed() {
    return isClosed.get();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(final Class<T> clazz) {
    if (InMemoryCacheManager.class.isAssignableFrom(clazz)) {
      return (T) this;
    }
    throw new IllegalArgumentException("Cannot unwrap to " + clazz);
  }

  private static <T> void checkNotNull(T argument, String errorMessage) {
    if (argument == null) {
      throw new NullPointerException(errorMessage);
    }
  }

  private void ensureOpen() {
    if (isClosed()) {
      throw new IllegalStateException("CacheManager InMemoryCacheManager is already closed");
    }
  }
}
