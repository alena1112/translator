package com.alenaco.mytranslator.main.controller.managers;

import com.alenaco.mytranslator.main.controller.storages.JSONStorage;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.translator.TranslatorResult;
import com.alenaco.mytranslator.main.controller.translator.yandex.YandexTranslator;
import com.alenaco.mytranslator.main.controller.utils.LanguageUtils;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.SessionContext;
import com.alenaco.mytranslator.main.model.Word;
import com.alenaco.mytranslator.main.ui.UIApp;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author kovalenko
 * @version $Id$
 */
public class SessionManager {
    private SessionContext sessionContext;
    private CashManager cashManager;
    private Translator translator;

    public SessionManager() throws StorageException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        //todo read session params from db or default settings
        sessionContext = new SessionContext(YandexTranslator.class, JSONStorage.class);
        translator = (Translator) SettingsHelper.getSettingsInstance(sessionContext.getTranslator());
        cashManager = new CashManager(sessionContext.getStorage());
    }

    public SessionContext getSessionContext() {
        return sessionContext;
    }

    public Map<UUID, Word> getWords() {
        Map<UUID, Word> words = new HashMap<>();
        for (Word word : cashManager.getWords()) {
            words.put(word.getId(), word.getCopyWord());
        }
        return words;
    }

    public Word translateWord(String chars) throws UnsupportedOperationException {
        if (StringUtils.isNotBlank(chars)) {
            Language fromLang = LanguageUtils.getLanguage(chars);
            Language toLang = fromLang == Language.RU ? Language.EN : Language.RU;
            Word word = cashManager.getTranslation(chars);
            if (word == null) {
                TranslatorResult result = translator.getTranslation(chars, fromLang, toLang);
                return cashManager.createWord(chars, result.getText(), fromLang);
            } else {
                return word;
            }
        }
        return null;
    }

    public void saveCash() throws StorageException {
        cashManager.saveCash();
    }


    public void addCashChangedListener(CashManager.CashChangedListener listener) {
        cashManager.addCashChangedListener(listener);
    }

    public void removeWordsWithTranslations(Word...words) {
        List<Word> foundWords = new ArrayList<>();
        for (Word word : words) {
            foundWords.add(cashManager.findWordById(word.getId()));
        }
        cashManager.removeWordsWithTranslations(foundWords);
    }
}
