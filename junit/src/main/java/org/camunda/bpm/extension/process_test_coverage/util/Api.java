package org.camunda.bpm.extension.process_test_coverage.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

/*
 * Attention, code smells!-) But: it has proven to be quite useful and
 * pragmatic for small projects like Camunda BPM Community Extensions.
 *
 * @author Martin Schimak
 */
public class Api {

    public static class Camunda {

        /**
         * @since Camunda BPM 7.5.0
         */
        public static Api FEATURE_COMPENSATION_BOUNDARY_EVENT = Api.feature("org.camunda.bpm.engine.impl.bpmn.helper.BpmnProperties", "COMPENSATION_BOUNDARY_EVENT");
        /**
         * @since Camunda BPM 7.13
         */
        public static Api FEATURE_CUSTOM_HISTORY_EVENT_HANDLERS = Api.feature("org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl", "setCustomHistoryEventHandlers", List.class);
        public static Api FEATURE_ENABLE_DEFAULT_DB_HISTORY_EVENT_HANDLERS = Api.feature("org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl", "setEnableDefaultDbHistoryEventHandler", boolean.class);

        public static boolean supportsCompensationEventCoverage() {
            return FEATURE_COMPENSATION_BOUNDARY_EVENT.isSupported();
        }

        public static boolean supportsCustomHistoryEventHandlers() {
            return FEATURE_CUSTOM_HISTORY_EVENT_HANDLERS.isSupported();
        }

    }

    private static final Logger log = Logger.getLogger(Api.class.getCanonicalName());

    private final String className;
    private final String memberName;
    private final Class<?>[] parameterTypes;
    private final String signature;

    // maps signatures to a "supported" Boolean, true means supported.
    // false and null means not supported, but null means that a warning
    // was already logged, too. We don't want to warn a thousand times.
    private static final Map<String, Boolean> support = new HashMap<String, Boolean>();

    private Api(final String className, final String memberName, final Class<?>... parameterTypes) {
        this.className = className;
        this.memberName = memberName;
        this.parameterTypes = parameterTypes;
        this.signature = this.signature();
        if (!support.containsKey(this.signature))
            support.put(this.signature, this.supported());
    }

    public static Api feature(final String className) {
        return feature(className, null);
    }

    public static Api feature(final String className, final String memberName, final Class<?>... parameterTypes) {
        return new Api(className, memberName, parameterTypes);
    }

    public static Api feature(final Class cls, final String memberName, final Class<?>... parameterTypes) {
        return feature(cls.getCanonicalName(), memberName, parameterTypes);
    }

    public void fail() {
        this.fail(this.message());
    }

    public void fail(final String message) {
        if (!this.isSupported()) {
            throw new UnsupportedOperationException(message);
        }
    }

    public boolean warn() {
        return this.warn(this.message());
    }

    public boolean warn(final String message) {
        final Boolean supported = support.get(this.signature);
        if (supported != null && !supported) {
            support.put(this.signature, null);
            log.warning(message);
        }
        return this.isSupported();
    }

    public boolean isSupported() {
        final Boolean s = support.get(this.signature);
        return s != null && s;
    }

    public Object invoke(final Object obj, final Object... args) {
        try {
            return this.method().invoke(obj, args);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String message() {
        final StringBuffer buffer = new StringBuffer("Usage of API '").append(this.signature)
                .append("' requested, but not supported by the classes found in classpath.");
        return buffer.toString();
    }

    private String signature() {
        final StringBuffer buffer = new StringBuffer(this.className);
        if (this.memberName != null) {
            buffer.append(".").append(this.memberName);
            buffer.append("(");
            if (this.parameterTypes != null && this.parameterTypes.length > 0) {
                final Iterator<Class<?>> it = Arrays.asList(this.parameterTypes).iterator();
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
        return this.field() != null || this.method() != null;
    }

    private Method method() {
        final Method method;
        try {
            method = Class.forName(this.className).getDeclaredMethod(this.memberName, this.parameterTypes);
            method.setAccessible(true);
        } catch (final ClassNotFoundException e) {
            return null;
        } catch (final NoSuchMethodException e) {
            return null;
        }
        return method;
    }

    private Field field() {
        if (this.parameterTypes == null || this.parameterTypes.length > 0)
            return null;
        final Field field;
        try {
            field = Class.forName(this.className).getDeclaredField(this.memberName);
            field.setAccessible(true);
        } catch (final ClassNotFoundException e) {
            return null;
        } catch (final NoSuchFieldException e) {
            return null;
        }
        return field;
    }

}
