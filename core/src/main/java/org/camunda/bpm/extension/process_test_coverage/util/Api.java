package org.camunda.bpm.extension.process_test_coverage.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/*
 * Attention, code smells!-) But: it has proven to be quite useful and
 * pragmatic for small projects like Camunda BPM Community Extensions.
 *
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Api {

  public static class Camunda {

    /**
     * @since Camunda BPM 7.5.0
     */
    public static boolean supportsCompensationEventCoverage() {
      return Api.feature("org.camunda.bpm.engine.impl.bpmn.helper.BpmnProperties", "COMPENSATION_BOUNDARY_EVENT").isSupported();
    }

  }

  private static final Logger log = Logger.getLogger(Api.class.getCanonicalName());

  private String className;
  private String memberName;
  private Class<?>[] parameterTypes;
  private String signature;

  // maps signatures to a "supported" Boolean, true means supported.
  // false and null means not supported, but null means that a warning
  // was already logged, too. We don't want to warn a thousand times.
  private static Map<String, Boolean> support = new HashMap<String, Boolean>();

  private Api(String className, String memberName, Class<?>... parameterTypes) {
    this.className = className;
    this.memberName = memberName;
    this.parameterTypes = parameterTypes;
    this.signature = signature();
    if (!support.containsKey(signature))
      support.put(signature, supported());
  }

  public static Api feature(String className) {
    return feature(className, null);
  }

  public static Api feature(String className, String memberName, Class<?>... parameterTypes) {
    return new Api(className, memberName, parameterTypes);
  }

  public static Api feature(Class cls, String memberName, Class<?>... parameterTypes) {
    return feature(cls.getCanonicalName(), memberName, parameterTypes);
  }

  public void fail() {
    fail(message());
  }

  public void fail(String message) {
    if (!isSupported()) {
      throw new UnsupportedOperationException(message);
    }
  }

  public boolean warn() {
    return warn(message());
  }

  public boolean warn(String message) {
    Boolean supported = support.get(signature);
    if (supported != null && !supported) {
      support.put(signature, null);
      log.warning(message);
    }
    return isSupported();
  }

  public boolean isSupported() {
    Boolean s = support.get(signature);
    return s != null && s;
  }

  public Object invoke(Object obj, Object... args) {
    try {
      return method().invoke(obj, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String message() {
    StringBuffer buffer = new StringBuffer("Usage of API '").append(signature)
      .append("' requested, but not supported by the classes found in classpath.");
    return buffer.toString();
  }

  private String signature() {
    StringBuffer buffer = new StringBuffer(className);
    if (memberName != null) {
      buffer.append(".").append(memberName);
      buffer.append("(");
      if (parameterTypes != null && parameterTypes.length > 0) {
        Iterator<Class<?>> it = Arrays.asList(parameterTypes).iterator();
        while (it.hasNext()) {
          buffer.append(it.next().getSimpleName());
          if (it.hasNext())
            buffer.append(", ");
        }
      }
      buffer.append(")");
    }
    return buffer.toString();
  }

  private boolean supported() {
    return field() != null || method() != null;
  }

  private Method method(){
    Method method;
    try {
      method = Class.forName(className).getDeclaredMethod(memberName, parameterTypes);
      method.setAccessible(true);
    } catch (ClassNotFoundException e) {
      return null;
    } catch (NoSuchMethodException e) {
      return null;
    }
    return method;
  }

  private Field field() {
    if (parameterTypes == null || parameterTypes.length > 0)
      return null;
    Field field;
    try {
      field = Class.forName(className).getDeclaredField(memberName);
      field.setAccessible(true);
    } catch (ClassNotFoundException e) {
      return null;
    } catch (NoSuchFieldException e) {
      return null;
    }
    return field;
  }

}
