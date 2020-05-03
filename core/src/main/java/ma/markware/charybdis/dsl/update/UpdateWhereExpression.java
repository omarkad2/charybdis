package ma.markware.charybdis.dsl.update;

import ma.markware.charybdis.model.criteria.CriteriaExpression;

public interface UpdateWhereExpression {

  UpdateExtraWhereExpression where(CriteriaExpression condition);
}
