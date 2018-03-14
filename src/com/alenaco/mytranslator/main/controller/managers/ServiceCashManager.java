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

public class ServiceCashManager implements CashManagerAPI {
    private Cash cash;
    private Storage storage;
    private boolean modified;
    private List<CashChangedListener> listeners = new ArrayList<>();

    public ServiceCashManager(Class storageClass) throws StorageException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        super();
        this.storage = (Storage) SettingsHelper.getSettingsInstance(storageClass);
        this.cash = storage.restoreCash();
    }

    @Override
    public void saveCash() throws StorageException {
        storage.saveCash(cash);
        modified = false;
    }

    @Override
    public Word getTranslation(String chars) {
        Word word = findWord(chars);
        if (word != null) {
            word.increaseSearchCount();
            setModified(true, word, CashChangingType.CHANGE_COUNT);
            return word;
        }
        return null;
    }

    @Override
    public Word createWord(String en, String ru, Language fromLang) {
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
            setModified(true, ruWord, enWord);
        }
        if (enWord.addTranslation(ruWord)) {
            setModified(true, enWord, enWord);
        }

        return fromLang == Language.RU ? ruWord : enWord;
    }

    @Override
    public String getTranslationsStr(Word word) {
        Word reloadWord = findWordById(word.getId());
        String[] params = new String[reloadWord.getTranslations().size()];
        Iterator<UUID> iterator = reloadWord.getTranslations().iterator();
        for (int i = 0; i < params.length; i++) {
            Word translation = findWordById(iterator.next());
            if (translation != null) {
                params[i] = translation.getChars();
            }
        }
        return StringUtils.join(params, ", ");
    }

    @Override
    public void addNewTranslation(Word translationWord) {
        cash.getWords().add(translationWord);
        setModified(true, translationWord, CashChangingType.ADD);
        Set<UUID> baseWords = translationWord.getTranslations();
        for (UUID baseWordId : baseWords) {
            Word baseWord = findWordById(baseWordId);
            if (baseWord != null) {
                baseWord.addTranslation(translationWord);
                setModified(true, baseWord, translationWord);
            }
        }
    }

    @Override
    public Set<Word> getWords() {
        return cash.getWords();
    }

    @Override
    public void removeWordsWithTranslations(List<Word> selectedWords) {
        for (Word word : selectedWords) {
            Word reloadWord = findWordById(word.getId());
            for (UUID id : reloadWord.getTranslations()) {
                Word translation = findWordById(id);
                if (translation != null) {
                    cash.getWords().remove(translation);
                    setModified(true, translation, CashChangingType.DELETE);
                }
            }
            cash.getWords().remove(reloadWord);
            setModified(true, reloadWord, CashChangingType.DELETE);
        }
    }

    @Override
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

    @Override
    public void addCashChangedListener(CashChangedListener listener) {
        listeners.add(listener);
    }

    private Word findWord(String chars) {
        Optional<Word> foundWord = cash.getWords().stream()
                .filter(word -> word.getChars().equals(chars))
                .findFirst();
        return foundWord.orElse(null);
    }

    private void setModified(boolean modified, Word word, CashChangingType changingType) {
        this.modified = modified;
        if (modified) {
            fireCashChangedListeners(word, changingType);
        }
    }

    private void setModified(boolean modified, Word word, Word translation) {
        this.modified = modified;
        if (modified) {
            fireAddTranslationListeners(word, translation);
        }
    }

    @Override
    public void fireCashChangedListeners(Word word, CashChangingType changingType) {
        for (CashChangedListener listener : listeners) {
            listener.cashChanged(word, changingType);
        }
    }

    @Override
    public void fireAddTranslationListeners(Word word, Word translation) {
        for (CashChangedListener listener : listeners) {
            listener.addTranslation(word, translation);
        }
    }

    @Override
    public Word findWordById(UUID id) {
        for (Word word : cash.getWords()) {
            if (word.getId().equals(id)) {
                return word;
            }
        }
        return null;
    }
}
