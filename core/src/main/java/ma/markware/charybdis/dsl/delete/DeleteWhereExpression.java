package ma.markware.charybdis.dsl.delete;

import ma.markware.charybdis.model.criteria.CriteriaExpression;

public interface DeleteWhereExpression extends DeleteOnExistExpression, DeleteIfExpression {

  DeleteExtraWhereExpression where(CriteriaExpression condition);
}
