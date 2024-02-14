package kpan.b_line_break.config.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ConfigAnnotations {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Id {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ConfigOrder {
        int value() default 0;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface RangeInt {
        int minValue() default Integer.MIN_VALUE;

        int maxValue() default Integer.MAX_VALUE;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface RangeLong {
        long minValue() default Long.MIN_VALUE;

        long maxValue() default Long.MAX_VALUE;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface RangeFloat {
        float minValue() default Float.NEGATIVE_INFINITY;

        float maxValue() default Float.POSITIVE_INFINITY;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface RangeDouble {
        double minValue() default Double.NEGATIVE_INFINITY;

        double maxValue() default Double.POSITIVE_INFINITY;
    }

}
