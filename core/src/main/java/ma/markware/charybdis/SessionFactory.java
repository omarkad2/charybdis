package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.CqlSession;

public interface SessionFactory {

  CqlSession getSession();
}
