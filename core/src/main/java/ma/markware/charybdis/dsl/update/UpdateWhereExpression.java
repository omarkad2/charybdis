package ma.markware.charybdis.dsl.update;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface UpdateWhereExpression {

  UpdateExtraWhereExpression where(CriteriaExpression condition);
}
