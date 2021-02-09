package com.zoomphant.agent.trace.common.minimal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is a import annotation working with bytebuddy's Advice.customMapping
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Custom {
    /* empty */
}