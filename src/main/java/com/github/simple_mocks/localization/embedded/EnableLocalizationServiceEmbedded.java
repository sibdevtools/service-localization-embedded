package com.github.simple_mocks.localization.embedded;

import com.github.simple_mocks.localization.embedded.conf.LocalizationServiceEmbeddedConfig;
import org.springframework.context.annotation.Import;

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
@Import(LocalizationServiceEmbeddedConfig.class)
public @interface EnableLocalizationServiceEmbedded {
}
