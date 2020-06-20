package ma.markware.charybdis.crud;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.ExtendedCriteriaExpression;
import ma.markware.charybdis.model.field.metadata.TableMetadata;
import ma.markware.charybdis.query.PageRequest;
import ma.markware.charybdis.query.PageResult;

public interface EntityManager {

  <T> T create(TableMetadata<T> tableMetadata, T entity);

  <T> T create(TableMetadata<T> tableMetadata, T entity, boolean ifNotExists);

  <T> T create(TableMetadata<T> tableMetadata, T entity, int seconds);

  <T> T create(TableMetadata<T> tableMetadata, T entity, boolean ifNotExists, int seconds);

  <T> T create(TableMetadata<T> tableMetadata, T entity, Instant timestamp);

  <T> T create(TableMetadata<T> tableMetadata, T entity, long timestamp);

  <T> T update(TableMetadata<T> tableMetadata, T entity);

  <T> T delete(TableMetadata<T> tableMetadata, T entity);

  <T> T findOne(TableMetadata<T> tableMetadata, ExtendedCriteriaExpression conditions);

  <T> T findOne(TableMetadata<T> tableMetadata, CriteriaExpression condition);

  <T> Optional<T> findOptional(TableMetadata<T> tableMetadata, ExtendedCriteriaExpression conditions);

  <T> Optional<T> findOptional(TableMetadata<T> tableMetadata, CriteriaExpression condition);

  <T> List<T> find(TableMetadata<T> tableMetadata);

  <T> List<T> find(TableMetadata<T> tableMetadata, ExtendedCriteriaExpression conditions);

  <T> List<T> find(TableMetadata<T> tableMetadata, CriteriaExpression condition);

  <T> PageResult<T> find(TableMetadata<T> tableMetadata, PageRequest pageRequest);

  <T> PageResult<T> find(TableMetadata<T> tableMetadata, ExtendedCriteriaExpression conditions, PageRequest pageRequest);

  <T> PageResult<T> find(TableMetadata<T> tableMetadata, CriteriaExpression condition, PageRequest pageRequest);
}