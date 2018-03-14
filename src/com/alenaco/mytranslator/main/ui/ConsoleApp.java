package com.alenaco.mytranslator.main.ui;

import com.alenaco.mytranslator.main.controller.managers.SessionManager;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
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
                        System.out.println(sessionManager.getCashManager().getCashStr());
                    } else {
                        String translations = sessionManager.translateWord(clientInput);
                        System.out.println("Word: " + translations);
                    }
                }
                clientInput = in.nextLine();
            }
        } catch (StorageException | NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
