package ma.markware.charybdis.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.internal.querybuilder.lhs.ColumnLeftOperand;
import com.datastax.oss.driver.internal.querybuilder.relation.DefaultRelation;
import com.datastax.oss.driver.internal.querybuilder.select.ColumnSelector;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import ma.markware.charybdis.query.SelectQuery;
import ma.markware.charybdis.query.clause.WhereClause;
import ma.markware.charybdis.test.entities.TestEnum;
import ma.markware.charybdis.test.entities.TestNestedUdt;
import ma.markware.charybdis.test.entities.TestUdt;
import ma.markware.charybdis.test.metadata.TestEntity_Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SelectImplTest {

  @Mock
  private CqlSession session;

  private SelectImpl selectImpl;
  private TestUdt udt1, udt2;

  @BeforeEach
  void setup() {
    selectImpl = new SelectImpl(session);

    TestNestedUdt nestedUdt1 = new TestNestedUdt("nestedName1", "nestedValue1", Arrays.asList(12, 13));
    TestNestedUdt nestedUdt2 = new TestNestedUdt("nestedName2", "nestedValue2", Arrays.asList(14, 15, 16));
    TestNestedUdt nestedUdt3 = new TestNestedUdt("nestedName3", "nestedValue3", Arrays.asList(17, 18));
    TestNestedUdt nestedUdt4 = new TestNestedUdt("nestedName4", "nestedValue4", Arrays.asList(19, 20, 21));
    TestNestedUdt nestedUdt5 = new TestNestedUdt("nestedName5", "nestedValue5", Arrays.asList(22, 23, 24));
    udt1 = new TestUdt(1, "test1", Arrays.asList(nestedUdt1, nestedUdt2), Collections.singleton(Arrays.asList(nestedUdt3, nestedUdt4)),
                       ImmutableMap.of(TestEnum.TYPE_A, Arrays.asList(nestedUdt1, nestedUdt5), TestEnum.TYPE_B, Collections.singletonList(nestedUdt4)),
                       new TestNestedUdt());
    udt2 = new TestUdt(2, "test2", Arrays.asList(nestedUdt2, nestedUdt3, nestedUdt4), Collections.singleton(Collections.singletonList(nestedUdt5)),
                       ImmutableMap.of(TestEnum.TYPE_A, Arrays.asList(nestedUdt5, nestedUdt3), TestEnum.TYPE_B, Arrays.asList(nestedUdt1, nestedUdt2, nestedUdt3)),
                       nestedUdt1);
  }

  @Test
  void select() {
    selectImpl.select(TestEntity_Table.id, TestEntity_Table.udt);

    SelectQuery selectQuery = selectImpl.getSelectQuery();
    assertThat(selectQuery.getSelectors()).extracting(selector -> ((ColumnSelector) selector).getColumnId())
                                          .containsExactlyInAnyOrder(
        CqlIdentifier.fromCql(TestEntity_Table.id.getName()),
        CqlIdentifier.fromCql(TestEntity_Table.udt.getName()));
  }

  @Test
  void selectDistinct() {
    selectImpl.selectDistinct(TestEntity_Table.id);

    SelectQuery selectQuery = selectImpl.getSelectQuery();
    assertThat(selectQuery.isDistinct()).isTrue();
    assertThat(selectQuery.getSelectors()).extracting(selector -> ((ColumnSelector) selector).getColumnId())
                                          .containsExactlyInAnyOrder(
                                              CqlIdentifier.fromCql(TestEntity_Table.id.getName()));
  }

  @Test
  void selectFrom() {
    selectImpl.selectFrom(TestEntity_Table.test_entity);

    SelectQuery selectQuery = selectImpl.getSelectQuery();

    assertThat(selectQuery.getKeyspace()).isEqualTo(TestEntity_Table.KEYSPACE_NAME);
    assertThat(selectQuery.getTable()).isEqualTo(TestEntity_Table.TABLE_NAME);

    assertThat(selectQuery.getSelectors()).isEqualTo(SelectQuery.SELECT_ALL);
  }

  @Test
  void from() {
    selectImpl.selectFrom(TestEntity_Table.test_entity);

    SelectQuery selectQuery = selectImpl.getSelectQuery();

    assertThat(selectQuery.getKeyspace()).isEqualTo(TestEntity_Table.KEYSPACE_NAME);
    assertThat(selectQuery.getTable()).isEqualTo(TestEntity_Table.TABLE_NAME);
  }

  @Test
  void where() {
    UUID uuid = UUID.randomUUID();
    selectImpl.selectFrom(TestEntity_Table.test_entity)
              .where(TestEntity_Table.id.eq(uuid));

    SelectQuery selectQuery = selectImpl.getSelectQuery();

    assertThat(selectQuery.getWhereClauses())
        .extracting(whereClause -> ((ColumnLeftOperand) ((DefaultRelation) whereClause.getRelation()).getLeftOperand()).getColumnId(),
                    whereClause -> ((DefaultRelation) whereClause.getRelation()).getOperator(), WhereClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.id.getName()), "=", new Object[] { uuid })
        );
  }

  @Test
  void and() {
    UUID uuid = UUID.randomUUID();
    Instant now = Instant.now();
    String mapKey = "key0";
    List<Integer> nestedSetValue = Collections.singletonList(10);
    selectImpl.selectFrom(TestEntity_Table.test_entity)
              .where(TestEntity_Table.id.eq(uuid))
              .and(TestEntity_Table.date.lt(now))
              .and(TestEntity_Table.udt.in(udt1, udt2))
              .and(TestEntity_Table.list.isNotNull())
              .and(TestEntity_Table.map.containsKey(mapKey))
              .and(TestEntity_Table.nestedSet.contains(nestedSetValue))
              .and(TestEntity_Table.enumMap.eq(ImmutableMap.of(0, TestEnum.TYPE_A, 1, TestEnum.TYPE_B)));

    SelectQuery selectQuery = selectImpl.getSelectQuery();

    assertThat(selectQuery.getWhereClauses())
        .extracting(whereClause -> ((ColumnLeftOperand) ((DefaultRelation) whereClause.getRelation()).getLeftOperand()).getColumnId(),
                     whereClause -> ((DefaultRelation) whereClause.getRelation()).getOperator(), WhereClause::getBindValues)
        .containsExactlyInAnyOrder(
            tuple(CqlIdentifier.fromCql(TestEntity_Table.id.getName()), "=", new Object[] { uuid }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.date.getName()), "<", new Object[] { now }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.udt.getName()), " IN ", new Object[] { TestEntity_Table.udt.serialize(udt1), TestEntity_Table.udt.serialize(udt2) }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.list.getName()), " IS NOT NULL ", null),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.map.getName()), " CONTAINS KEY ", new Object[] { mapKey }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.nestedSet.getName()), " CONTAINS ", new Object[] { nestedSetValue }),
            tuple(CqlIdentifier.fromCql(TestEntity_Table.enumMap.getName()), "=", new Object[] { ImmutableMap.of(0, TestEnum.TYPE_A.name(), 1, TestEnum.TYPE_B.name()) })
        );
  }

  @Test
  void orderBy() {
    UUID uuid = UUID.randomUUID();
    selectImpl.selectFrom(TestEntity_Table.test_entity)
              .where(TestEntity_Table.id.eq(uuid))
              .orderBy(TestEntity_Table.date.desc());

    SelectQuery selectQuery = selectImpl.getSelectQuery();

    assertThat(selectQuery.getOrderings()).containsEntry(TestEntity_Table.date.getName(), ClusteringOrder.DESC);
  }

  @Test
  void limit() {
    selectImpl.selectFrom(TestEntity_Table.test_entity)
              .limit(100);

    SelectQuery selectQuery = selectImpl.getSelectQuery();

    assertThat(selectQuery.getLimit()).isEqualTo(100);
  }

  @Test
  void allowFiltering() {
    selectImpl.selectFrom(TestEntity_Table.test_entity)
              .where(TestEntity_Table.udt.eq(udt1))
              .allowFiltering();

    SelectQuery selectQuery = selectImpl.getSelectQuery();

    assertThat(selectQuery.isAllowFiltering()).isTrue();
  }
}
