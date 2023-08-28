package org.chat.HttpUtil.API;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.List;


public class ChatGPTAPI {


    public static void main(String[] args) {
        String apiKey = "";
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String proxyHost = "127.0.0.1";
        int proxyPort = 10809;

        // Set up proxy configuration
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            JSONObject prompt = new JSONObject();
            prompt.put("role", "system");
            prompt.put("content", "用java写个汉诺塔");

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("model", "gpt-3.5-turbo");
            jsonRequest.put("messages", new JSONArray().add(prompt));

            StringEntity entity = new StringEntity(jsonRequest.toString());
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();
                String jsonResponse = EntityUtils.toString(responseEntity);
                System.out.println(jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}