package ma.markware.charybdis.dsl.select;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface SelectFromExpression extends SelectOrderExpression {

  SelectConditionExpression where(CriteriaExpression condition);
}
