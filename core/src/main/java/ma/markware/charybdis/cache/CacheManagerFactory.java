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

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache manager factory. If a cache provider exists, it is used otherwise we fallback on a in memory cache
 *
 * @author Oussama Markad
 */
public class CacheManagerFactory {

  private static final Logger log = LoggerFactory.getLogger(CacheManagerFactory.class);

  /**
   * @return cache manager
   */
  public static CacheManager getCacheManager() {
    try {
      CachingProvider cachingProvider = Caching.getCachingProvider();
      log.info("Using cache provider: {}", cachingProvider.getDefaultURI());
      return cachingProvider.getCacheManager();
    } catch (CacheException e) {
      log.info("No caching provider found, fallback on in memory cache");
      return InMemoryCacheManager.INSTANCE;
    }
  }
}
