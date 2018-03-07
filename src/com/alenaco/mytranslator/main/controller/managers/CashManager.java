package com.alenaco.mytranslator.main.controller.managers;

import com.alenaco.mytranslator.main.controller.storages.Storage;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import com.alenaco.mytranslator.main.model.Cash;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author kovalenko
 * @version $Id$
 */

public abstract class CashManager {
    protected Cash cash;
    protected boolean modified = false;
    protected List<CashChangedListener> listeners = new ArrayList<>();

    protected abstract void saveCash() throws StorageException;

    public boolean isModified() {
        return modified;
    }

    public Set<Word> getWords() {
        return cash.getWords();
    }

    protected void setModified(boolean modified, Word word, CashChangingType changingType) {
        this.modified = modified;
        if (modified) {
            fireCashChangedListeners(word, changingType);
        }
    }

    private void fireCashChangedListeners(Word word, CashChangingType changingType) {
        for (CashChangedListener listener : listeners) {
            listener.cashChanged(word, changingType);
        }
    }

    protected abstract Word getTranslation(String chars);

    public String getCashStr() {
        StringBuilder sb = new StringBuilder();
        for (Word word : cash.getWords()) {
            switch (word.getLanguage()) {
                case RU:
                    sb.append("ru: ");
                    break;
                case EN:
                    sb.append("en: ");
                    break;
            }
            sb.append(word.getChars()).append("\n");
            sb.append("translation: ").append(getTranslationsStr(word)).append("\n");
            sb.append("count: ").append(word.getSearchCount()).append("\n");
        }
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    protected abstract Word createWord(String en, String ru, Language fromLang);

    public abstract void addNewTranslation(Word translationWord);

    public Word findWord(String chars) {
        Optional<Word> foundWord = cash.getWords().stream()
                .filter(word -> word.getChars().equals(chars))
                .findFirst();
        return foundWord.orElse(null);
    }

    public Word findWordById(UUID id) {
        for (Word word : cash.getWords()) {
            if (word.getId().equals(id)) {
                return word;
            }
        }
        return null;
    }

    public abstract void removeWordsWithTranslations(List<Word> selectedWords);

    public String getTranslationsStr(Word word) {
        String[] params = new String[word.getTranslations().size()];
        Iterator<UUID> iterator = word.getTranslations().iterator();
        for (int i = 0; i < params.length; i++) {
            Word translation = findWordById(iterator.next());
            if (translation != null) {
                params[i] = translation.getChars();
            }
        }
        return StringUtils.join(params, ", ");
    }

    public List<Word> getTranslations(Word word) {
        List<Word> result = new ArrayList<>();
        for (UUID id : word.getTranslations()) {
            Word translation = findWordById(id);
            if (translation != null) {
                result.add(translation);
            }
        }
        return result;
    }

    public void addCashChangedListener(CashChangedListener listener) {
        listeners.add(listener);
    }

    public interface CashChangedListener {
        void cashChanged(Word word, CashChangingType changingType);
    }

    public enum CashChangingType {
        ADD, CHANGE_CHARS, CHANGE_COUNT, ADD_TRANSLATION, DELETE
    }
}
