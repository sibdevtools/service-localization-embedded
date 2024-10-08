package com.github.sibdevtools.localization.embedded.service;

import com.github.sibdevtools.content.mutable.api.rq.CreateContentGroupRq;
import com.github.sibdevtools.content.mutable.api.rq.CreateContentRq;
import com.github.sibdevtools.content.mutable.api.rq.CreateSystemRq;
import com.github.sibdevtools.content.mutable.api.rq.DeleteContentRq;
import com.github.sibdevtools.content.mutable.api.service.MutableContentService;
import com.github.sibdevtools.localization.embedded.constants.Constants;
import com.github.sibdevtools.localization.mutable.api.rq.AddLocalizationsRq;
import com.github.sibdevtools.localization.mutable.api.rq.DeleteLocalizationsRq;
import com.github.sibdevtools.localization.mutable.api.service.MutableLocalizationService;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "service.localization.mode", havingValue = "EMBEDDED")
public class MutableLocalizationServiceEmbedded implements MutableLocalizationService {
    private final MutableContentService mutableContentService;

    /**
     * Constructor embedded mutable localization service
     *
     * @param mutableContentService mutable content service
     */
    public MutableLocalizationServiceEmbedded(MutableContentService mutableContentService) {
        this.mutableContentService = mutableContentService;
    }

    @Override
    public void addLocalizations(@Nonnull AddLocalizationsRq addLocalizationsRq) {
        var sourceId = addLocalizationsRq.sourceId();

        var systemCode = sourceId.getSystemCode();

        mutableContentService.createSystem(
                CreateSystemRq.builder()
                        .systemCode(systemCode)
                        .build()
        );

        var kindCode = sourceId.getKindCode();
        mutableContentService.createContentGroup(
                CreateContentGroupRq.builder()
                        .systemCode(systemCode)
                        .type(Constants.CONTENT_TYPE)
                        .code(kindCode)
                        .build()
        );

        var localizationsToAdd = addLocalizationsRq.localizations();
        for (var localizationEntry : localizationsToAdd.entrySet()) {
            var localizationKey = localizationEntry.getKey();

            var code = localizationKey.code();
            var userLocale = localizationKey.userLocale();
            var iso3Locale = userLocale.getISO3Language();

            var localization = localizationEntry.getValue();

            mutableContentService.createContent(
                    CreateContentRq.builder()
                            .systemCode(systemCode)
                            .type(Constants.CONTENT_TYPE)
                            .groupCode(kindCode)
                            .code(iso3Locale + ":" + code)
                            .content(localization)
                            .attributes(
                                    Map.of(
                                            Constants.ATTRIBUTE_LOCALE, iso3Locale,
                                            Constants.ATTRIBUTE_TYPE, Constants.TYPE_TEXT,
                                            Constants.ATTRIBUTE_CODE, code
                                    )
                            )
                            .build()
            );
        }

    }

    @Override
    public void deleteLocalizations(@Nonnull DeleteLocalizationsRq deleteLocalizationsRq) {
        var sourceId = deleteLocalizationsRq.sourceId();

        var systemCode = sourceId.getSystemCode();
        var kindCode = sourceId.getKindCode();

        var localizationKeys = deleteLocalizationsRq.localizations();

        for (var localizationKey : localizationKeys) {
            mutableContentService.deleteContent(
                    DeleteContentRq.builder()
                            .systemCode(systemCode)
                            .type(Constants.CONTENT_TYPE)
                            .groupCode(kindCode)
                            .code(localizationKey.userLocale() + ":" + localizationKey.code())
                            .build()
            );
        }
    }
}
