package ma.markware.charybdis.apt.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ma.markware.charybdis.apt.model.option.ClusteringOrderEnum;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface ClusteringKey {

  int index() default 0;

  ClusteringOrderEnum order() default ClusteringOrderEnum.ASC;
}
