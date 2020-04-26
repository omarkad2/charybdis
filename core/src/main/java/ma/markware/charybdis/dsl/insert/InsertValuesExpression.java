package ma.markware.charybdis.dsl.insert;

public interface InsertValuesExpression extends InsertOnExistExpression {

  InsertValuesExpression values(Object... values);
}
