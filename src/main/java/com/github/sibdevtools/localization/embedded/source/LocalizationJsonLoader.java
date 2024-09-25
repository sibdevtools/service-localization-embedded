package com.github.sibdevtools.localization.embedded.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sibdevtools.localization.api.dto.LocalizationSourceId;
import com.github.sibdevtools.localization.api.dto.LocalizedText;
import com.github.sibdevtools.localization.embedded.exception.LocalizationLoadingException;
import com.github.sibdevtools.localization.mutable.api.dto.LocalizationKey;
import com.github.sibdevtools.localization.mutable.api.rq.AddLocalizationsRq;
import com.github.sibdevtools.localization.mutable.api.service.MutableLocalizationService;
import com.github.sibdevtools.localization.mutable.api.source.LocalizationJsonSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sibmaks
 * @since 0.0.3
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "service.localization.mode", havingValue = "EMBEDDED")
public class LocalizationJsonLoader {
    private final MutableLocalizationService mutableLocalizationService;
    private final ObjectMapper objectMapper;
    private final ApplicationContext context;
    private final ResourceLoader resourceLoader;

    public LocalizationJsonLoader(MutableLocalizationService mutableLocalizationService,
                                  @Qualifier("localizationServiceObjectMapper")
                                  ObjectMapper objectMapper,
                                  ApplicationContext context,
                                  ResourceLoader resourceLoader) {
        this.mutableLocalizationService = mutableLocalizationService;
        this.objectMapper = objectMapper;
        this.context = context;
        this.resourceLoader = resourceLoader;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        var beans = context.getBeanNamesForAnnotation(LocalizationJsonSource.class);
        for (var bean : beans) {
            var sources = context.findAllAnnotationsOnBean(bean, LocalizationJsonSource.class, true);
            for (var source : sources) {
                loadLocalizations(source);
            }
        }
    }

    private void loadLocalizations(LocalizationJsonSource source) {
        var resource = resourceLoader.getResource(source.path());
        var localizationId = new LocalizationSourceId(source.systemCode(), source.kindCode());
        var locale = Locale.of(source.iso3Code());
        Map<String, String> jsonLocalizations;
        try {
            jsonLocalizations = (Map<String, String>) objectMapper.readValue(resource.getInputStream(), Map.class);
        } catch (IOException e) {
            throw new LocalizationLoadingException("Source read exception", e);
        }
        var localizations = jsonLocalizations.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        it -> new LocalizationKey(it.getKey(), locale),
                        it -> new LocalizedText(it.getValue())
                ));
        mutableLocalizationService.addLocalizations(
                new AddLocalizationsRq(localizationId, localizations)
        );
    }
}
