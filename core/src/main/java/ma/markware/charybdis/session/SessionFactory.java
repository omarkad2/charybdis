package ma.markware.charybdis.session;

import com.datastax.oss.driver.api.core.CqlSession;

public interface SessionFactory {

  CqlSession getSession();

  void shutdown();
}
