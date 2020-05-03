package ma.markware.charybdis.model.criteria;

public interface CriteriaExpression {

  String getFieldName();

  CriteriaOperator getCriteriaOperator();

  Object[] getValues();
}
