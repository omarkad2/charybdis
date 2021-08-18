package ma.markware.charybdis.model.annotation;

/**
 * Annotation to indicate that a field is a counter column
 *
 * Examples:
 *
 * // Define counter column <i>'counter'</i>
 * <pre><code>
 * @literal @Table
 * public class Entity {
 *
 *  @literal @Column
 *  @literal @Counter
 *  private Integer counter;
 * }
 * </code></pre>
 *
 * @author Oussama Markad
 */
public @interface Counter {
}
