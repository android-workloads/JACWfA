package com.intel.mttest.loaders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created for future as a draft. It should help to control default
 * values for tests. Also it will eliminate restrictions for naming of
 * parameters.
 * 
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XMLParameter {
	public String defaultValue() default "";
	public boolean mustBeNotEmpty() default true;
}
