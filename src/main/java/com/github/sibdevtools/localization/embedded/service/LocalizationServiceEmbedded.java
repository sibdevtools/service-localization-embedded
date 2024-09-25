package com.github.sibdevtools.localization.embedded.service;

import com.github.sibdevtools.content.api.condition.EqualsCondition;
import com.github.sibdevtools.content.api.rq.GetContentRq;
import com.github.sibdevtools.content.api.service.ContentService;
import com.github.sibdevtools.error.exception.ServiceException;
import com.github.sibdevtools.localization.api.dto.LocalizedText;
import com.github.sibdevtools.localization.api.rq.LocalizeRq;
import com.github.sibdevtools.localization.api.rs.LocalizeRs;
import com.github.sibdevtools.localization.api.service.LocalizationService;
import com.github.sibdevtools.localization.embedded.constants.Constants;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.sibdevtools.localization.embedded.constants.Constants.TYPE_TEXT;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "service.localization.mode", havingValue = "EMBEDDED")
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
