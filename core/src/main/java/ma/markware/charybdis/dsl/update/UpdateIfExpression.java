package ma.markware.charybdis.dsl.update;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface UpdateIfExpression extends UpdateExecuteExpression {

  UpdateExtraIfExpression if_(CriteriaExpression condition);
}
