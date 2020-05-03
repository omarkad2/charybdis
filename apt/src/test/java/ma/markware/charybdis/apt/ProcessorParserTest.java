package ma.markware.charybdis.apt;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.domain.Address;
import ma.markware.charybdis.apt.domain.Country;
import ma.markware.charybdis.apt.domain.KeyspaceDefinition;
import ma.markware.charybdis.apt.domain.RoleEnum;
import ma.markware.charybdis.apt.domain.User;
import ma.markware.charybdis.apt.metatype.AbstractFieldMetaType;
import ma.markware.charybdis.apt.metatype.ColumnFieldMetaType;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.apt.metatype.TableMetaType;
import ma.markware.charybdis.apt.metatype.TypeDetail;
import ma.markware.charybdis.apt.metatype.TypeDetail.TypeDetailEnum;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.option.ClusteringOrder;
import ma.markware.charybdis.model.option.Replication;
import ma.markware.charybdis.model.option.SequenceModelEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({CompilationExtension.class, MockitoExtension.class})
class ProcessorParserTest {

  @Mock
  private RoundEnvironment roundEnvironment;
  
  private AptContext aptContext = new AptContext();
  private AptDefaultConfiguration configuration = new AptDefaultConfiguration();
  private Types types;
  private TypeElement keyspaceTypeElement;
  private TypeElement addressUdtElement, countryUdtElement;
  private TypeElement userTableElement;

  @BeforeEach
  void setup(Elements elements, Types types) {
    this.types = types;
    keyspaceTypeElement = elements.getTypeElement(KeyspaceDefinition.class.getCanonicalName());
    addressUdtElement = elements.getTypeElement(Address.class.getCanonicalName());
    countryUdtElement = elements.getTypeElement(Country.class.getCanonicalName());
    userTableElement = elements.getTypeElement(User.class.getCanonicalName());
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn((Set) new HashSet<>(asList(addressUdtElement, countryUdtElement)));
    aptContext.init(roundEnvironment, configuration);
  }

