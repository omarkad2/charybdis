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
package ma.markware.charybdis.apt.parser;


import static java.util.Arrays.asList;
import static ma.markware.charybdis.apt.parser.ParserTestHelper.buildFieldTypeMetaType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptConfiguration;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptDefaultConfiguration;
import ma.markware.charybdis.apt.CompilationExtension;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.test.entities.TestEntity;
import ma.markware.charybdis.test.entities.TestEntityWithNoPrimaryKey;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestExtraUdt;
import ma.markware.charybdis.test.entities.TestKeyspaceDefinition;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;
import ma.markware.charybdis.test.entities.invalid.TestEntityWithNonFrozenNestedCollectionField;
import ma.markware.charybdis.test.entities.invalid.TestEntityWithNonFrozenNestedUdtField;
import ma.markware.charybdis.test.entities.invalid.TestEntityWithUnsupportedType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith({CompilationExtension.class})
class TableParserTest {

  @Mock
  private RoundEnvironment roundEnvironment;
  @Mock
  private Messager messager;

  private AptConfiguration configuration;
  private TypeElement testNestedUdtElement;
  private TypeElement testUdtElement;
  private TypeElement testExtraUdtElement;
  private TypeElement testEntityElement;

  @BeforeAll
  @SuppressWarnings("unchecked")
  void setup(Elements elements) {
    MockitoAnnotations.initMocks(this);
    testNestedUdtElement = elements.getTypeElement(TestNestedUdt.class.getCanonicalName());
    testUdtElement = elements.getTypeElement(TestUdt.class.getCanonicalName());
    testExtraUdtElement = elements.getTypeElement(TestExtraUdt.class.getCanonicalName());
    Set elementsAnnotatedWithUdt = new HashSet<>(asList(testNestedUdtElement, testUdtElement, testExtraUdtElement));
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn(elementsAnnotatedWithUdt);
    testEntityElement = elements.getTypeElement(TestEntity.class.getCanonicalName());
  }

  @BeforeEach
  void initProcessorContext(Types types, Elements elements, Filer filer) {
    AptContext aptContext = new AptContext();
    this.configuration = AptDefaultConfiguration.initConfig(aptContext, types, elements, filer, messager);
    aptContext.init(roundEnvironment, configuration);
    // Define keyspace
    configuration.getKeyspaceParser().parse(elements.getTypeElement(TestKeyspaceDefinition.class.getCanonicalName()));
    // Define UDTs
    configuration.getUdtParser().parse(testNestedUdtElement);
    configuration.getUdtParser().parse(testUdtElement);

  }

