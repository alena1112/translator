package com.alenaco.mytranslator.main.controller.utils;

import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.controller.translator.LanguageDirection;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kovalenko
 * @version $Id$
 */
public class LanguageUtils {

    private static Pattern PATTERN_RU = Pattern.compile("[а-яА-ЯёЁ\\d\\s\\p{Punct}]*");
    private static Pattern PATTERN_EN = Pattern.compile("[a-zA-Z\\d\\s\\p{Punct}]*");

    public static LanguageDirection getLanguageDirection(String word) {
        Language language = getLanguage(word);
        if (language != null) {
            switch (language) {
                case RU:
                    return LanguageDirection.RU_EN;
                case EN:
                    return LanguageDirection.EN_RU;
            }
        }
        return null;
    }

    public static Language getLanguage(String word) {
        if (StringUtils.isNotBlank(word)) {
            Matcher matcher = PATTERN_RU.matcher(word);
            if (matcher.matches()) {
                return Language.RU;
            } else {
                return Language.EN;
            }
        }
        return null;
    }
}
