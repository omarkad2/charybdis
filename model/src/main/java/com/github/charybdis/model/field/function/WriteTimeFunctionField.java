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
package com.github.charybdis.model.field.function;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.github.charybdis.model.utils.StringUtils;
import com.github.charybdis.model.field.SelectableField;
import com.github.charybdis.model.field.metadata.ColumnMetadata;

/**
 * Selectable field, to seek write time value.
 *
 * @author Oussama Markad
 */
public class WriteTimeFunctionField implements SelectableField<Long> {

  private final ColumnMetadata columnMetadata;

  public WriteTimeFunctionField(final ColumnMetadata columnMetadata) {
    this.columnMetadata = columnMetadata;
  }

  @Override
  public Long deserialize(final Row row) {
    return row.get(resolveAlias(), getFieldClass());
  }

  @Override
  public Class<Long> getFieldClass() {
    return Long.class;
  }

  @Override
  public String getName() {
    return "writetime_" + columnMetadata.getName();
  }

  @Override
  public Selector toSelector(boolean useAlias) {
    Selector ttlSelector = Selector.function("writetime", columnMetadata.toSelector(false));
    return useAlias ? ttlSelector.as(resolveAlias()) : ttlSelector;
  }

  private String resolveAlias() {
    return StringUtils.quoteString(getName());
  }
}
