package ma.markware.charybdis.apt.serializer;

import com.squareup.javapoet.TypeSpec;
import java.util.Arrays;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.model.metadata.KeyspaceMetadata;

public class KeyspaceSerializer implements Serializer<KeyspaceMetaType> {

  @Override
  public void serialize(final KeyspaceMetaType keyspaceMetaType, final AptContext aptContext, final Filer filer) {
    String className = keyspaceMetaType.getClassName();
    String packageName = keyspaceMetaType.getPackageName();
    String generatedClassName = getClassName(className);
    String keyspaceName = keyspaceMetaType.getKeyspaceName();
    TypeSpec keyspaceMetadataSerialization = TypeSpec.classBuilder(generatedClassName)
                                             .addModifiers(Modifier.PUBLIC)
                                             .addSuperinterface(KeyspaceMetadata.class)
                                             .addFields(Arrays.asList(
                                                 buildStaticInstance(packageName, generatedClassName, keyspaceName),
                                                 buildEntityNameField(SerializationConstants.KEYSPACE_NAME_ATTRIBUTE, keyspaceName)))
                                             .addMethods(Arrays.asList(
                                                 buildPrivateConstructor(),
                                                 buildGetEntityNameMethod(SerializationConstants.GET_KEYSPACE_NAME_METHOD, SerializationConstants.KEYSPACE_NAME_ATTRIBUTE)))
                                             .build();

    writeSerialization(packageName, className, keyspaceMetadataSerialization, filer);
  }

  @Override
  public String getClassName(final String metaTypeClassName) {
    return metaTypeClassName + SerializationConstants.KEYSPACE_SERIALIZATION_SUFFIX;
  }
}
