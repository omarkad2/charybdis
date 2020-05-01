package ma.markware.charybdis.query.clause;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.update.Assignment;
import ma.markware.charybdis.model.metadata.ColumnMetadata;

public class AssignmentClause {

  private Assignment assignment;
  private Object bindValue;

  private AssignmentClause(final Assignment assignment, final Object bindValue) {
    this.assignment = assignment;
    this.bindValue = bindValue;
  }

  public static AssignmentClause from(final ColumnMetadata columnMetadata, final Object value) {
    return from(columnMetadata.getColumnName(), value);
  }

  public static AssignmentClause from(final String columnName, final Object value) {
    return new AssignmentClause(Assignment.setColumn(columnName, QueryBuilder.bindMarker()), value);
  }

  public Assignment getAssignment() {
    return assignment;
  }

  public Object getBindValue() {
    return bindValue;
  }
}