  @Test
  void parseKeyspaceTest() {
    KeyspaceMetaType keyspaceMetaType = configuration.getKeyspaceParser().parse(keyspaceTypeElement, types, aptContext);
    assertThat(keyspaceMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(keyspaceMetaType.getReplication()).isEqualTo(Replication.DEFAULT_REPLICATION);
  }

  @Test
  void parseUdtTest() {
    configuration.getKeyspaceParser().parse(keyspaceTypeElement, types, aptContext);
    UdtMetaType udtMetaType = configuration.getUdtParser().parse(addressUdtElement, types, aptContext);
    assertThat(udtMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(udtMetaType.getUdtName()).isEqualTo("address");
    assertThat(udtMetaType.getUdtFields())
        .extracting(UdtFieldMetaType::getUdtFieldName, AbstractFieldMetaType::getFieldName, AbstractFieldMetaType::getFieldType,
                    AbstractFieldMetaType::getFieldSubTypes, AbstractFieldMetaType::getGetterName, AbstractFieldMetaType::getSetterName)
        .containsExactlyInAnyOrder(
            tuple("number", "number", buildTypeDetail(int.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getNumber", "setNumber"),
            tuple("street", "street", buildTypeDetail(String.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getStreet", "setStreet"),
            tuple("city", "city", buildTypeDetail(String.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getCity", "setCity"),
            tuple("country", "country", buildTypeDetail(Country.class.getCanonicalName(), TypeDetailEnum.UDT), EMPTY_LIST,
                  "getCountry", "setCountry")
        );
  }

  @Test
  void parseTableTest() {
    configuration.getKeyspaceParser().parse(keyspaceTypeElement, types, aptContext);
    TableMetaType tableMetaType = configuration.getTableParser().parse(userTableElement, types, aptContext);
    assertThat(tableMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(tableMetaType.getTableName()).isEqualTo("user");
    assertThat(tableMetaType.getColumns())
        .extracting(ColumnFieldMetaType::getColumnName, ColumnFieldMetaType::isPartitionKey, ColumnFieldMetaType::getPartitionKeyIndex,
                    ColumnFieldMetaType::isClusteringKey, ColumnFieldMetaType::getClusteringKeyIndex, ColumnFieldMetaType::getClusteringOrder,
                    ColumnFieldMetaType::isIndexed, ColumnFieldMetaType::getIndexName, ColumnFieldMetaType::getSequenceModel,
                    ColumnFieldMetaType::isCreationDate, ColumnFieldMetaType::isLastUpdatedDate, AbstractFieldMetaType::getFieldName,
                    AbstractFieldMetaType::getFieldType, AbstractFieldMetaType::getFieldSubTypes, AbstractFieldMetaType::getGetterName,
                    AbstractFieldMetaType::getSetterName)
        .containsExactlyInAnyOrder(
            tuple("id", true, 0, false, null, null, false, null, SequenceModelEnum.UUID, false, false,
                  "id", buildTypeDetail(UUID.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getId", "setId"),
            tuple("fullname", false, null, true, 0, ClusteringOrder.ASC, false, null, null, false, false,
                  "fullname", buildTypeDetail(String.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getFullname", "setFullname"),
            tuple("joining_date", false, null, true, 1, ClusteringOrder.DESC, false, null, null, false, false,
                  "joiningDate", buildTypeDetail(Instant.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getJoiningDate", "setJoiningDate"),
            tuple("age", false, null, false, null, null, false, null, null, false, false,
                  "age", buildTypeDetail(int.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getAge", "setAge"),
            tuple("email", false, null, false, null, null, false, null, null, false, false,
                  "email", buildTypeDetail(String.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getEmail", "setEmail"),
            tuple("password", false, null, false, null, null, false, null, null, false, false,
                  "password", buildTypeDetail(String.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getPassword", "setPassword"),
            tuple("address", false, null, false, null, null, false, null, null, false, false,
                  "address", buildTypeDetail(Address.class.getCanonicalName(), TypeDetail.TypeDetailEnum.UDT), EMPTY_LIST,
                  "getAddress", "setAddress"),
            tuple("followers", false, null, false, null, null, false, null, null, false, false,
                  "followers", buildTypeDetail(List.class.getCanonicalName(), TypeDetail.TypeDetailEnum.LIST),
                  Collections.singletonList(buildTypeDetail(UUID.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL)),
                  "getFollowers", "setFollowers"),
            tuple("role", false, null, false, null, null, true, "user_role_idx", null, false, false,
                  "role", buildTypeDetail(RoleEnum.class.getCanonicalName(), TypeDetail.TypeDetailEnum.ENUM), EMPTY_LIST,
                  "getRole", "setRole"),
            tuple("access_logs", false, null, false, null, null, false, null, null, false, false,
                  "accessLogs", buildTypeDetail(Set.class.getCanonicalName(), TypeDetail.TypeDetailEnum.SET),
                  Collections.singletonList(buildTypeDetail(Instant.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL)),
                  "getAccessLogs", "setAccessLogs"),
            tuple("metadata", false, null, false, null, null, false, null, null, false, false,
                  "metadata", buildTypeDetail(Map.class.getCanonicalName(), TypeDetail.TypeDetailEnum.MAP),
                  asList(buildTypeDetail(String.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL),
                         buildTypeDetail(String.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL)),
                  "getMetadata", "setMetadata"),
            // Inherited columns
            tuple("creation_date", false, null, false, null, null, false, null, null, true, false,
                  "creationDate", buildTypeDetail(Instant.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getCreationDate", "setCreationDate"),
            tuple("last_updated_date", false, null, false, null, null, false, null, null, false, true,
                  "lastUpdatedDate", buildTypeDetail(Instant.class.getCanonicalName(), TypeDetail.TypeDetailEnum.NORMAL), EMPTY_LIST,
                  "getLastUpdatedDate", "setLastUpdatedDate")
        );
  }

  private TypeDetail buildTypeDetail(String typeCanonicalName, TypeDetail.TypeDetailEnum typeDetailEnum) {
    TypeDetail typeDetail = new TypeDetail();
    typeDetail.setTypeCanonicalName(typeCanonicalName);
    typeDetail.setTypeDetailEnum(typeDetailEnum);
    return typeDetail;
  }
}