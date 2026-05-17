package hr.algebra.uni_course_management.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LocaleConfigTest {
    private final LocaleConfig localeConfig = new LocaleConfig();

    @Test
    void localeResolver_UsesEnglishAsDefaultLocale() {
        LocaleResolver resolver = localeConfig.localeResolver();

        assertThat(resolver).isInstanceOf(SessionLocaleResolver.class);
        assertThat(((SessionLocaleResolver) resolver).getDefaultLocale()).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void localeChangeInterceptor_UsesLangParameter() {
        LocaleChangeInterceptor interceptor = localeConfig.localeChangeInterceptor();

        assertThat(interceptor.getParamName()).isEqualTo("lang");
    }

    @Test
    void messageSource_LoadsMessagesBundle() {
        MessageSource messageSource = localeConfig.messageSource();

        assertThat(messageSource.getMessage("app.title", null, Locale.ENGLISH))
                .isEqualTo("University Course Management System");
    }
}
