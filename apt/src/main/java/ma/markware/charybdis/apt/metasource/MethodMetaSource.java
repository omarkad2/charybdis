package ma.markware.charybdis.apt.metasource;

import java.util.List;

public class MethodMetaSource {

  private String methodName;
  private String returnType;
  private List<String> returnSubTypes;
  private String parameterType;
  private List<String> parameterSubType;

  public MethodMetaSource(final String methodName, final String returnType, final List<String> returnSubTypes, final String parameterType,
      final List<String> parameterSubType) {
    this.methodName = methodName;
    this.returnType = returnType;
    this.returnSubTypes = returnSubTypes;
    this.parameterType = parameterType;
    this.parameterSubType = parameterSubType;
  }

  public String getMethodName() {
    return methodName;
  }

  public String getReturnType() {
    return returnType;
  }

  public List<String> getReturnSubTypes() {
    return returnSubTypes;
  }

  public String getParameterType() {
    return parameterType;
  }

  public List<String> getParameterSubType() {
    return parameterSubType;
  }
}
