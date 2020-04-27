package ma.markware.charybdis.dsl.insert;

public interface InsertTtlExpression extends InsertExecuteExpression {

  InsertExecuteExpression usingTtl(int seconds);
}
