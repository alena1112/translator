package com.alenaco.mytranslator.main.controller.managers;

import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.translator.TranslatorResult;
import com.alenaco.mytranslator.main.controller.utils.LanguageUtils;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author kovalenko
 * @version $Id$
 */
public class ClientCashManager extends CashManager {
    private CashManager serviceCashManager;

    @Override
    protected Word getTranslation(String chars) {
        return serviceCashManager.getTranslation(chars);
    }

    public void saveCash() throws StorageException {
        serviceCashManager.saveCash();
    }

    @Override
    public void removeWordsWithTranslations(List<Word> selectedWords) {

    }

    public String getCashStr() {
        return cashManager.getCashStr();
    }

    @Override
    protected Word createWord(String en, String ru, Language fromLang) {
        return null;
    }

    public String getTranslationsStr(Word word) {
        return cashManager.getTranslationsStr(cashManager.findWordById(word.getId()));
    }

    public void addNewTranslation(Word word) {
        Word copy = word.getCopyWord();
        cashManager.addNewTranslation(copy);
        updateWords();
    }
}
