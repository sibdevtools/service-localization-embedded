package com.github.simple_mocks.localization.embedded;

import java.lang.annotation.*;

/**
 * Enabler for embedded localization service implementation
 *
 * @author sibmaks
 * @since 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnableLocalizationServiceEmbedded {
}
