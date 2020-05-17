package ma.markware.charybdis.test.tools;

import com.datastax.oss.driver.api.core.CqlSession;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class DatabaseSetupExtension implements BeforeAllCallback, AfterAllCallback, ParameterResolver {

  private DockerizedCassandra dockerizedCassandra;

  @Override
  public void beforeAll(final ExtensionContext extensionContext) throws Exception {
    dockerizedCassandra = new DockerizedCassandra();
    dockerizedCassandra.start();
  }

  @Override
  public void afterAll(final ExtensionContext extensionContext) throws Exception {
    dockerizedCassandra.close();
  }

  @Override
  public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getType()
                           .equals(CqlSession.class);
  }

  @Override
  public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return dockerizedCassandra.getSession();
  }
}
