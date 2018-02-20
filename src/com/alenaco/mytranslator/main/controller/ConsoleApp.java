package com.alenaco.mytranslator.main.controller;

import com.alenaco.mytranslator.main.controller.utils.LanguageUtils;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.translator.TranslatorResult;
import com.alenaco.mytranslator.main.controller.translator.yandex.YandexTranslator;
import com.alenaco.mytranslator.main.model.Cash;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;
import org.apache.commons.lang.StringUtils;

import java.util.Scanner;

/**
 * @author kovalenko
 * @version $Id$
 */
public class ConsoleApp {

    //указание синонимичных слов
    //приведение слов к инфинитиву???
    //сохранение статистики (кэша) в xml, data base using NoSQL
    //design patterns
    //JUnit, documentation

    public static void main(String[] args) {
        Translator translator = new YandexTranslator();//create factory
        Cash cash = new Cash();
        Scanner in = new Scanner(System.in);

        System.out.println("Write a word");

        String clientInput = in.nextLine();

        while (!clientInput.equals("@e")) {
            if (StringUtils.isNotBlank(clientInput)) {

                if (clientInput.equals("@c")) {
                    System.out.println(cash.getCashStr());
                } else {
                    Language fromLang = LanguageUtils.getLanguage(clientInput);
                    Language toLang = fromLang == Language.RU ? Language.EN : Language.RU;
                    Word word = cash.getTranslation(clientInput);
                    if (word == null) {
                        TranslatorResult result = translator.getTranslation(clientInput, fromLang, toLang);
                        System.out.println("Word: " + result.getText());
                        cash.put(clientInput, result.getText(), fromLang);
                    } else {
                        System.out.println("Word: " + word.getTranslationsStr());
                    }
                }
            }
            clientInput = in.nextLine();
        }
    }
}
