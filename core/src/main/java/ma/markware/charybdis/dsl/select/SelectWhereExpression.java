package ma.markware.charybdis.dsl.select;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface SelectWhereExpression extends SelectOrderExpression {

  SelectExtraWhereExpression where(CriteriaExpression condition);
}
