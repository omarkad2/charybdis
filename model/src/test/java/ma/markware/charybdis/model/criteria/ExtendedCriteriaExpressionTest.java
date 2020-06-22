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
package ma.markware.charybdis.model.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.oss.driver.api.core.cql.Row;
import java.util.List;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import org.junit.jupiter.api.Test;

class ExtendedCriteriaExpressionTest {

  @Test
  void test() {
    ColumnMetadata<Integer, Integer> simpleColumnMetadata = new ColumnMetadata<Integer, Integer>() {
      @Override
      public Integer deserialize(final Row row) {
        return row.get(getName(), Integer.class);
      }

      @Override
      public Class<Integer> getFieldClass() {
        return Integer.class;
      }

      @Override
      public Integer serialize(final Integer field) {
        return field;
      }

      @Override
      public String getName() {
        return "simple";
      }
    };

    ExtendedCriteriaExpression extendedCriteriaExpression = simpleColumnMetadata.gte(1)
                                                         .and(simpleColumnMetadata.lt(100));

    List<CriteriaExpression> criterias = extendedCriteriaExpression.getCriterias();
    assertThat(criterias).hasSize(2);

    assertThat(criterias.get(0).getField()).isEqualTo(simpleColumnMetadata);
    assertThat(criterias.get(0).getCriteriaOperator()).isEqualTo(CriteriaOperator.GTE);
    assertThat(criterias.get(0).getSerializedValues()).isEqualTo(new Object[]{ 1 });

    assertThat(criterias.get(1).getField()).isEqualTo(simpleColumnMetadata);
    assertThat(criterias.get(1).getCriteriaOperator()).isEqualTo(CriteriaOperator.LT);
    assertThat(criterias.get(1).getSerializedValues()).isEqualTo(new Object[]{ 100 });
  }
}
