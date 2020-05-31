package ma.markware.charybdis.model.field.metadata;

import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.exception.CharybdisUnsupportedCriteriaExpressionException;

public interface CollectionColumnMetadata<T> extends ColumnMetadata<T> {

  @Override
  default CriteriaExpression gt(T value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'greater than' on Collection types");
  }

  @Override
  default CriteriaExpression gte(T value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'greater than or equal' on Collection types");
  }

  @Override
  default CriteriaExpression lt(T value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'lesser than' on Collection types");
  }

  @Override
  default CriteriaExpression lte(T value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'lesser than or equal' on Collection types");
  }

  @Override
  default CriteriaExpression like(T value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'like' on Collection types");
  }
}
