package ma.markware.charybdis;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import ma.markware.charybdis.dsl.CriteriaExpression;
import ma.markware.charybdis.model.metadata.TableMetadata;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;

public interface EntityManager {

  <T> T create(TableMetadata<T> tableMetadata, T entity);

  <T> T create(TableMetadata<T> tableMetadata, T entity, boolean ifNotExists);

  <T> T create(TableMetadata<T> tableMetadata, T entity, int seconds);

  <T> T create(TableMetadata<T> tableMetadata, T entity, boolean ifNotExists, int seconds);

  <T> T create(TableMetadata<T> tableMetadata, T entity, Instant timestamp);

  <T> T create(TableMetadata<T> tableMetadata, T entity, boolean ifNotExists, Instant timestamp);

  <T> T create(TableMetadata<T> tableMetadata, T entity, long timestamp);

  <T> T create(TableMetadata<T> tableMetadata, T entity, boolean ifNotExists, long timestamp);

  <T> T update(TableMetadata<T> tableMetadata, T entity);

  <T> T delete(TableMetadata<T> tableMetadata, T entity);

  <T> T findOne(TableMetadata<T> tableMetadata, CriteriaExpression criteria);

  <T> Optional<T> findOptional(TableMetadata<T> tableMetadata, CriteriaExpression criteria);

  <T> List<T> find(TableMetadata<T> tableMetadata, CriteriaExpression criteria);

  <T> PageResult<T> find(TableMetadata<T> tableMetadata, CriteriaExpression criteria, PageRequest pageRequest);
}