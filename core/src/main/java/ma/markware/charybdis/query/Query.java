package ma.markware.charybdis.query;

import com.datastax.oss.driver.api.core.cql.ResultSet;

public interface Query {

  ResultSet execute();
}
