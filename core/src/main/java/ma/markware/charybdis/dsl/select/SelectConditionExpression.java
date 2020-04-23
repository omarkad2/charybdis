package ma.markware.charybdis.dsl.select;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface SelectConditionExpression extends SelectOrderExpression {

  SelectConditionExpression and(CriteriaExpression criteria);
}
