package com.alenaco.mytranslator.main.controller.translator.google;

import com.alenaco.mytranslator.main.controller.Named;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.translator.TranslatorResult;
import com.alenaco.mytranslator.main.model.Language;

/**
 * Created by alena on 22.02.18.
 */
@Named(name = "Google Translator")
public class GoogleTranslator implements Translator {

    @Override
    public TranslatorResult getTranslation(String text, Language from, Language to) {
        throw new UnsupportedOperationException();
    }
}
