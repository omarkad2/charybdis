package ma.markware.charybdis.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ma.markware.charybdis.model.option.ReplicationStrategyClassEnum;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Keyspace {


  String name() default "";

  ReplicationStrategyClassEnum replicaPlacementStrategy() default ReplicationStrategyClassEnum.SIMPLE_STRATEGY;

  int replicationFactor() default 1;
}
