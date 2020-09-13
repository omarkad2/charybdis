package ma.markware.charybdis.test.entities;

import java.time.Instant;
import java.util.List;
import ma.markware.charybdis.model.annotation.Column;
import ma.markware.charybdis.model.annotation.CreationDate;
import ma.markware.charybdis.model.annotation.Frozen;
import ma.markware.charybdis.model.annotation.LastUpdatedDate;

public class TestSuperEntity {

  @Column
  Instant date;

  @Column
  @Frozen TestUdt udt;

  @Column
  @Frozen List<String> list;

  @Column
  Boolean flag;

  @Column(name = "creation_date")
  @CreationDate
  private Instant creationDate;

  @Column(name = "last_updated_date")
  @LastUpdatedDate
  private Instant lastUpdatedDate;

  public Instant getDate() {
    return date;
  }

  public void setDate(final Instant date) {
    this.date = date;
  }

  public TestUdt getUdt() {
    return udt;
  }

  public void setUdt(final TestUdt udt) {
    this.udt = udt;
  }

  public List<String> getList() {
    return list;
  }

  public void setList(final List<String> list) {
    this.list = list;
  }

  public Boolean isFlag() {
    return flag;
  }

  public void setFlag(final Boolean flag) {
    this.flag = flag;
  }

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
