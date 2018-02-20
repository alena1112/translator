package com.alenaco.mytranslator.main.model;

import com.alenaco.mytranslator.main.controller.LanguageUtils;

import java.util.HashSet;
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
        StringBuilder sb = new StringBuilder();
        for (Word word : translations) {
            sb.append(word.getChars()).append(", ");
        }
        return sb.length() != 0 ? sb.substring(0, sb.length() - 2) : sb.toString();
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
