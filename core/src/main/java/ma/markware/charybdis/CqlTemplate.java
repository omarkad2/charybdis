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

package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.CqlSession;
import ma.markware.charybdis.batch.Batch;
import ma.markware.charybdis.batch.BatchQueryBuilder;
import ma.markware.charybdis.crud.CrudQueryBuilder;
import ma.markware.charybdis.dsl.DslQueryBuilder;
import ma.markware.charybdis.session.DefaultSessionFactory;
import ma.markware.charybdis.session.SessionFactory;
import ma.markware.charybdis.session.StandaloneSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Interface used to interact with Cql database. This API gives access to :
 * <ul>
 *   <li>Crud API {@link CrudQueryBuilder}</li>
 *   <li>Dsl API {@link DslQueryBuilder}</li>
 *   <li>Batch API{@link BatchQueryBuilder}</li>
 * </ul>
 */
@ThreadSafe
public class CqlTemplate {

  private static final Logger log = LoggerFactory.getLogger(CqlTemplate.class);

  private final SessionFactory sessionFactory;
  private ThreadLocal<Batch> threadLocal = new ThreadLocal<Batch>();

  /**
   * Initialize the data manager using a custom session factory.
   *
   * @param customSessionFactory Instance of {@link SessionFactory} responsible of creating cql sessions.
   */
  public CqlTemplate(SessionFactory customSessionFactory) {
    this.sessionFactory = customSessionFactory;
  }

  /**
   * Initialize the data manager using datastax default driver configuration.
   * For details: <a href="https://docs.datastax.com/en/developer/java-driver/4.5/manual/core/configuration/reference/">
   *   https://docs.datastax.com/en/developer/java-driver/4.5/manual/core/configuration/reference/</a>
   */
  public CqlTemplate() {
    this(new DefaultSessionFactory());
  }

  /**
   * Initialize the data manager with custom configuration file loaded from classpath.
   *
   * @param customConfiguration driver configuration file name in classpath.
   */
  public CqlTemplate(final String customConfiguration) {
    this(new DefaultSessionFactory(customConfiguration));
  }

  /**
   * Initialize the data manager with an existing session.
   *
   * @param session open cql session.
   */
  public CqlTemplate(CqlSession session) {
    this(new StandaloneSessionFactory(session));
  }

  /**
   * Create a Dsl API entry point.
   *
   * @return Dsl API
   */
  public DslQueryBuilder dsl() {
    return new DslQueryBuilder(sessionFactory.getSession(), threadLocal.get());
  }

  /**
   * Create a Dsl API entry point, for queries executed in batch
   *
   * @param batch enclosing batch query
   * @return Dsl API
   */
  public DslQueryBuilder dsl(Batch batch) {
    Batch enclosingBatch = threadLocal.get();
    return new DslQueryBuilder(sessionFactory.getSession(), enclosingBatch != null ? enclosingBatch : batch);
  }

  /**
   * Create a Crud API entry point.
   *
   * @return Crud API
   */
  public CrudQueryBuilder crud() {
    return new CrudQueryBuilder(sessionFactory.getSession(), threadLocal.get());
  }

  /**
   * Create a Crud API entry point, for queries executed in batch
   *
   * @param batch enclosing batch query
   * @return Crud API
   */
  public CrudQueryBuilder crud(Batch batch) {
    Batch enclosingBatch = threadLocal.get();
    return new CrudQueryBuilder(sessionFactory.getSession(), enclosingBatch != null ? enclosingBatch : batch);
  }

  /**
   * @return entry point to Batch API
   */
  public BatchQueryBuilder batch() {
    return new BatchQueryBuilder(sessionFactory.getSession());
  }

  /**
   * Execute all write queries that are present in {@link BatchContextCallback} as a unique logged batch query.
   *
   * @param action a set of write queries
   */
  public void executeAsLoggedBatch(BatchContextCallback action) {
    Batch batch = batch().logged();
    threadLocal.set(batch);
    log.info("Create thread local variable in thread: '{}'", Thread.currentThread().getName());
    log.info("Execute callback");
    action.execute();
    log.info("Execute Batch");
    batch.execute();
    threadLocal.remove();
  }

  /**
   * Execute all write queries that are present in {@link BatchContextCallback} as a unique unlogged batch query.
   *
   * @param action a set of write queries
   */
  public void executeAsUnloggedBatch(BatchContextCallback action) {
    Batch batch = batch().unlogged();
    threadLocal.set(batch);
    action.execute();
    batch.execute();
    threadLocal.remove();
  }

  /**
   * Callback interface for code/queries to be executed as a batch.
   * Used with {@link CqlTemplate}'s {@code executeAsUnloggedBatch} and {@code executeAsLoggedBatch} method,
   * often as anonymous class within a method implementation.
   */
  @FunctionalInterface
  public interface BatchContextCallback {

    /**
     * Gets called by {@link CqlTemplate#executeAsUnloggedBatch} and {@link CqlTemplate#executeAsLoggedBatch}
     * within a batch query context.
     * All write queries that are present in this context are be executed as an atomic batch query.
     */
    void execute();
  }
}
