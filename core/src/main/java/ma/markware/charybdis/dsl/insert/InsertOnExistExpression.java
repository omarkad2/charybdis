package ma.markware.charybdis.dsl.insert;

public interface InsertOnExistExpression extends InsertTtlExpression, InsertTimestampExpression {

  <T extends InsertTtlExpression & InsertTimestampExpression> T ifNotExists();
}
