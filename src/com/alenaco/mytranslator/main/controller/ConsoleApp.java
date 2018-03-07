package com.alenaco.mytranslator.main.controller;

import com.alenaco.mytranslator.main.controller.managers.SessionManager;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.utils.LanguageUtils;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.translator.TranslatorResult;
import com.alenaco.mytranslator.main.controller.translator.yandex.YandexTranslator;
import com.alenaco.mytranslator.main.model.Cash;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

/**
 * @author kovalenko
 * @version $Id$
 */
public class ConsoleApp {

    public static void main(String[] args) {
        try {
            SessionManager sessionManager = new SessionManager();
            Scanner in = new Scanner(System.in);

            System.out.println("Write a word");

            String clientInput = in.nextLine();

            while (!clientInput.equals("@e")) {
                if (StringUtils.isNotBlank(clientInput)) {

                    if (clientInput.equals("@c")) {
                        System.out.println(sessionManager.getCashStr());
                    } else {
                        Word word = sessionManager.translateWord(clientInput);
                        System.out.println("Word: " + sessionManager.getTranslationsStr(word));
                    }
                }
                clientInput = in.nextLine();
            }
        } catch (StorageException | NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
