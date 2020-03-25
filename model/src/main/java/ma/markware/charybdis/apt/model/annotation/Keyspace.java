package ma.markware.charybdis.apt.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ma.markware.charybdis.apt.model.option.ReplicationStrategyClassEnum;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Keyspace {


  String name() default "";

  ReplicationStrategyClassEnum replicaPlacementStrategy() default ReplicationStrategyClassEnum.SIMPLESTRATEGY;

  int replicationFactor() default 1;
}
