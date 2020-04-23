package ma.markware.charybdis.dsl.select;

public interface SelectFilteringExpression extends SelectFetchExpression {

  SelectFetchExpression allowFiltering();
}
