package co.edu.javeriana.as.personapp.common.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Anotacion de marca para puertos de la arquitectura hexagonal. */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Port {

	String value() default "";
}
