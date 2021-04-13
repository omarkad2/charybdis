package ma.markware.charybdis.apt.parser;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import ma.markware.charybdis.apt.AptConfiguration;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptDefaultConfiguration;
import ma.markware.charybdis.apt.CompilationExtension;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.MaterializedViewMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.test.entities.*;
import ma.markware.charybdis.test.entities.invalid.TestEntityViewWithMissingPrimaryKeys;
import ma.markware.charybdis.test.entities.invalid.TestEntityViewWithUnknownColumns;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.time.Instant;
import java.util.*;

import static java.util.Arrays.asList;
import static ma.markware.charybdis.apt.parser.ParserTestHelper.buildFieldTypeMetaType;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({CompilationExtension.class})
class MaterializedViewParserTest {

  @Mock
  private RoundEnvironment roundEnvironment;
  @Mock
  private Messager messager;

  private AptConfiguration configuration;
  private TypeElement testNestedUdtElement;
  private TypeElement testUdtElement;
  private TypeElement testExtraUdtElement;
  private TypeElement testEntityByValueElement;

  @BeforeAll
  @SuppressWarnings("unchecked")
  void setup(Elements elements) {
    MockitoAnnotations.initMocks(this);
    testNestedUdtElement = elements.getTypeElement(TestNestedUdt.class.getCanonicalName());
    testUdtElement = elements.getTypeElement(TestUdt.class.getCanonicalName());
    testExtraUdtElement = elements.getTypeElement(TestExtraUdt.class.getCanonicalName());
    Set elementsAnnotatedWithUdt = new HashSet<>(asList(testNestedUdtElement, testUdtElement, testExtraUdtElement));
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn(elementsAnnotatedWithUdt);
    testEntityByValueElement = elements.getTypeElement(TestEntityByValue.class.getCanonicalName());
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
    // Define Table
    configuration.getTableParser().parse(elements.getTypeElement(TestEntity.class.getCanonicalName()));
  }

