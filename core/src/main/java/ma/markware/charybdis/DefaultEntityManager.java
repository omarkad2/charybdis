package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.CqlSession;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import ma.markware.charybdis.crud.CreateEntityManager;
import ma.markware.charybdis.crud.DeleteEntityManager;
import ma.markware.charybdis.crud.ReadEntityManager;
import ma.markware.charybdis.crud.UpdateEntityManager;
import ma.markware.charybdis.model.criteria.ExtendedCriteriaExpression;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;

public class DefaultEntityManager implements EntityManager {

  private final SessionFactory sessionFactory;

  public DefaultEntityManager(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public DefaultEntityManager() {
    this(new DefaultSessionFactory());
  }

  public DefaultEntityManager(final String customConfiguration) {
    this(new DefaultSessionFactory(customConfiguration));
  }

  public DefaultEntityManager(CqlSession session) {
    this(new StandaloneSessionFactory(session));
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final boolean ifNotExists) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withIfNotExists(ifNotExists)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final int seconds) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withTtl(seconds)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final boolean ifNotExists, final int seconds) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withIfNotExists(ifNotExists).withTtl(seconds)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final Instant timestamp) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withTimestamp(timestamp)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final boolean ifNotExists, final Instant timestamp) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withIfNotExists(ifNotExists).withTimestamp(timestamp)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final long timestamp) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withTimestamp(timestamp)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T create(final TableMetadata<T> tableMetadata, final T entity, final boolean ifNotExists, final long timestamp) {
    return new CreateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity).withIfNotExists(ifNotExists).withTimestamp(timestamp)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T update(final TableMetadata<T> tableMetadata, final T entity) {
    return new UpdateEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T delete(final TableMetadata<T> tableMetadata, final T entity) {
    return new DeleteEntityManager<T>().withTableMetadata(tableMetadata).withEntity(entity)
                                       .save(sessionFactory.getSession());
  }

  @Override
  public <T> T findOne(final TableMetadata<T> tableMetadata, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata).withConditions(conditions)
                                     .fetchOne(sessionFactory.getSession());
  }

  @Override
  public <T> Optional<T> findOptional(final TableMetadata<T> tableMetadata, final ExtendedCriteriaExpression conditions) {
    return Optional.ofNullable(findOne(tableMetadata, conditions));
  }

  @Override
  public <T> List<T> find(final TableMetadata<T> tableMetadata, final ExtendedCriteriaExpression conditions) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata).withConditions(conditions)
                                     .fetch(sessionFactory.getSession());
  }

  @Override
  public <T> PageResult<T> find(final TableMetadata<T> tableMetadata, final ExtendedCriteriaExpression conditions, final PageRequest pageRequest) {
    return new ReadEntityManager<T>().withTableMetadata(tableMetadata).withConditions(conditions)
                                     .fetchPage(sessionFactory.getSession());
  }
}
