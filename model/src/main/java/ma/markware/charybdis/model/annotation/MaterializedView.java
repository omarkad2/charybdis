package ma.markware.charybdis.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a class is a Cql Materialized View representation.
 *
 * Example:
 *
 * // Define materialized view <i>'test_table_by_value'</i> based on table <i>'test_table'</i> (represented with class TestTable.class)
 * // in keyspace <i>'test_keyspace'</i>.
 * <pre><code>
 * @literal @MaterializedView(keyspace="test_keyspace", baseTable=TestTable.class, name = "test_table")
 * public class MaterializedViewDefinition {
 *  ...<Column definitions>...
 * }
 * <code></pre>
 *
 * @author Oussama Markad
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MaterializedView {

  /**
   * Set keyspace name in which the materialized view exists.
   *
   * @return keyspace name defined in annotation.
   */
  String keyspace();

  /**
   * Set base table class representation in Charybdis
   *
   * @return base table class
   */
  Class<?> baseTable();

  /**
   * Set materialized view name.
   *
   * @return materialized view name defined in annotation.
   */
  String name();
}
