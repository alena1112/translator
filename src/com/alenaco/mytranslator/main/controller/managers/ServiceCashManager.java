package com.alenaco.mytranslator.main.controller.managers;

import com.alenaco.mytranslator.main.controller.storages.Storage;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

/**
 * @author kovalenko
 * @version $Id$
 */
public class ServiceCashManager extends CashManager {
    private Storage storage;

    public ServiceCashManager(Class storageClass) throws StorageException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        super();
        this.storage = (Storage) SettingsHelper.getSettingsInstance(storageClass);
        this.cash = storage.restoreCash();
    }

    @Override
    protected void saveCash() throws StorageException {
        storage.saveCash(cash);
        modified = false;
    }

    @Override
    protected Word getTranslation(String chars) {
        Word word = findWord(chars);
        if (word != null) {
            word.increaseSearchCount();
            setModified(true, word, CashManager.CashChangingType.CHANGE_COUNT);
            return word;
        }
        return null;
    }

    @Override
    protected Word createWord(String en, String ru, Language fromLang) {
        Word ruWord = findWord(ru);
        Word enWord = findWord(en);
        if (ruWord == null) {
            ruWord = new Word(ru, Language.RU);
            cash.getWords().add(ruWord);
            setModified(true, ruWord, CashManager.CashChangingType.ADD);
        }
        if (enWord == null) {
            enWord = new Word(en, Language.EN);
            cash.getWords().add(enWord);
            setModified(true, enWord, CashManager.CashChangingType.ADD);
        }
        switch (fromLang) {
            case RU:
                ruWord.increaseSearchCount();
                setModified(true, ruWord, CashManager.CashChangingType.CHANGE_COUNT);
                break;
            case EN:
                enWord.increaseSearchCount();
                setModified(true, enWord, CashManager.CashChangingType.CHANGE_COUNT);
                break;
        }
        if (ruWord.addTranslation(enWord)) {
            setModified(true, ruWord, CashManager.CashChangingType.ADD_TRANSLATION);
        }
        if (enWord.addTranslation(ruWord)) {
            setModified(true, enWord, CashManager.CashChangingType.ADD_TRANSLATION);
        }

        return fromLang == Language.RU ? ruWord : enWord;
    }

    @Override
    public void addNewTranslation(Word translationWord) {
        Word word = findWord(translationWord.getChars());
        if (word != null && word.getLanguage() == translationWord.getLanguage()) {
            for (UUID id : translationWord.getTranslations()) {
                word.getTranslations().add(id);
                setModified(true, word, CashManager.CashChangingType.ADD_TRANSLATION);
                Word wordById = findWordById(id);
                wordById.getTranslations().add(word.getId());
                setModified(true, wordById, CashManager.CashChangingType.ADD_TRANSLATION);
            }
        } else {
            cash.getWords().add(translationWord);
            setModified(true, translationWord, CashManager.CashChangingType.ADD);
            for (UUID id : translationWord.getTranslations()) {
                Word wordById = findWordById(id);
                wordById.getTranslations().add(translationWord.getId());
                setModified(true, wordById, CashManager.CashChangingType.ADD_TRANSLATION);
            }
        }
    }

    @Override
    public void removeWordsWithTranslations(List<Word> selectedWords) {
        for (Word word : selectedWords) {
            for (UUID id : word.getTranslations()) {
                Word translation = findWordById(id);
                if (translation != null) {
                    cash.getWords().remove(translation);
                    setModified(true, translation, CashManager.CashChangingType.DELETE);
                }
            }
            cash.getWords().remove(word);
            setModified(true, word, CashManager.CashChangingType.DELETE);
        }
    }
}
