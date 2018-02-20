package com.alenaco.mytranslator.main.controller.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author kovalenko
 * @version $Id$
 */
public class HttpSender {

    public static HttpResult sendRequest(String baseUrl, Map<String, String> params) {
        HttpResult result = new HttpResult();
        result.setSuccessful(false);

        DefaultHttpClient httpClient = new DefaultHttpClient();

        try {
            URI url = buildUri(baseUrl, params);
            HttpPost request = new HttpPost(url);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            JSONObject obj = new JSONObject(IOUtils.toString(entity.getContent()));
            result.setSuccessful(true);
            result.setJsonObject(obj);

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
