package ma.markware.charybdis.dsl;

public interface CriteriaExpression {

  String getColumnName();

  CriteriaOperatorEnum getCriteriaOperator();

  Object[] getValues();
}
