package com.github.simplemocks.localization.embedded.exception;

import com.github.simplemocks.error_service.exception.ServiceException;
import com.github.simplemocks.localization.embedded.constants.Constants;
import jakarta.annotation.Nonnull;

/**
 * @author sibmaks
 * @since 0.0.3
 */
public class LocalizationLoadingException extends ServiceException {
    public LocalizationLoadingException(@Nonnull String systemMessage, @Nonnull Throwable cause) {
        super(Constants.ERROR_SOURCE_ID, "LOCALIZATION_LOADING_EXCEPTION", systemMessage, cause);
    }
}
