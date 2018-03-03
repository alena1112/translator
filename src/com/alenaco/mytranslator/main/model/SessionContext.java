package com.alenaco.mytranslator.main.model;

/**
 * @author kovalenko
 * @version $Id$
 */
public class SessionContext {
    private Class translator;
    private Class storage;

    public SessionContext(Class translator, Class storage) {
        this.translator = translator;
        this.storage = storage;
    }

    public Class getTranslator() {
        return translator;
    }

    public void setTranslator(Class translator) {
        this.translator = translator;
    }

    public Class getStorage() {
        return storage;
    }

    public void setStorage(Class storage) {
        this.storage = storage;
    }
}
