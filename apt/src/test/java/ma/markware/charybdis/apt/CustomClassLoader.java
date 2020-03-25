package ma.markware.charybdis.apt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.commons.lang.exception.NestableRuntimeException;

class CustomClassLoader extends URLClassLoader {

  private static final CustomClassLoader INSTANCE = new CustomClassLoader();

  static CustomClassLoader getInstance() {
    return INSTANCE;
  }

  private CustomClassLoader() {

    super(getAppClassLoaderUrls(), null);
  }

  private static URL[] getAppClassLoaderUrls() {

    return getURLs(CustomClassLoader.class.getClassLoader());
  }

  private static URL[] getURLs(ClassLoader classLoader) {

    Class<?> clazz = classLoader.getClass();

    try {
      Field field;
      field = clazz.getDeclaredField("ucp");
      field.setAccessible(true);

      Object urlClassPath = field.get(classLoader);

      Method method = urlClassPath.getClass()
                                  .getDeclaredMethod("getURLs");
      method.setAccessible(true);

      return (URL[]) method.invoke(urlClassPath, new Object[]{});

    } catch (Exception e) {
      throw new NestableRuntimeException(e);
    }
  }

}
