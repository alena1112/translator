package com.alenaco.mytranslator.main.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author kovalenko
 * @version $Id$
 */

@XmlRootElement
public class Cash {
    private Set<Word> words = new HashSet<>();

    @XmlElementWrapper
    @XmlElement(name = "word")
    public Set<Word> getWords() {
        return words;
    }

    public void setWords(Set<Word> words) {
        this.words = words;
    }
}
