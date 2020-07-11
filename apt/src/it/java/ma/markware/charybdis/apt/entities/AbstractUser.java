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
package ma.markware.charybdis.apt.entities;

import java.time.Instant;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.CreationDate;
import ma.markware.charybdis.model.annotation.LastUpdatedDate;

public class AbstractUser {

  @Column(name = "creation_date")
  @CreationDate
  private Instant creationDate;

  @Column(name = "last_updated_date")
  @LastUpdatedDate
  private Instant lastUpdatedDate;

  public Instant getCreationDate() {
    return creationDate;
  }

  public Instant getLastUpdatedDate() {
    return lastUpdatedDate;
  }

  public void setCreationDate(final Instant creationDate) {
    this.creationDate = creationDate;
  }

  public void setLastUpdatedDate(final Instant lastUpdatedDate) {
    this.lastUpdatedDate = lastUpdatedDate;
  }
}
