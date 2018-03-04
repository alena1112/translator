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

public class CashManager {
    private Storage storage;
    private Cash cash;
    private boolean modified = false;
    private List<CashChangedListener> listeners = new ArrayList<>();

    public CashManager(Class storageClass) throws StorageException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        this.storage = (Storage) SettingsHelper.getSettingsInstance(storageClass);
        this.cash = storage.restoreCash();
    }

    protected void saveCash() throws StorageException {
        storage.saveCash(cash);
        modified = false;
    }

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

    protected Word getTranslation(String chars) {
        Word word = findWord(chars);
        if (word != null) {
            word.increaseSearchCount();
            setModified(true, word, CashChangingType.CHANGE_COUNT);
            return word;
        }
        return null;
    }

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

    protected Word createWord(String en, String ru, Language fromLang) {
        Word ruWord = findWord(ru);
        Word enWord = findWord(en);
        if (ruWord == null) {
            ruWord = new Word(ru, Language.RU);
            cash.getWords().add(ruWord);
            setModified(true, ruWord, CashChangingType.ADD);
        }
        if (enWord == null) {
            enWord = new Word(en, Language.EN);
            cash.getWords().add(enWord);
            setModified(true, enWord, CashChangingType.ADD);
        }
        switch (fromLang) {
            case RU:
                ruWord.increaseSearchCount();
                setModified(true, ruWord, CashChangingType.CHANGE_COUNT);
                break;
            case EN:
                enWord.increaseSearchCount();
                setModified(true, enWord, CashChangingType.CHANGE_COUNT);
                break;
        }
        if (ruWord.addTranslation(enWord)) {
            setModified(true, ruWord, CashChangingType.ADD_TRANSLATION);
        }
        if (enWord.addTranslation(ruWord)) {
            setModified(true, enWord, CashChangingType.ADD_TRANSLATION);
        }

        return fromLang == Language.RU ? ruWord : enWord;
    }

    public void addNewTranslation(Word translationWord) {
        Word word = findWord(translationWord.getChars());
        if (word != null && word.getLanguage() == translationWord.getLanguage()) {
            for (UUID id : translationWord.getTranslations()) {
                word.getTranslations().add(id);
                setModified(true, word, CashChangingType.ADD_TRANSLATION);
                Word wordById = findWordById(id);
                wordById.getTranslations().add(word.getId());
                setModified(true, wordById, CashChangingType.ADD_TRANSLATION);
            }
        } else {
            cash.getWords().add(translationWord);
            setModified(true, translationWord, CashChangingType.ADD);
            for (UUID id : translationWord.getTranslations()) {
                Word wordById = findWordById(id);
                wordById.getTranslations().add(translationWord.getId());
                setModified(true, wordById, CashChangingType.ADD_TRANSLATION);
            }
        }
    }

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

    public void removeWordsWithTranslations(List<Word> selectedWords) {
        for (Word word : selectedWords) {
            for (UUID id : word.getTranslations()) {
                Word translation = findWordById(id);
                if (translation != null) {
                    cash.getWords().remove(translation);
                    setModified(true, translation, CashChangingType.DELETE);
                }
            }
            cash.getWords().remove(word);
            setModified(true, word, CashChangingType.DELETE);
        }
    }

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
