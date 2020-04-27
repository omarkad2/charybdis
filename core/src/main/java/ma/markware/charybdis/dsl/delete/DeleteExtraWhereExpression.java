package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface DeleteExtraWhereExpression extends DeleteOnExistExpression, DeleteIfExpression {

  DeleteExtraWhereExpression and(CriteriaExpression condition);
}
