package com.alenaco.mytranslator.main.model;

import com.alenaco.mytranslator.main.controller.storages.Storage;
import com.alenaco.mytranslator.main.controller.translator.Translator;

/**
 * @author kovalenko
 * @version $Id$
 */
public class SessionContext {
    private Translator translator;
    private String translatorName;
    private Storage<Cash> storage;

    public Translator getTranslator() {
        return translator;
    }

    public void setTranslator(Translator translator, String translatorName) {
        this.translator = translator;
        this.translatorName = translatorName;
    }

    public Storage<Cash> getStorage() {
        return storage;
    }

    public void setStorage(Storage<Cash> storage) {
        this.storage = storage;
    }

    public String getTranslatorName() {
        return translatorName;
    }
}
