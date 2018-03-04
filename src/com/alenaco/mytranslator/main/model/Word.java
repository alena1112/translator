package com.alenaco.mytranslator.main.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    public Word(UUID id) {
        super(id);
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

    public boolean addTranslation(Word translation) {
        if (translation.getLanguage() != this.language && this.translations.contains(translation.getId())) {
            this.translations.add(translation.getId());
            return true;
        }
        return false;
    }

    public void increaseSearchCount() {
        this.searchCount += 1;
        this.lastSearchDate = new Date();
    }

    public Word getCopyWord() {
        Word copy = new Word(getId());
        copy.chars = chars;
        copy.language = language;
        copy.searchCount = searchCount;
        copy.lastSearchDate = lastSearchDate;
        copy.translations = new HashSet<>(translations);
        return copy;
    }
}
