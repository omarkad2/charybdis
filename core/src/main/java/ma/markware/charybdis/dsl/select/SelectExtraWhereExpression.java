package ma.markware.charybdis.dsl.select;


import ma.markware.charybdis.model.criteria.CriteriaExpression;

public interface SelectExtraWhereExpression extends SelectOrderExpression {

  SelectExtraWhereExpression and(CriteriaExpression criteria);
}