  @Test
  void parseTableTest() {
    TableMetaType tableMetaType = configuration.getTableParser().parse(testEntityElement);
    assertThat(tableMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(tableMetaType.getTableName()).isEqualTo("test_entity");
    assertThat(tableMetaType.getColumns())
        .extracting(AbstractFieldMetaType::getSerializationName, AbstractFieldMetaType::getDeserializationName, AbstractFieldMetaType::getFieldType,
                    AbstractFieldMetaType::getGetterName, AbstractFieldMetaType::getSetterName, ColumnFieldMetaType::isPartitionKey,
                    ColumnFieldMetaType::getPartitionKeyIndex, ColumnFieldMetaType::isClusteringKey, ColumnFieldMetaType::getClusteringKeyIndex,
                    ColumnFieldMetaType::getClusteringOrder, ColumnFieldMetaType::isIndexed, ColumnFieldMetaType::getIndexName,
                    ColumnFieldMetaType::getSequenceModel, ColumnFieldMetaType::isCreationDate, ColumnFieldMetaType::isLastUpdatedDate)
        .containsExactlyInAnyOrder(
            tuple("id", "id", buildFieldTypeMetaType(TypeName.get(UUID.class), FieldTypeKind.NORMAL),
                  "getId", "setId", true, 0, false, null, null, false, null, null, false, false),
            tuple("date", "date", buildFieldTypeMetaType(TypeName.get(Instant.class), FieldTypeKind.NORMAL),
                  "getDate", "setDate", false, null, true, 0, ClusteringOrder.DESC, false, null, null, false, false),
            tuple("udt", "udt", buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                       true, false, false),
                  "getUdt", "setUdt", false, null, true, 1, ClusteringOrder.ASC, false, null, null, false, false),
            tuple("list", "list", buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, String.class), FieldTypeKind.LIST,
                                                       true, false,
                                                         buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeKind.NORMAL)),
                  "getList", "setList", false, null, true, 2, ClusteringOrder.ASC, false, null, null, false, false),
            tuple("se", "se", buildFieldTypeMetaType(ParameterizedTypeName.get(Set.class, Integer.class), FieldTypeKind.SET,
                                                         false, false,
                                                     buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeKind.NORMAL)),
                  "getSe", "setSe", false, null, false, null, null, false, null, null, false, false),
            tuple("map", "map", buildFieldTypeMetaType(ParameterizedTypeName.get(Map.class, String.class, String.class), FieldTypeKind.MAP,
                                                     false, false,
                                                       buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeKind.NORMAL),
                                                       buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeKind.NORMAL)),
                  "getMap", "setMap", false, null, false, null, null, false, null, null, false, false),
            tuple("nestedlist", "nestedList",
                  buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(List.class, Integer.class)),
                                         FieldTypeKind.LIST, true, true,
                                         buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, Integer.class), FieldTypeKind.LIST,
                                                                false, false,
                                                                buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeKind.NORMAL))),
                  "getNestedList", "setNestedList", false, null, false, null, null, false, null, null, false, false),
            tuple("nestedset", "nestedSet",
                  buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(Set.class), ParameterizedTypeName.get(List.class, Integer.class)),
                                         FieldTypeKind.SET, false, true,
                                         buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, Integer.class), FieldTypeKind.LIST,
                                                                true, false,
                                                                buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeKind.NORMAL))),
                  "getNestedSet", "setNestedSet", false, null, false, null, null, false, null, null, false, false),
            tuple("nestedmap", "nestedMap",
                  buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), ParameterizedTypeName.get(Map.class, Integer.class, String.class)),
                                         FieldTypeKind.MAP, false, true,
                                         buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeKind.NORMAL),
                                         buildFieldTypeMetaType(ParameterizedTypeName.get(Map.class, Integer.class, String.class), FieldTypeKind.MAP,
                                                                true, false,
                                                                buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeKind.NORMAL),
                                                                buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeKind.NORMAL))),
                  "getNestedMap", "setNestedMap", false, null, false, null, null, false, null, null, false, false),
            tuple("enumvalue", "enumValue", buildFieldTypeMetaType(TypeName.get(TestEnum.class), TypeName.get(String.class), FieldTypeKind.ENUM,
                                                       false, false, false),
                  "getEnumValue", "setEnumValue", false, null, false, null, null, false, null, null, false, false),
            tuple("enumlist", "enumList", buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, TestEnum.class), ParameterizedTypeName.get(List.class, String.class),
                                                                 FieldTypeKind.LIST, false, true, false,
                                                                 buildFieldTypeMetaType(TypeName.get(TestEnum.class), TypeName.get(String.class), FieldTypeKind.ENUM,
                                                                                        false, false, false)),
                  "getEnumList", "setEnumList", false, null, false, null, null, false, null, null, false, false),
            tuple("enummap", "enumMap", buildFieldTypeMetaType(ParameterizedTypeName.get(Map.class, Integer.class, TestEnum.class),
                                                               ParameterizedTypeName.get(Map.class, Integer.class, String.class),
                                                               FieldTypeKind.MAP, false, true, false,
                                                               buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeKind.NORMAL),
                                                               buildFieldTypeMetaType(TypeName.get(TestEnum.class), TypeName.get(String.class), FieldTypeKind.ENUM,
                                                                                        false, false, false)),
                  "getEnumMap", "setEnumMap", false, null, false, null, null, false, null, null, false, false),
            tuple("enumnestedlist", "enumNestedList",
                  buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(Set.class, TestEnum.class)),
                                         ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(Set.class, String.class)),
                                         FieldTypeKind.LIST, false, true, true,
                                         buildFieldTypeMetaType(ParameterizedTypeName.get(Set.class, TestEnum.class), ParameterizedTypeName.get(Set.class, String.class),
                                                                FieldTypeKind.SET, true, true, false,
                                                                buildFieldTypeMetaType(TypeName.get(TestEnum.class), TypeName.get(String.class), FieldTypeKind.ENUM,
                                                                                       false, false, false))),
                  "getEnumNestedList", "setEnumNestedList", false, null, false, null, null, false, null, null, false, false),
            tuple("udtlist", "udtList", buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, TestUdt.class), ParameterizedTypeName.get(List.class, UdtValue.class),
                                                                 FieldTypeKind.LIST, false, true, false,
                                                                 buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                                                        true, false, false)),
                  "getUdtList", "setUdtList", false, null, false, null, null, false, null, null, false, false),
            tuple("extraudt", "extraUdt", buildFieldTypeMetaType(TypeName.get(TestExtraUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                                 false, false, false),
                  "getExtraUdt", "setExtraUdt", false, null, false, null, null, false, null, null, false, false),
            tuple("udtset", "udtSet", buildFieldTypeMetaType(ParameterizedTypeName.get(Set.class, TestUdt.class), ParameterizedTypeName.get(Set.class, UdtValue.class),
                                                               FieldTypeKind.SET, false, true, false,
                                                               buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                                                      true, false, false)),
                  "getUdtSet", "setUdtSet", false, null, false, null, null, false, null, null, false, false),
            tuple("udtmap", "udtMap", buildFieldTypeMetaType(ParameterizedTypeName.get(Map.class, Integer.class, TestUdt.class),
                                                               ParameterizedTypeName.get(Map.class, Integer.class, UdtValue.class),
                                                               FieldTypeKind.MAP, false, true, false,
                                                               buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeKind.NORMAL),
                                                               buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                                                      true, false, false)),
                  "getUdtMap", "setUdtMap", false, null, false, null, null, false, null, null, false, false),
            tuple("udtnestedlist", "udtNestedList",
                  buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(List.class, TestUdt.class)),
                                         ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(List.class, UdtValue.class)),
                                         FieldTypeKind.LIST, false, true, true,
                                         buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, TestUdt.class), ParameterizedTypeName.get(List.class, UdtValue.class),
                                                                FieldTypeKind.LIST, true, true, false,
                                                                buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeKind.UDT,
                                                                                       false, false, false))),
                  "getUdtNestedList", "setUdtNestedList", false, null, false, null, null, false, null, null, false, false),
            tuple("flag", "flag", buildFieldTypeMetaType(TypeName.get(Boolean.class), FieldTypeKind.NORMAL),
                  "isFlag", "setFlag", false, null, false, null, null, true, "test_entity_flag_idx", null, false, false),
            // Inherited columns
            tuple("creation_date", "creationDate", buildFieldTypeMetaType(TypeName.get(Instant.class), FieldTypeKind.NORMAL),
                  "getCreationDate", "setCreationDate", false, null, false, null, null, false, null, null, true, false),
            tuple("last_updated_date", "lastUpdatedDate", buildFieldTypeMetaType(TypeName.get(Instant.class), FieldTypeKind.NORMAL),
                  "getLastUpdatedDate", "setLastUpdatedDate", false, null, false, null, null, false, null, null, false, true)
        );
  }

  @Test
  @DisplayName("Compilation should fail if table has no primary key")
  void should_throw_exception_when_table_has_no_primary_key(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getTableParser()
                                    .parse(elements.getTypeElement(TestEntityWithNoPrimaryKey.class.getCanonicalName())))
        .withMessage("There should be at least one primary key defined for the table 'test_entity_no_primary_key'");
  }

  @Test
  @DisplayName("Compilation should fail if nested collection type not frozen")
  void should_throw_exception_when_nested_collection_field_not_frozen(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getTableParser()
                                       .parse(elements.getTypeElement(TestEntityWithNonFrozenNestedCollectionField.class.getCanonicalName())))
        .withMessage("Error while parsing field 'shouldBeFrozenField'");
  }

  @Test
  @DisplayName("Compilation should fail if nested user-defined type not frozen")
  void should_throw_exception_when_nested_udt_field_not_frozen(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getTableParser()
                                       .parse(elements.getTypeElement(TestEntityWithNonFrozenNestedUdtField.class.getCanonicalName())))
        .withMessage("Error while parsing field 'shouldBeFrozenField'");
  }

  @Test
  @DisplayName("Compilation should fail if column type not supported")
  void should_throw_exception_when_column_has_unsupported_type(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getTableParser()
                                       .parse(elements.getTypeElement(TestEntityWithUnsupportedType.class.getCanonicalName())))
        .withMessage("type 'ArrayList' is not supported. Parameter types are supported only on list, set and map");
  }
}
