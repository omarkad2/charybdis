package ma.markware.charybdis.dsl.delete;

public interface DeleteOnExistExpression extends DeleteExecuteExpression {

  DeleteExecuteExpression ifExists();
}
