package com.github.simple_mocks.localization.embedded.conf;


import com.github.simple_mocks.localization.embedded.EnableLocalizationServiceEmbedded;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class LocalizationServiceEmbeddedCondition implements Condition {
    @Override
    public boolean matches(@Nonnull ConditionContext context,
                           @Nonnull AnnotatedTypeMetadata metadata) {
        var beanFactory = Objects.requireNonNull(context.getBeanFactory());
        return beanFactory
                .getBeanNamesForAnnotation(EnableLocalizationServiceEmbedded.class).length > 0;

    }
}
