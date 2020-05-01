package ma.markware.charybdis.dsl.update;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface UpdateExtraIfExpression extends UpdateExecuteExpression {

  UpdateExtraIfExpression and_(CriteriaExpression condition);
}
