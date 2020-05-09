package ma.markware.charybdis.model.criteria;

public class CriteriaExpression {

  private String fieldName;
  private CriteriaOperator criteriaOperator;
  private Object[] values;

  public CriteriaExpression(final String fieldName, final CriteriaOperator criteriaOperator, final Object[] values) {
    this.fieldName = fieldName;
    this.criteriaOperator = criteriaOperator;
    this.values = values;
  }

  public CriteriaExpression(final String fieldName, final CriteriaOperator criteriaOperator, final Object value) {
    this.fieldName = fieldName;
    this.criteriaOperator = criteriaOperator;
    this.values = new Object[]{ value };
  }

  public String getFieldName() {
    return fieldName;
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
