package com.alenaco.mytranslator.main.model;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

/**
 * @author kovalenko
 * @version $Id$
 */
@XmlRootElement
public class Word extends UUIDEntity {
    private String chars;
    private Language language;
    private Set<UUID> translations;
    private int searchCount;
    private Date lastSearchDate;

    public Word() {
        super();
        this.translations = new HashSet<>();
    }

    public Word(String chars, Language language) {
        this();
        this.chars = chars;
        this.language = language;
    }

    @XmlAttribute
    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    @XmlAttribute
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @XmlAttribute
    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    @XmlAttribute
    public Date getLastSearchDate() {
        return lastSearchDate;
    }

    public void setLastSearchDate(Date lastSearchDate) {
        this.lastSearchDate = lastSearchDate;
    }

    @XmlElementWrapper(name = "translationIds")
    @XmlElement(name = "id")
    public Set<UUID> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<UUID> translations) {
        this.translations = translations;
    }

    public void addTranslation(Word translation) {
        if (translation.getLanguage() != this.language) {
            this.translations.add(translation.getId());
        }
    }

    public String getTranslationsStr(Cash cash) {
        String[] params = new String[translations.size()];
        Iterator<UUID> iterator = translations.iterator();
        for (int i = 0; i < params.length; i++) {
            Word word = cash.findWordById(iterator.next());
            if (word != null) {
                params[i] = word.getChars();
            }
        }
        return StringUtils.join(params, ", ");
    }

    public List<Word> getTranslations(Cash cash) {
        List<Word> result = new ArrayList<>();
        for (UUID id : translations) {
            Word word = cash.findWordById(id);
            if (word != null) {
                result.add(word);
            }
        }
        return result;
    }

    public void increaseSearchCount() {
        this.searchCount += 1;
        this.lastSearchDate = new Date();
    }
}
