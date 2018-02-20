package com.alenaco.mytranslator.main.controller;

/**
 * @author kovalenko
 * @version $Id$
 */
public class HttpResult {
    private boolean result;
    private String text;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
