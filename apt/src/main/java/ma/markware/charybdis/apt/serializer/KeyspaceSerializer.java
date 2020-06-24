/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ma.markware.charybdis.apt.serializer;

import com.squareup.javapoet.TypeSpec;
import java.util.Arrays;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.model.field.metadata.KeyspaceMetadata;

/**
 * A specific Class serializer.
 * Serializes Keyspace metadata {@link ma.markware.charybdis.apt.metatype.KeyspaceMetaType} into java methods and fields.
 *
 * @author Oussama Markad
 */
public class KeyspaceSerializer implements EntitySerializer<KeyspaceMetaType> {

  private final Filer filer;

  public KeyspaceSerializer(final Filer filer) {
    this.filer = filer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(final KeyspaceMetaType keyspaceMetaType) {
    String className = keyspaceMetaType.getDeserializationName();
    String packageName = keyspaceMetaType.getPackageName();
    String generatedClassName = resolveClassName(className);
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

  /**
   * {@inheritDoc}
   */
  @Override
  public String resolveClassName(final String metaTypeClassName) {
    return metaTypeClassName + SerializationConstants.KEYSPACE_SERIALIZATION_SUFFIX;
  }
}
