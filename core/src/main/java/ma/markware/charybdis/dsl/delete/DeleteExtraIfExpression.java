package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.model.criteria.CriteriaExpression;

public interface DeleteExtraIfExpression extends DeleteExecuteExpression {

  DeleteExtraIfExpression and_(CriteriaExpression condition);
}
