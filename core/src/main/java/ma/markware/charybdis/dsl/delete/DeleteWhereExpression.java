package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.dsl.CriteriaExpression;

public interface DeleteWhereExpression extends DeleteOnExistExpression, DeleteIfExpression {

  DeleteExtraWhereExpression where(CriteriaExpression condition);
}
