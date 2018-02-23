package com.alenaco.mytranslator.main.controller.translator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by alena on 22.02.18.
 */
public class TranslatorHelper {

    public static Map<Class<Translator>, String> getTranslators() throws ClassNotFoundException {
        Map<Class<Translator>, String> translators = new HashMap<>();
        String packageName = Translator.class.getPackage().getName();
        File directory = new File(System.getProperty("user.dir") + "/src/" + "com/alenaco/mytranslator/main/controller/translator");
        List<Class<Translator>> classes = new ArrayList<>();
        getAllTranslatorClasses(classes, directory, packageName);
        for (Class<Translator> clazz : classes) {
            Named annotation = clazz.getAnnotation(Named.class);
            if (annotation != null) {
                translators.put(clazz, annotation.name());
            } else {
                translators.put(clazz, clazz.getTypeName());
            }
        }
        return translators;
    }

    private static void getAllTranslatorClasses(List<Class<Translator>> classes, File directory, String packageName)
            throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getAllTranslatorClasses(classes, file, packageName);
                } else if (file.getName().endsWith(".java") || file.getName().endsWith(".class")) {
                    Class<?> tClass = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                    Class<?>[] tInterfaces = tClass.getInterfaces();
                    if (ArrayUtils.isNotEmpty(tInterfaces)) {
                        for (Class tInterface : tInterfaces) {
                            if (tInterface.equals(Translator.class)) {
                                classes.add((Class<Translator>) tClass);
                            }
                        }
                    }
                }
            }
        }
    }

    public static Translator getTranslatorInstance(Class<Translator> clazz) {
        return null;
    }
}
