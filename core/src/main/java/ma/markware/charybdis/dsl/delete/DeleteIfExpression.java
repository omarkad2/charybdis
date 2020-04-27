package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface DeleteIfExpression extends DeleteExecuteExpression {

  DeleteExtraIfExpression if_(CriteriaExpression condition);
}
