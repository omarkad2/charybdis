package ma.markware.charybdis.dsl.update;

import ma.markware.charybdis.model.criteria.CriteriaExpression;

public interface UpdateExtraWhereExpression extends UpdateOnExistExpression, UpdateIfExpression, UpdateExecuteExpression {

  UpdateExtraWhereExpression and(CriteriaExpression condition);
}
