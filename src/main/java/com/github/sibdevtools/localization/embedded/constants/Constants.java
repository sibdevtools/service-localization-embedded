package com.github.sibdevtools.localization.embedded.constants;

import com.github.sibdevtools.error.api.dto.ErrorSourceId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author sibmaks
 * @since 0.0.10
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    public static final String CONTENT_TYPE = "sibdevtools.localizations";
    public static final ErrorSourceId ERROR_SOURCE_ID = new ErrorSourceId("LOCALIZATION_SERVICE");

    public static final String ATTRIBUTE_LOCALE = "locale";
    public static final String ATTRIBUTE_CODE = "code";
    public static final String ATTRIBUTE_TYPE = "type";
    public static final String TYPE_TEXT = "text";
}