  @Test
  void parseMaterializedViewTest() {
    MaterializedViewMetaType materializedViewMetaType = configuration.getMaterializedViewParser().parse(testEntityByValueElement);
    assertThat(materializedViewMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(materializedViewMetaType.getViewName()).isEqualTo("test_entity_by_value");
    assertThat(materializedViewMetaType.getBaseTableName()).isEqualTo("test_entity");
    assertThat(materializedViewMetaType.getColumns())
        .extracting(AbstractFieldMetaType::getSerializationName, AbstractFieldMetaType::getDeserializationName, AbstractFieldMetaType::getFieldType,
            AbstractFieldMetaType::getGetterName, AbstractFieldMetaType::getSetterName, ColumnFieldMetaType::isPartitionKey,
            ColumnFieldMetaType::getPartitionKeyIndex, ColumnFieldMetaType::isClusteringKey, ColumnFieldMetaType::getClusteringKeyIndex,
            ColumnFieldMetaType::getClusteringOrder, ColumnFieldMetaType::isIndexed, ColumnFieldMetaType::getIndexName,
            ColumnFieldMetaType::getSequenceModel, ColumnFieldMetaType::isCreationDate, ColumnFieldMetaType::isLastUpdatedDate)
        .containsExactlyInAnyOrder(
            tuple("enumvalue", "enumValue", buildFieldTypeMetaType(TypeName.get(TestEnum.class), TypeName.get(String.class), FieldTypeMetaType.FieldTypeKind.ENUM,
                false, false, false),
                "getEnumValue", "setEnumValue", true, 0, false, null, null, false, null, null, false, false),
            tuple("id", "id", buildFieldTypeMetaType(TypeName.get(UUID.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                "getId", "setId", false, null, true, 0, ClusteringOrder.ASC, false, null, null, false, false),
            tuple("date", "date", buildFieldTypeMetaType(TypeName.get(Instant.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                "getDate", "setDate", false, null, true, 1, ClusteringOrder.DESC, false, null, null, false, false),
            tuple("udt", "udt", buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeMetaType.FieldTypeKind.UDT,
                true, false, false),
                "getUdt", "setUdt", false, null, true, 2, ClusteringOrder.ASC, false, null, null, false, false),
            tuple("list", "list", buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, String.class), FieldTypeMetaType.FieldTypeKind.LIST,
                true, false,
                buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeMetaType.FieldTypeKind.NORMAL)),
                "getList", "setList", false, null, true, 3, ClusteringOrder.ASC, false, null, null, false, false),
            tuple("se", "se", buildFieldTypeMetaType(ParameterizedTypeName.get(Set.class, Integer.class), FieldTypeMetaType.FieldTypeKind.SET,
                false, false,
                buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeMetaType.FieldTypeKind.NORMAL)),
                "getSe", "setSe", false, null, false, null, null, false, null, null, false, false),
            tuple("map", "map", buildFieldTypeMetaType(ParameterizedTypeName.get(Map.class, String.class, String.class), FieldTypeMetaType.FieldTypeKind.MAP,
                false, false,
                buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeMetaType.FieldTypeKind.NORMAL)),
                "getMap", "setMap", false, null, false, null, null, false, null, null, false, false),
            tuple("nestedlist", "nestedList",
                buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(List.class, Integer.class)),
                    FieldTypeMetaType.FieldTypeKind.LIST, true, true,
                    buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, Integer.class), FieldTypeMetaType.FieldTypeKind.LIST,
                        false, false,
                        buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeMetaType.FieldTypeKind.NORMAL))),
                "getNestedList", "setNestedList", false, null, false, null, null, false, null, null, false, false),
            tuple("nestedset", "nestedSet",
                buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(Set.class), ParameterizedTypeName.get(List.class, Integer.class)),
                    FieldTypeMetaType.FieldTypeKind.SET, false, true,
                    buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, Integer.class), FieldTypeMetaType.FieldTypeKind.LIST,
                        true, false,
                        buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeMetaType.FieldTypeKind.NORMAL))),
                "getNestedSet", "setNestedSet", false, null, false, null, null, false, null, null, false, false),
            tuple("nestedmap", "nestedMap",
                buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), ParameterizedTypeName.get(Map.class, Integer.class, String.class)),
                    FieldTypeMetaType.FieldTypeKind.MAP, false, true,
                    buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                    buildFieldTypeMetaType(ParameterizedTypeName.get(Map.class, Integer.class, String.class), FieldTypeMetaType.FieldTypeKind.MAP,
                        true, false,
                        buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                        buildFieldTypeMetaType(TypeName.get(String.class), FieldTypeMetaType.FieldTypeKind.NORMAL))),
                "getNestedMap", "setNestedMap", false, null, false, null, null, false, null, null, false, false),
            tuple("enumlist", "enumList", buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, TestEnum.class), ParameterizedTypeName.get(List.class, String.class),
                FieldTypeMetaType.FieldTypeKind.LIST, false, true, false,
                buildFieldTypeMetaType(TypeName.get(TestEnum.class), TypeName.get(String.class), FieldTypeMetaType.FieldTypeKind.ENUM,
                    false, false, false)),
                "getEnumList", "setEnumList", false, null, false, null, null, false, null, null, false, false),
            tuple("enummap", "enumMap", buildFieldTypeMetaType(ParameterizedTypeName.get(Map.class, Integer.class, TestEnum.class),
                ParameterizedTypeName.get(Map.class, Integer.class, String.class),
                FieldTypeMetaType.FieldTypeKind.MAP, false, true, false,
                buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                buildFieldTypeMetaType(TypeName.get(TestEnum.class), TypeName.get(String.class), FieldTypeMetaType.FieldTypeKind.ENUM,
                    false, false, false)),
                "getEnumMap", "setEnumMap", false, null, false, null, null, false, null, null, false, false),
            tuple("enumnestedlist", "enumNestedList",
                buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(Set.class, TestEnum.class)),
                    ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(Set.class, String.class)),
                    FieldTypeMetaType.FieldTypeKind.LIST, false, true, true,
                    buildFieldTypeMetaType(ParameterizedTypeName.get(Set.class, TestEnum.class), ParameterizedTypeName.get(Set.class, String.class),
                        FieldTypeMetaType.FieldTypeKind.SET, true, true, false,
                        buildFieldTypeMetaType(TypeName.get(TestEnum.class), TypeName.get(String.class), FieldTypeMetaType.FieldTypeKind.ENUM,
                            false, false, false))),
                "getEnumNestedList", "setEnumNestedList", false, null, false, null, null, false, null, null, false, false),
            tuple("udtlist", "udtList", buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, TestUdt.class), ParameterizedTypeName.get(List.class, UdtValue.class),
                FieldTypeMetaType.FieldTypeKind.LIST, false, true, false,
                buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeMetaType.FieldTypeKind.UDT,
                    true, false, false)),
                "getUdtList", "setUdtList", false, null, false, null, null, false, null, null, false, false),
            tuple("extraudt", "extraUdt", buildFieldTypeMetaType(TypeName.get(TestExtraUdt.class), TypeName.get(UdtValue.class), FieldTypeMetaType.FieldTypeKind.UDT,
                false, false, false),
                "getExtraUdt", "setExtraUdt", false, null, false, null, null, false, null, null, false, false),
            tuple("udtset", "udtSet", buildFieldTypeMetaType(ParameterizedTypeName.get(Set.class, TestUdt.class), ParameterizedTypeName.get(Set.class, UdtValue.class),
                FieldTypeMetaType.FieldTypeKind.SET, false, true, false,
                buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeMetaType.FieldTypeKind.UDT,
                    true, false, false)),
                "getUdtSet", "setUdtSet", false, null, false, null, null, false, null, null, false, false),
            tuple("udtmap", "udtMap", buildFieldTypeMetaType(ParameterizedTypeName.get(Map.class, Integer.class, TestUdt.class),
                ParameterizedTypeName.get(Map.class, Integer.class, UdtValue.class),
                FieldTypeMetaType.FieldTypeKind.MAP, false, true, false,
                buildFieldTypeMetaType(TypeName.get(Integer.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeMetaType.FieldTypeKind.UDT,
                    true, false, false)),
                "getUdtMap", "setUdtMap", false, null, false, null, null, false, null, null, false, false),
            tuple("udtnestedlist", "udtNestedList",
                buildFieldTypeMetaType(ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(List.class, TestUdt.class)),
                    ParameterizedTypeName.get(ClassName.get(List.class), ParameterizedTypeName.get(List.class, UdtValue.class)),
                    FieldTypeMetaType.FieldTypeKind.LIST, false, true, true,
                    buildFieldTypeMetaType(ParameterizedTypeName.get(List.class, TestUdt.class), ParameterizedTypeName.get(List.class, UdtValue.class),
                        FieldTypeMetaType.FieldTypeKind.LIST, true, true, false,
                        buildFieldTypeMetaType(TypeName.get(TestUdt.class), TypeName.get(UdtValue.class), FieldTypeMetaType.FieldTypeKind.UDT,
                            false, false, false))),
                "getUdtNestedList", "setUdtNestedList", false, null, false, null, null, false, null, null, false, false),
            tuple("flag", "flag", buildFieldTypeMetaType(TypeName.get(Boolean.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                "isFlag", "setFlag", false, null, false, null, null, false, null, null, false, false),
            // Inherited columns
            tuple("creation_date", "creationDate", buildFieldTypeMetaType(TypeName.get(Instant.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                "getCreationDate", "setCreationDate", false, null, false, null, null, false, null, null, true, false),
            tuple("last_updated_date", "lastUpdatedDate", buildFieldTypeMetaType(TypeName.get(Instant.class), FieldTypeMetaType.FieldTypeKind.NORMAL),
                "getLastUpdatedDate", "setLastUpdatedDate", false, null, false, null, null, false, null, null, false, true)
        );
  }

  @Test
  @DisplayName("Compilation should fail if view has unknown columns")
  void should_throw_exception_when_view_has_unknown_columns(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getMaterializedViewParser()
            .parse(elements.getTypeElement(TestEntityViewWithUnknownColumns.class.getCanonicalName())))
        .withMessage("Columns 'unknowncolumn' found in materialized view 'test_entity_with_unknown_columns' don't exist in base table 'test_entity'");
  }

  @Test
  @DisplayName("Compilation should fail if view is missing primary keys")
  void should_throw_exception_when_column_is_missing_primary_keys(Elements elements) {
    assertThatExceptionOfType(CharybdisParsingException.class)
        .isThrownBy(() -> configuration.getMaterializedViewParser()
            .parse(elements.getTypeElement(TestEntityViewWithMissingPrimaryKeys.class.getCanonicalName())))
        .withMessage("Primary keys ['date, udt, list'] from base table 'test_entity' are missing in view 'test_entity_with_missing_primary_keys'");
  }
}
