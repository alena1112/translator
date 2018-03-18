package com.alenaco.mytranslator.main.controller.utils;

import com.alenaco.mytranslator.main.controller.Named;
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
public class SettingsHelper {

    private static final String MAC_OS = "Mac OS X";

    public static Map<Class, String> getSettingsClasses(Class settingsInterface) throws ClassNotFoundException {
        Map<Class, String> map = new HashMap<>();
        String packageName = settingsInterface.getPackage().getName();
        String osName = System.getProperty("os.name");
        File directory;
        if (osName.equals(MAC_OS)) {
            directory = new File(System.getProperty("user.dir") + "/src/" + packageName.replace(".", "/"));
        } else {
            directory = new File(System.getProperty("user.dir") + "\\src\\" + packageName.replace(".", "\\"));
        }
        List<Class> classes = new ArrayList<>();
        getAllSettingsClasses(settingsInterface, classes, directory, packageName);
        for (Class clazz : classes) {
            Named annotation = (Named) clazz.getAnnotation(Named.class);
            if (annotation != null) {
                map.put(clazz, annotation.value());
            } else {
                map.put(clazz, clazz.getTypeName());
            }
        }
        return map;
    }

    private static void getAllSettingsClasses(Class settingsInterface, List<Class> classes, File directory, String packageName)
            throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
                String fileName = file.getName();
                if (file.isDirectory()) {
                    getAllSettingsClasses(settingsInterface, classes, file, packageName + "." + fileName);
                } else if (fileName.endsWith(".java") || fileName.endsWith(".class")) {
                    Class<?> sClass = Class.forName(packageName + '.' + fileName.substring(0, fileName.lastIndexOf('.')));
                    Class<?>[] sInterfaces = sClass.getInterfaces();
                    if (ArrayUtils.isNotEmpty(sInterfaces)) {
                        for (Class sInterface : sInterfaces) {
                            if (sInterface.equals(settingsInterface)) {
                                classes.add(sClass);
                            }
                        }
                    }
                }
            }
        }
    }

    public static Object getSettingsInstance(Class clazz) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor constructor = clazz.getConstructor();
        return constructor.newInstance();
    }

    public static String getClassName(Class clazz) {
        Named annotation = (Named) clazz.getAnnotation(Named.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }
}
