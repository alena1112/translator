package com.alenaco.mytranslator.main.controller.managers;

import com.alenaco.mytranslator.main.controller.importing.ImportWordsFromFile;
import com.alenaco.mytranslator.main.controller.storages.JSONStorage;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.translator.TranslatorResult;
import com.alenaco.mytranslator.main.controller.translator.google.GoogleTranslator;
import com.alenaco.mytranslator.main.controller.translator.yandex.YandexTranslator;
import com.alenaco.mytranslator.main.controller.utils.LanguageUtils;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.SessionContext;
import com.alenaco.mytranslator.main.model.Word;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author kovalenko
 * @version $Id$
 */
public class SessionManager {
    private SessionContext sessionContext;
    private CashManagerAPI cashManager;
    private Translator translator;

    public SessionManager() throws StorageException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        //todo read session params from db or default settings
        sessionContext = new SessionContext(YandexTranslator.class, JSONStorage.class);
        translator = (Translator) SettingsHelper.getSettingsInstance(sessionContext.getTranslator());
        cashManager = new ProxyCashManagerAPI(sessionContext.getStorage());
    }

    public void restartSessionManager() throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, StorageException {
        if (!translator.getClass().equals(sessionContext.getTranslator())) {
            translator = (Translator) SettingsHelper.getSettingsInstance(sessionContext.getTranslator());
        }
        cashManager.restoreCash(sessionContext.getStorage());
    }

    public SessionContext getSessionContext() {
        return sessionContext;
    }

    public CashManagerAPI getCashManager() {
        return cashManager;
    }

    public String translateWord(String chars) throws UnsupportedOperationException {
        if (StringUtils.isNotBlank(chars)) {
            Language fromLang = LanguageUtils.getLanguage(chars);
            Language toLang = fromLang == Language.RU ? Language.EN : Language.RU;
            Word word = cashManager.getTranslation(chars);
            if (word == null) {
                TranslatorResult result = translator.getTranslation(chars, fromLang, toLang);
                return cashManager.getTranslationsStr(cashManager.createWord(chars, result.getText(), fromLang));
            } else {
                return cashManager.getTranslationsStr(word);
            }
        }
        return null;
    }

    public void importFileIntoCash() throws StorageException {
        cashManager.importFileIntoCash();
    }

    public boolean isWordExists(UUID id) {
        return cashManager.findWordById(id) != null;
    }
}
