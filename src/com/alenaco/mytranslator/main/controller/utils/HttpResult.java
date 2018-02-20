package com.alenaco.mytranslator.main.controller.utils;

import org.json.JSONObject;

/**
 * @author kovalenko
 * @version $Id$
 */
public class HttpResult {
    private boolean successful;
    private JSONObject jsonObject;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
