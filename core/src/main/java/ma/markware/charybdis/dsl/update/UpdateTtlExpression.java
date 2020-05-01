package ma.markware.charybdis.dsl.update;

public interface UpdateTtlExpression extends UpdateAssignmentExpression {

  UpdateAssignmentExpression usingTtl(int seconds);
}
