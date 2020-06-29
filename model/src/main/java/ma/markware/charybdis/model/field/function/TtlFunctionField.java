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
package ma.markware.charybdis.model.field.function;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import ma.markware.charybdis.model.field.SelectableField;
import ma.markware.charybdis.model.field.metadata.ColumnMetadata;
import ma.markware.charybdis.model.utils.StringUtils;

/**
 * Selectable field, to seek ttl in seconds value.
 *
 * @author Oussama Markad
 */
public class TtlFunctionField implements SelectableField<Integer> {

  private final ColumnMetadata columnMetadata;

  public TtlFunctionField(final ColumnMetadata columnMetadata) {
    this.columnMetadata = columnMetadata;
  }

  @Override
  public Integer deserialize(final Row row) {
    return row.get(resolveAlias(), getFieldClass());
  }

  @Override
  public Class<Integer> getFieldClass() {
    return Integer.class;
  }

  @Override
  public String getName() {
    return "ttl_" + columnMetadata.getName();
  }

  @Override
  public Selector toSelector(boolean useAlias) {
    Selector ttlSelector = Selector.function("ttl", columnMetadata.toSelector(false));
    return useAlias ? ttlSelector.as(resolveAlias()) : ttlSelector;
  }

  private String resolveAlias() {
    return StringUtils.quoteString(getName());
  }
}
