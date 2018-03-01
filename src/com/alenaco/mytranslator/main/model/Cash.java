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

//todo смешанная логика сущности и ее управления
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

    public Word getTranslation(String chars) {
        Word word = findWord(chars);
        if (word != null) {
            word.increaseSearchCount();
            return word;
        }
        return null;
    }

    public String getCashStr() {
        StringBuilder sb = new StringBuilder();
        for (Word word : words) {
            switch (word.getLanguage()) {
                case RU:
                    sb.append("ru: ");
                    break;
                case EN:
                    sb.append("en: ");
                    break;
            }
            sb.append(word.getChars()).append("\n");
            sb.append("translation: ").append(word.getTranslationsStr(this)).append("\n");
            sb.append("count: ").append(word.getSearchCount()).append("\n");
        }
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public Word put(String en, String ru, Language fromLang) {
        Word ruWord = findWord(ru);
        Word enWord = findWord(en);
        if (ruWord == null) {
            ruWord = new Word(ru, Language.RU);
            words.add(ruWord);
        }
        if (enWord == null) {
            enWord = new Word(en, Language.EN);
            words.add(enWord);
        }
        switch (fromLang) {
            case RU:
                ruWord.increaseSearchCount();
                break;
            case EN:
                enWord.increaseSearchCount();
                break;
        }
        ruWord.addTranslation(enWord);
        enWord.addTranslation(ruWord);

        return fromLang == Language.RU ? ruWord : enWord;
    }

    public void addNewTranslation(Word translationWord) {
        Word word = findWord(translationWord.getChars());
        if (word != null && word.getLanguage() == translationWord.getLanguage()) {
            for (UUID id : translationWord.getTranslations()) {
                word.getTranslations().add(id);
                findWordById(id).getTranslations().add(word.getId());
            }
        } else {
            words.add(translationWord);
            for (UUID id : translationWord.getTranslations()) {
                findWordById(id).getTranslations().add(translationWord.getId());
            }
        }
    }

    private Word findWord(String chars) {
        Optional<Word> foundWord = words.stream()
                .filter(word -> word.getChars().equals(chars))
                .findFirst();
        return foundWord.orElse(null);
    }

    public Word findWordById(UUID id) {
        for (Word word : words) {
            if (word.getId().equals(id)) {
                return word;
            }
        }
        return null;
    }

    public void removeWords(Word... selectedWords) {
        for (Word word : selectedWords) {
            words.remove(word);
        }
    }
}
