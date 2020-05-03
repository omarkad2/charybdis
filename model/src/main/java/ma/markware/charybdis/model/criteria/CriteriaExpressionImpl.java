package ma.markware.charybdis.model.criteria;

public class CriteriaExpressionImpl implements CriteriaExpression {

  private String fieldName;
  private CriteriaOperator criteriaOperator;
  private Object[] values;

  public CriteriaExpressionImpl(final String fieldName, final CriteriaOperator criteriaOperator, final Object[] values) {
    this.fieldName = fieldName;
    this.criteriaOperator = criteriaOperator;
    this.values = values;
  }

  public CriteriaExpressionImpl(final String fieldName, final CriteriaOperator criteriaOperator, final Object value) {
    this.fieldName = fieldName;
    this.criteriaOperator = criteriaOperator;
    this.values = new Object[]{value};
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  @Override
  public CriteriaOperator getCriteriaOperator() {
    return criteriaOperator;
  }

  @Override
  public Object[] getValues() {
    return values;
  }
}
