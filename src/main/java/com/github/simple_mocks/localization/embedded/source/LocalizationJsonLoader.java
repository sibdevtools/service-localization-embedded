package com.github.simple_mocks.localization.embedded.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.simple_mocks.localization.embedded.conf.LocalizationServiceEmbeddedCondition;
import com.github.simple_mocks.localization.embedded.exception.LocalizationLoadingException;
import com.github.simple_mocks.localization_service.api.dto.LocalizationSourceId;
import com.github.simple_mocks.localization_service.api.dto.LocalizedText;
import com.github.simple_mocks.localization_service.mutable.api.dto.LocalizationKey;
import com.github.simple_mocks.localization_service.mutable.api.rq.AddLocalizationsRq;
import com.github.simple_mocks.localization_service.mutable.api.service.MutableLocalizationService;
import com.github.simple_mocks.localization_service.mutable.api.source.LocalizationJsonSource;
import com.github.simple_mocks.localization_service.mutable.api.source.LocalizationJsonSources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.FileUrlResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sibmaks
 * @since 0.0.3
 */
@Slf4j
@Component
@Conditional(LocalizationServiceEmbeddedCondition.class)
public class LocalizationJsonLoader {
    private final MutableLocalizationService mutableLocalizationService;
    private final ObjectMapper objectMapper;

    public LocalizationJsonLoader(MutableLocalizationService mutableLocalizationService,
                                  @Qualifier("localizationServiceObjectMapper")
                                  ObjectMapper objectMapper) {
        this.mutableLocalizationService = mutableLocalizationService;
        this.objectMapper = objectMapper;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent(ApplicationContext context) {
        var beans = context.getBeanNamesForAnnotation(LocalizationJsonSources.class);
        for (var bean : beans) {
            var sources = context.findAnnotationOnBean(bean, LocalizationJsonSources.class);
            if (sources == null) {
                continue;
            }
            for (var source : sources.value()) {
                loadLocalizations(source);
            }
        }
    }

    private void loadLocalizations(LocalizationJsonSource source) {
        FileUrlResource resource;
        try {
            resource = new FileUrlResource(source.path());
        } catch (MalformedURLException e) {
            throw new LocalizationLoadingException("Source path invalid exception", e);
        }
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
