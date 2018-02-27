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
    private Storage storage;
    private String storageName;

    public Translator getTranslator() {
        return translator;
    }

    public void setTranslator(Translator translator, String translatorName) {
        this.translator = translator;
        this.translatorName = translatorName;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage, String storageName) {
        this.storage = storage;
        this.storageName = storageName;
    }

    public String getTranslatorName() {
        return translatorName;
    }
}
