package com.alenaco.mytranslator.main.controller;

import com.alenaco.mytranslator.main.model.Cash;
import com.alenaco.mytranslator.main.model.LanguageDirection;
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
    //ui
    //git, JUnit, documentation

    public static void main(String[] args) {
        Cash cash = new Cash();
        Scanner in = new Scanner(System.in);

        System.out.println("Write a word");

        String clientInput = in.nextLine();

        while (!clientInput.equals("@e")) {
            if (StringUtils.isNotBlank(clientInput)) {

                if (clientInput.equals("@c")) {
                    System.out.println(cash.getCashStr());
                } else {
                    LanguageDirection direction = LanguageUtils.getLanguageDirection(clientInput);
                    Word word = cash.getTranslation(clientInput);
                    if (word == null) {
                        HttpResult result = HttpSender.sendRequest(clientInput, direction);
                        System.out.println("Word: " + result.getText());
                        cash.put(clientInput, result.getText(), direction);
                    } else {
                        System.out.println("Word: " + word.getTranslationsStr());
                    }
                }
            }
            clientInput = in.nextLine();
        }
    }
}
