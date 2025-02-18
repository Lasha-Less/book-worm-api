package com.lb.book_worm_api.util;

import java.util.Locale;

public class LanguageUtils {

    public static String toIsoCode(String language){
        if (language == null || language.trim().isEmpty()){
            return language;
        }

        String normalizedLang = language.trim().toLowerCase();

        for (Locale locale : Locale.getAvailableLocales()){
            if (locale.getLanguage().equalsIgnoreCase(normalizedLang)){
                return normalizedLang;
            }
        }

        for (Locale locale : Locale.getAvailableLocales()){
            if (locale.getDisplayLanguage().equalsIgnoreCase(normalizedLang)){
                return locale.getLanguage();
            }
        }

        return language;
    }
}
