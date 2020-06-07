package ma.markware.charybdis.model.criteria;

import ma.markware.charybdis.model.field.criteria.CriteriaField;

public class CriteriaExpression {

  private CriteriaField field;
  private CriteriaOperator criteriaOperator;
  private Object[] serializedValues;

  public CriteriaExpression(final CriteriaField field, final CriteriaOperator criteriaOperator, final Object[] serializedValues) {
    this.field = field;
    this.criteriaOperator = criteriaOperator;
    this.serializedValues = serializedValues;
  }

  public CriteriaExpression(final CriteriaField field, final CriteriaOperator criteriaOperator, final Object value) {
    this(field, criteriaOperator, new Object[]{ value });
  }

  public CriteriaField getField() {
    return field;
  }

  public CriteriaOperator getCriteriaOperator() {
    return criteriaOperator;
  }

  public Object[] getSerializedValues() {
    return serializedValues;
  }

  public ExtendedCriteriaExpression and(CriteriaExpression criteriaExpression) {
    return new ExtendedCriteriaExpression(this).and(criteriaExpression);
  }
}
