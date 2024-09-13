package com.github.simplemocks.localization.embedded.service;

import com.github.simplemocks.content.api.condition.EqualsCondition;
import com.github.simplemocks.content.api.rq.GetContentRq;
import com.github.simplemocks.content.api.service.ContentService;
import com.github.simplemocks.error_service.exception.ServiceException;
import com.github.simplemocks.localization.embedded.conf.LocalizationServiceEmbeddedCondition;
import com.github.simplemocks.localization.embedded.constants.Constants;
import com.github.simplemocks.localization_service.api.dto.LocalizedText;
import com.github.simplemocks.localization_service.api.rq.LocalizeRq;
import com.github.simplemocks.localization_service.api.rs.LocalizeRs;
import com.github.simplemocks.localization_service.api.service.LocalizationService;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.simplemocks.localization.embedded.constants.Constants.TYPE_TEXT;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Slf4j
@Component
@Conditional(LocalizationServiceEmbeddedCondition.class)
public class LocalizationServiceEmbedded implements LocalizationService {

    private final ContentService contentService;

    /**
     * Constructor embedded localization service
     *
     * @param contentService content service
     */
    public LocalizationServiceEmbedded(ContentService contentService) {
        this.contentService = contentService;
    }

    @Nonnull
    @Override
    public LocalizeRs localize(@Nonnull LocalizeRq rq) {
        var localizationId = rq.localizationId();
        var code = localizationId.code();
        var sourceId = localizationId.sourceId();
        var userLocale = rq.userLocale();

        try {
            var contentRq = GetContentRq.<LocalizedText>builder()
                    .systemCode(sourceId.getSystemCode())
                    .type(Constants.CONTENT_TYPE)
                    .groupCode(sourceId.getKindCode())
                    .contentType(LocalizedText.class)
                    .conditions(
                            List.of(
                                    new EqualsCondition(Constants.ATTRIBUTE_CODE, code),
                                    new EqualsCondition(Constants.ATTRIBUTE_TYPE, TYPE_TEXT),
                                    new EqualsCondition(Constants.ATTRIBUTE_LOCALE, userLocale.getISO3Language())
                            )
                    )
                    .build();

            var rs = contentService.getContent(contentRq);

            var contents = rs.getBody();

            for (var entry : contents.entrySet()) {
                var contentHolder = entry.getValue();
                if (contentHolder != null) {
                    return new LocalizeRs(contentHolder.getContent());
                }
            }
        } catch (ServiceException e) {
            log.error("Failed to localize error", e);
        }
        return new LocalizeRs((LocalizedText) null);
    }
}
