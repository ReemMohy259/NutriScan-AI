package gov.iti.jets.NutriScan.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    int limit();

    int durationSeconds() default 60;

    RateLimitKeyType keyType() default RateLimitKeyType.USER;
}
