package ma.markware.charybdis.model.field.criteria;

import com.datastax.oss.driver.api.querybuilder.condition.Condition;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import ma.markware.charybdis.model.criteria.CriteriaExpression;
import ma.markware.charybdis.model.criteria.CriteriaOperator;
import ma.markware.charybdis.model.field.Field;

public interface CriteriaField<T> extends Field {

  Object serialize(T field);

  default Relation toRelation(String operator, Term term) {
    return Relation.column(getName()).build(operator, term);
  }

  default Condition toCondition(String operator, Term term) {
    return Condition.column(getName()).build(operator, term);
  }

  default CriteriaExpression eq(T value) {
    return new CriteriaExpression(this, CriteriaOperator.EQ, serialize(value));
  }

  default CriteriaExpression neq(T value) {
    return new CriteriaExpression(this, CriteriaOperator.NOT_EQ, serialize(value));
  }

  default CriteriaExpression gt(T value) {
    return new CriteriaExpression(this, CriteriaOperator.GT, serialize(value));
  }

  default CriteriaExpression gte(T value) {
    return new CriteriaExpression(this, CriteriaOperator.GTE, serialize(value));
  }

  default CriteriaExpression lt(T value) {
    return new CriteriaExpression(this, CriteriaOperator.LT, serialize(value));
  }

  default CriteriaExpression lte(T value) {
    return new CriteriaExpression(this, CriteriaOperator.LTE, serialize(value));
  }
}
