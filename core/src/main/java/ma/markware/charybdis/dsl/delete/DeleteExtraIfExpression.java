package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface DeleteExtraIfExpression extends DeleteExecuteExpression {

  DeleteExtraIfExpression and_(CriteriaExpression condition);
}
