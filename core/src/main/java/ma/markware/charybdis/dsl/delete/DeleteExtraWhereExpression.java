package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.model.criteria.CriteriaExpression;

public interface DeleteExtraWhereExpression extends DeleteIfExpression {

  DeleteExtraWhereExpression and(CriteriaExpression condition);
}
