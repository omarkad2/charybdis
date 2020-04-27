package ma.markware.charybdis.dsl.select;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface SelectExtraWhereExpression extends SelectOrderExpression {

  SelectExtraWhereExpression and(CriteriaExpression criteria);
}
