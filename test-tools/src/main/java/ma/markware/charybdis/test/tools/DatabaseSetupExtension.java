package ma.markware.charybdis.test.tools;

import com.datastax.oss.driver.api.core.CqlSession;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class DatabaseSetupExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

  private DockerizedCassandra dockerizedCassandra;
  private static final Set<Class<?>> SUPPORTED_PARAMETERS;

  static {
    SUPPORTED_PARAMETERS = new HashSet<>();
    SUPPORTED_PARAMETERS.add(CqlSession.class);
    SUPPORTED_PARAMETERS.add(int.class);
  }

  @Override
  public void beforeAll(final ExtensionContext extensionContext) throws Exception {
    dockerizedCassandra = new DockerizedCassandra();
    dockerizedCassandra.start();
    System.setProperty("datastax-java-driver.basic.request.timeout", "10 minutes");
    System.setProperty("datastax-java-driver.basic.contact-points.0", "127.0.0.1:" + dockerizedCassandra.getPort());
    System.setProperty("datastax-java-driver.basic.load-balancing-policy.local-datacenter", "datacenter1");
  }

  @Override
  public void afterAll(final ExtensionContext extensionContext) throws Exception {
    dockerizedCassandra.close();
    System.clearProperty("datastax-java-driver.basic.request.timeout");
    System.clearProperty("datastax-java-driver.basic.contact-points.0");
    System.clearProperty("datastax-java-driver.basic.load-balancing-policy.local-datacenter");
  }

  @Override
  public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return SUPPORTED_PARAMETERS.contains(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    if (parameterContext.getParameter().getType().equals(CqlSession.class)) {
      return dockerizedCassandra.getSession();
    }
    if (parameterContext.getParameter().getType().equals(int.class)) {
      return dockerizedCassandra.getPort();
    }
    return null;
  }
}
