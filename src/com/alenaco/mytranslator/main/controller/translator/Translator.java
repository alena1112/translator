package com.alenaco.mytranslator.main.controller.translator;

import com.alenaco.mytranslator.main.model.Language;

/**
 * @author kovalenko
 * @version $Id$
 */
public interface Translator {

    TranslatorResult getTranslation(String text, Language from, Language to);

    String getInstanceName();
}
