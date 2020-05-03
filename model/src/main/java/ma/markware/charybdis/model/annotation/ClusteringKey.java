package ma.markware.charybdis.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ma.markware.charybdis.model.option.ClusteringOrder;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface ClusteringKey {

  int index() default 0;

  ClusteringOrder order() default ClusteringOrder.ASC;
}
