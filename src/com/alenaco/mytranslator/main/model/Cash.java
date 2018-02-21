package com.alenaco.mytranslator.main.model;

import javax.xml.bind.annotation.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author kovalenko
 * @version $Id$
 */
@XmlRootElement()
public class Cash {
    private Statistic statistic = new Statistic();

    private Set<Word> words = new HashSet<>();//можно разделить листы на разные языки

    @XmlElementWrapper()
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
            statistic.increaseStatisticCount(word);
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
            sb.append("translation: ").append(word.getTranslationsStr()).append("\n");
            sb.append("count: ").append(statistic.getStatistic(word, false)).append("\n");
        }
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public int getStatistic(Word word) {
        return statistic.getStatistic(word, false);
    }

    public void put(String en, String ru, Language fromLang) {
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
                statistic.increaseStatisticCount(ruWord);
                break;
            case EN:
                statistic.increaseStatisticCount(enWord);
                break;
        }
        ruWord.addTranslation(enWord);
        enWord.addTranslation(ruWord);
    }

    private Word findWord(String chars) {
        Optional<Word> foundWord = words.stream()
                .filter(word -> word.getChars().equals(chars))
                .findFirst();
        return foundWord.orElse(null);
    }
}
