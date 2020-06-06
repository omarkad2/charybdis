package ma.markware.charybdis.model.field.metadata;

import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.exception.CharybdisUnsupportedCriteriaExpressionException;

public interface CollectionColumnMetadata<D, S> extends ColumnMetadata<D, S> {

  @Override
  default CriteriaExpression gt(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'greater than' on Collection types");
  }

  @Override
  default CriteriaExpression gte(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'greater than or equal' on Collection types");
  }

  @Override
  default CriteriaExpression lt(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'lesser than' on Collection types");
  }

  @Override
  default CriteriaExpression lte(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'lesser than or equal' on Collection types");
  }

  @Override
  default CriteriaExpression like(D value) {
    throw new CharybdisUnsupportedCriteriaExpressionException("Unsupported criteria 'like' on Collection types");
  }
}
