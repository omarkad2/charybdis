package ma.markware.charybdis.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 *
 * @author Oussama Markad
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MaterializedView {

  /**
   *
   * @return
   */
  String keyspace();

  /**
   *
   * @return
   */
  Class<?> baseTable();

  /**
   *
   * @return
   */
  String name();
}
