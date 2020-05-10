package ma.markware.charybdis.model.criteria;

import ma.markware.charybdis.model.field.criteria.CriteriaField;

public class CriteriaExpression {

  private CriteriaField field;
  private CriteriaOperator criteriaOperator;
  private Object[] values;

  public CriteriaExpression(final CriteriaField field, final CriteriaOperator criteriaOperator, final Object[] values) {
    this.field = field;
    this.criteriaOperator = criteriaOperator;
    this.values = values;
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

  public Object[] getValues() {
    return values;
  }

  public ExtendedCriteriaExpression and(CriteriaExpression criteriaExpression) {
    return new ExtendedCriteriaExpression(this).and(criteriaExpression);
  }
}
