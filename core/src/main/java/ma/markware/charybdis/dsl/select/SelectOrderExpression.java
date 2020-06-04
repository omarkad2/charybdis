package ma.markware.charybdis.dsl.select;

import ma.markware.charybdis.model.order.OrderExpression;

public interface SelectOrderExpression extends SelectLimitExpression {

  SelectLimitExpression orderBy(OrderExpression orderExpression);
}
