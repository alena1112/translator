package com.alenaco.mytranslator.main.controller;

import com.alenaco.mytranslator.main.model.LanguageDirection;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kovalenko
 * @version $Id$
 */
public class HttpSender {
    private static final String API_KEY = "trnsl.1.1.20180214T084739Z.01acd7691b070343.fa3ff14d0d76fdd894bc5559f8e7bfec39c19c8b";
    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";

    public static HttpResult sendRequest(String text, LanguageDirection lang) {
        HttpResult result = new HttpResult();

        DefaultHttpClient httpClient = new DefaultHttpClient();

        try {
            Map<String, String> params = new HashMap<>();
            params.put("key", API_KEY);
            params.put("text", text);
            params.put("lang", lang.getId());

            URI url = buildUri(BASE_URL, params);

            HttpPost request = new HttpPost(url);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            JSONObject obj = new JSONObject(IOUtils.toString(entity.getContent()));
            Integer code = (Integer) obj.get("code");
            JSONArray translation = (JSONArray) obj.get("text");
            result.setResult(code == 200);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < translation.length(); i++) {
                sb.append(translation.get(0));
                if ((i + 1) < translation.length()) {
                    sb.append("\n");
                }
            }
            result.setText(sb.toString());

            EntityUtils.consume(entity);

        } catch (IOException | URISyntaxException e) {
            System.out.println(e.getMessage());
        } finally {
            HttpClientUtils.closeQuietly(httpClient);
        }

        return result;
    }

    private static URI buildUri(String baseUri, Map<String, String> params) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(baseUri);
        for (Map.Entry<String, String> param : params.entrySet()) {
            uriBuilder.addParameter(param.getKey(), param.getValue());
        }
        return uriBuilder.build();
    }
}
