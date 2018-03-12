package com.alenaco.mytranslator.main.controller.managers;

import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;

import java.util.*;

/**
 * @author kovalenko
 * @version $Id$
 */
public class ClientCashManager implements CashManagerAPI {
    private CashManagerAPI serviceCashManager;

    public ClientCashManager(CashManagerAPI serviceCashManager) {
        this.serviceCashManager = serviceCashManager;
    }

    @Override
    public Word getTranslation(String chars) {
        return serviceCashManager.getTranslation(chars);
    }

    @Override
    public void saveCash() throws StorageException {
        serviceCashManager.saveCash();
    }

    @Override
    public void removeWordsWithTranslations(List<Word> selectedWords) {
        serviceCashManager.removeWordsWithTranslations(selectedWords);
    }

    @Override
    public String getCashStr() {
        return serviceCashManager.getCashStr();
    }

    @Override
    public Word createWord(String en, String ru, Language fromLang) {
        return serviceCashManager.createWord(en, ru, fromLang);
    }

    @Override
    public String getTranslationsStr(Word word) {
        return serviceCashManager.getTranslationsStr(word);
    }

    @Override
    public void addNewTranslation(Word word) {
        serviceCashManager.addNewTranslation(word);
    }

    @Override
    public Set<Word> getWords() {
        return serviceCashManager.getWords();
    }

    @Override
    public void addCashChangedListener(CashChangedListener listener) {
        serviceCashManager.addCashChangedListener(listener);
    }
}
