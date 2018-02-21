package com.alenaco.mytranslator.main.model;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * @author kovalenko
 * @version $Id$
 */
@XmlRootElement()
public class Word extends UUIDEntity {
    private String chars;
    private Language language;
    private Set<Word> translations;

    public Word() {
        super();
        this.translations = new HashSet<>();
    }

    public Word(String chars, Language language) {
        this();
        this.chars = chars;
        this.language = language;
    }

    @XmlAttribute()
    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    @XmlAttribute()
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

//    public Set<Word> getTranslations() {
//        return translations;
//    }

    public void setTranslations(Set<Word> translations) {
        this.translations = translations;
    }

    @XmlElementWrapper(name = "translations")
    @XmlElement(name = "id")
    public Set<UUID> getTranslationIds() {
        Set<UUID> ids = new HashSet<>();
        for (Word translation : translations) {
            ids.add(translation.getId());
        }
        return ids;
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
}
