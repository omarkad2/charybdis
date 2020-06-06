package ma.markware.charybdis.model.criteria;

import java.util.Arrays;
import java.util.Objects;
import ma.markware.charybdis.model.field.criteria.CriteriaField;

public class CriteriaExpression {

  private CriteriaField field;
  private CriteriaOperator criteriaOperator;
  private Object[] serializedValues;

  public CriteriaExpression(final CriteriaField field, final CriteriaOperator criteriaOperator, final Object[] serializedValues) {
    this.field = field;
    this.criteriaOperator = criteriaOperator;
    this.serializedValues = serializedValues;
  }

  public CriteriaExpression(final CriteriaField field, final CriteriaOperator criteriaOperator, final Object value) {
    this(field, criteriaOperator, new Object[]{ value });
  }

  public CriteriaField getField() {
    return field;
  }

  public CriteriaOperator getCriteriaOperator() {
    return criteriaOperator;
  }

  public Object[] getSerializedValues() {
    return serializedValues;
  }

  public ExtendedCriteriaExpression and(CriteriaExpression criteriaExpression) {
    return new ExtendedCriteriaExpression(this).and(criteriaExpression);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CriteriaExpression)) {
      return false;
    }
    final CriteriaExpression that = (CriteriaExpression) o;
    return Objects.equals(field, that.field) && criteriaOperator == that.criteriaOperator && Arrays.equals(serializedValues, that.serializedValues);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(field, criteriaOperator);
    result = 31 * result + Arrays.hashCode(serializedValues);
    return result;
  }

  @Override
  public String toString() {
    return "CriteriaExpression{" + "field=" + field + ", criteriaOperator=" + criteriaOperator + ", serializedValues=" + Arrays.toString(serializedValues) + '}';
  }
}
