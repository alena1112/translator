package com.alenaco.mytranslator.main.controller.translator;

import org.apache.commons.lang.ArrayUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alena on 22.02.18.
 */
public class TranslatorHelper {

    public static Map<Class<Translator>, String> getTranslators() throws ClassNotFoundException {
        Map<Class<Translator>, String> translators = new HashMap<>();
        String packageName = Translator.class.getPackage().getName();
        File directory = new File(System.getProperty("user.dir") + "\\src\\" + packageName.replace(".", "\\"));
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
                String fileName = file.getName();
                if (file.isDirectory()) {
                    getAllTranslatorClasses(classes, file, packageName + "." + fileName);
                } else if (fileName.endsWith(".java") || fileName.endsWith(".class")) {
                    Class<?> tClass = Class.forName(packageName + '.' + fileName.substring(0, fileName.lastIndexOf('.')));
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

    public static Translator getTranslatorInstance(Class<Translator> clazz) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<Translator> constructor = clazz.getConstructor();
        return constructor.newInstance();
    }
}
