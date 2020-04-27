package ma.markware.charybdis.dsl;

// For test purposes only
public class TempCriteria implements CriteriaExpression {

  private String columnName;
  private CriteriaOperatorEnum criteriaOperator;
  private Object[] values;

  public TempCriteria(final String columnName, final CriteriaOperatorEnum criteriaOperator, final Object... values) {
    this.columnName = columnName;
    this.criteriaOperator = criteriaOperator;
    this.values = values;
  }

  @Override
  public String getColumnName() {
    return columnName;
  }

  @Override
  public CriteriaOperatorEnum getCriteriaOperator() {
    return criteriaOperator;
  }

  @Override
  public Object[] getValues() {
    return values;
  }
}
