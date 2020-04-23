package ma.markware.charybdis.dsl.select;

public interface SelectLimitExpression extends SelectFilteringExpression {

  SelectFetchExpression limit(int nbOfRows);
}
