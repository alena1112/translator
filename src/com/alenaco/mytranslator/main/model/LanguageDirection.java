package com.alenaco.mytranslator.main.model;

/**
 * @author kovalenko
 * @version $Id$
 */
public enum LanguageDirection {
    RU_EN("ru-en"),
    EN_RU("en-ru");

    private String id;

    LanguageDirection(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static LanguageDirection fromId(String id) {
        if (id == null) {
            return null;
        }
        for (LanguageDirection direction : values()) {
            if (direction.id.equals(id)) {
                return direction;
            }
        }
        return null;
    }
}
