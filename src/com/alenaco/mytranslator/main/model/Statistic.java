package com.alenaco.mytranslator.main.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author kovalenko
 * @version $Id$
 */
public class Statistic {
    private List<WordStatistic> statistics = new ArrayList<>();

    public int getStatistic(Word word, boolean hasIncreaseCount) {
        Optional<WordStatistic> foundStatistic = statistics.stream()
                .filter(statistic -> {
                    Word statisticWord = statistic.word;
                    return word.getLanguage() == statisticWord.getLanguage() && statisticWord.getChars().equals(word.getChars());
                })
                .findFirst();
        if (foundStatistic.isPresent()) {
            WordStatistic statistic = foundStatistic.get();
            if (hasIncreaseCount) {
                statistic.increaseSearchCount();
            }
            return statistic.searchCount;
        } else if (hasIncreaseCount) {
            createStatistic(word);
            return 1;
        }
        return 0;
    }

    public void createStatistic(Word word) {
        WordStatistic statistic = new WordStatistic(word);
        statistics.add(statistic);
    }

    public void increaseStatisticCount(Word word) {
        getStatistic(word, true);
    }

    private class WordStatistic {
        private Word word;
        private int searchCount;

        public WordStatistic(Word word) {
            this.word = word;
            this.searchCount = 1;
        }

        public void increaseSearchCount() {
            this.searchCount += 1;
        }
    }
}
