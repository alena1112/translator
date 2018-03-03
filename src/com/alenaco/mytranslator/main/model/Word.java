package com.alenaco.mytranslator.main.model;

import com.alenaco.mytranslator.main.controller.managers.CashManager;
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

    public Word(UUID id, String chars, Language language, Set<UUID> translations, int searchCount, Date lastSearchDate) {
        super(id);
        this.chars = chars;
        this.language = language;
        this.searchCount = searchCount;
        this.lastSearchDate = lastSearchDate;
        this.translations = new HashSet<>(translations);
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
}
