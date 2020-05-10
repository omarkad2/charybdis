package ma.markware.charybdis.model.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExtendedCriteriaExpression {

  private List<CriteriaExpression> criterias;

  ExtendedCriteriaExpression(final CriteriaExpression criteria) {
    this.criterias = new ArrayList<>(Collections.singletonList(criteria));
  }

  public ExtendedCriteriaExpression and(CriteriaExpression criteria) {
    criterias.add(criteria);
    return this;
  }

  public List<CriteriaExpression> getCriterias() {
    return criterias;
  }
}
