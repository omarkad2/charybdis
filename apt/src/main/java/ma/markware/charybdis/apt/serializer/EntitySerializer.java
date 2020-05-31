package ma.markware.charybdis.apt.serializer;

import static java.lang.String.format;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.exception.CharybdisSerializationException;

public interface EntitySerializer<ENTITY_META_TYPE> {

  default FieldSpec buildStaticInstance(String packageName, String className, String entityName) {
    ClassName type = ClassName.get(packageName, className);
    return FieldSpec.builder(type, entityName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new $T()", type)
                    .build();
  }

  default FieldSpec buildEntityNameField(String attributeName, String value) {
    return FieldSpec.builder(String.class, attributeName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", value)
                    .build();
  }

  default MethodSpec buildPrivateConstructor() {
    return MethodSpec.constructorBuilder()
                     .addModifiers(Modifier.PRIVATE)
                     .build();
  }

  default MethodSpec buildGetEntityNameMethod(String methodName, String attributeName) {
    return MethodSpec.methodBuilder(methodName)
                     .addModifiers(Modifier.PUBLIC)
                     .returns(String.class)
                     .addStatement("return $N", attributeName)
                     .build();
  }

  default void writeSerialization(String packageName, String className, TypeSpec typeSpec, Filer filer) {
    try {
      JavaFile.builder(packageName, typeSpec)
              .build()
              .writeTo(filer);
    } catch (IOException e) {
      throw new CharybdisSerializationException(format("Serialization of class '%s' failed", className), e);
    }
  }

  void serialize(final ENTITY_META_TYPE metaType);

  String getClassName(String metaTypeClassName);
}
