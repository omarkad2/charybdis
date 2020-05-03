package ma.markware.charybdis.dsl.select;

import ma.markware.charybdis.model.criteria.CriteriaExpression;

public interface SelectWhereExpression extends SelectOrderExpression {

  SelectExtraWhereExpression where(CriteriaExpression condition);
}
