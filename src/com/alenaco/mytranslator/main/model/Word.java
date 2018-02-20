package com.alenaco.mytranslator.main.model;

import com.alenaco.mytranslator.main.controller.utils.LanguageUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author kovalenko
 * @version $Id$
 */
public class Word {
    private String chars;
    private Language language;
    private Set<Word> translations;

    public Word(String chars, Language language) {
        this.chars = chars;
        this.language = language;
        this.translations = new HashSet<>();
    }

    public Word(String chars) {
        this.chars = chars;
        this.language = LanguageUtils.getLanguage(chars);
        this.translations = new HashSet<>();
    }

    public void addTranslation(Word translation) {
        if (translation.getLanguage() != this.language) {
            this.translations.add(translation);
        }
    }

    public String getTranslationsStr() {
        String[] params = new String[translations.size()];
        Iterator<Word> iterator = translations.iterator();
        for (int i = 0; i < params.length; i++) {
            params[i] = iterator.next().getChars();
        }
        return StringUtils.join(params, ", ");
    }

    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Set<Word> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<Word> translations) {
        this.translations = translations;
    }
}
