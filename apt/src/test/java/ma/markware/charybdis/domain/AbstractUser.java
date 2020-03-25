package ma.markware.charybdis.domain;

import java.time.Instant;
import ma.markware.charybdis.apt.model.annotation.Column;
import ma.markware.charybdis.apt.model.annotation.CreationDate;
import ma.markware.charybdis.apt.model.annotation.LastUpdatedDate;

public class AbstractUser {

  @Column
  @CreationDate
  private Instant creationDate;

  @Column
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
