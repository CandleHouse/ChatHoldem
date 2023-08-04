package org.chat.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Auther: 楚月
 * @Date: 2020/5/14.
 */
public class HttpUtils {

    private static final int SOCKET_TIMEOUT = 60000;
    // connection timeout is the timeout until a connection with the server is
    // established.
    private static final int SOCKET_CONNECT_TIMEOUT = 60000;
    // connectionRequestTimeout used when requesting a connection from the
    // connection manager.
    private static final int SOCKET_CONNECTION_REQUEST_TIMEOUT = 1000;

    private static final String UTF_8 = "UTF-8";

    private static final String CONTENT_TYPE = "Content-Type";


    public static HttpResponseMeta httpPost(String url, String payload, Map<String, String> headMap,
                                            CookieStore cookieStore) throws IOException {
//        logger.info("send request to [" + url + "] with payload [" + payload + "] and head [" + headMap.toString()
//                + "] and cookie [" + cookieStore.getCookies().toString() + "]");
        HttpPost httpRequest = new HttpPost(url);
        headMap.entrySet().stream().forEach(h -> {
            httpRequest.addHeader(String.valueOf(h.getKey()), String.valueOf(h.getValue()));
        });

        // set content-type
        ContentType contentType = ContentType.APPLICATION_JSON;
        if (headMap.containsKey(CONTENT_TYPE)) {
            try {
                contentType = ContentType.parse(headMap.get(CONTENT_TYPE));
            } catch (Exception e) {
                System.out.println(e);
//                logger.error("", e);
            }
        }
        StringEntity reqEntity = new StringEntity(payload, contentType);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(SOCKET_CONNECT_TIMEOUT).build();
        httpRequest.setConfig(requestConfig);
        httpRequest.setEntity(reqEntity);
        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build()) {
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            return formatResponseMeta(httpResponse, UTF_8);
        }
    }

    private static HttpResponseMeta formatResponseMeta(HttpResponse response, String charset) {
        HttpResponseMeta meta = new HttpResponseMeta();
        if (response == null) {
            meta.setStatusCode(999);
            meta.setResult("invalid response");
        } else {
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                meta.setStatusCode(statusCode);
                String result = EntityUtils.toString(response.getEntity(), charset);
                meta.setResult(result);
                System.out.println("receive http result: [" + statusCode + "][" + result + "]");
//                logger.info("receive http result: [" + statusCode + "][" + result + "]");
                response.getEntity().getContent().close();
            } catch (IllegalStateException | UnsupportedOperationException | IOException e) {
                System.out.println("formatResponseMeta error: " + e);
//                logger.error("formatResponseMeta error: ", e);
            }
        }
        return meta;
    }

}

