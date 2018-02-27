package com.alenaco.mytranslator.main.controller;

import com.alenaco.mytranslator.main.controller.translator.Named;

/**
 * Created by alena on 28.02.18.
 */
public abstract class AppSettings {

    public String getInstanceName() {
        Named annotation = getClass().getAnnotation(Named.class);
        if (annotation != null) {
            return annotation.name();
        }
        return null;
    }
}
