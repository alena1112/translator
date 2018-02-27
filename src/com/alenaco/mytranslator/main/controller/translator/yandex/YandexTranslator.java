package com.alenaco.mytranslator.main.controller.translator.yandex;

import com.alenaco.mytranslator.main.controller.translator.Named;
import com.alenaco.mytranslator.main.controller.utils.HttpResult;
import com.alenaco.mytranslator.main.controller.utils.HttpSender;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.translator.TranslatorResult;
import com.alenaco.mytranslator.main.model.Language;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kovalenko
 * @version $Id$
 */
@Named(name = "Yandex Translator")
public class YandexTranslator implements Translator {

    private static final String API_KEY = "trnsl.1.1.20180214T084739Z.01acd7691b070343.fa3ff14d0d76fdd894bc5559f8e7bfec39c19c8b";
    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";

    @Override
    public TranslatorResult getTranslation(String text, Language from, Language to) {
        TranslatorResult result = new TranslatorResult();

        if (from == to) {
            result.setResult(true);
            result.setText(text);
            return result;
        }

        LanguageDirection direction = null;
        switch (from) {
            case RU:
                direction = LanguageDirection.RU_EN;
                break;
            case EN:
                direction = LanguageDirection.EN_RU;
                break;
        }

        Map<String, String> params = new HashMap<>();
        params.put("key", API_KEY);
        params.put("text", text);
        params.put("lang", direction.getId());

        HttpResult httpResult = HttpSender.sendRequest(BASE_URL, params);

        if (httpResult.isSuccessful()) {
            JSONObject object = httpResult.getJsonObject();
            Integer code = (Integer) object.get("code");
            JSONArray translation = (JSONArray) object.get("text");
            result.setResult(code == 200);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < translation.length(); i++) {
                sb.append(translation.get(0));
                if ((i + 1) < translation.length()) {
                    sb.append("\n");
                }
            }
            result.setText(sb.toString());
        }

        return result;
    }

    @Override
    public String getInstanceName() {
        Named annotation = getClass().getAnnotation(Named.class);
        if (annotation != null) {
            return annotation.name();
        }
        return null;
    }
}
