package ma.markware.charybdis.dsl.insert;

public interface InsertOnExistExpression extends InsertTtlExpression {

  InsertTtlExpression ifNotExists();
}
