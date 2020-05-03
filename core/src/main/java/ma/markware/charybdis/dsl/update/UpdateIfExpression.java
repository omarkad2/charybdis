package ma.markware.charybdis.dsl.update;

import ma.markware.charybdis.model.criteria.CriteriaExpression;

public interface UpdateIfExpression extends UpdateExecuteExpression {

  UpdateExtraIfExpression if_(CriteriaExpression condition);
}
