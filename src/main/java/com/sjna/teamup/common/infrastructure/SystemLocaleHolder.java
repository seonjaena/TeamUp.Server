package com.sjna.teamup.common.infrastructure;

import com.sjna.teamup.common.service.port.LocaleHolder;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SystemLocaleHolder implements LocaleHolder {
    @Override
    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
}
