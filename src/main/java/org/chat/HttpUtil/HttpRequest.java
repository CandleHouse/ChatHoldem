package org.chat.HttpUtil;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;

public class HttpRequest {
    public static JSONObject jsonPost(String url, Map<String, String> headers, String jsonBody) {
        //1.获得一个httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //2.生成一个post请求
        HttpPost httppost = new HttpPost(url);
        //解决中文乱码问题
        StringEntity entity = new StringEntity(jsonBody, "UTF-8");
        entity.setContentType("application/json");
        httppost.setEntity(entity);
        if (!CollectionUtils.isEmpty(headers)){
            for (Map.Entry<String,String> entry : headers.entrySet()){
                httppost.setHeader(entry.getKey(),entry.getValue());
            }
        }
        CloseableHttpResponse response = null;
        try {
            //3.执行post请求并返回结果
            response = httpclient.execute(httppost);
//                WORKER_LOGGER.info("send request to [" + url + "] with json [" + jsonBody + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildJSONResult(response);
    }

    private static JSONObject buildJSONResult(CloseableHttpResponse response){
        JSONObject json = new JSONObject();
        if (response != null && response.getStatusLine() != null) {
            try {
                json.put("httpCode", response.getStatusLine().getStatusCode());
                String body = EntityUtils.toString(response.getEntity());
                json.put("httpResponseBody",body);
                return json;
            } catch (IOException e) {
                System.out.println("[HttpRequest.buildJSONResult]parse response exception" + e);
//                WORKER_LOGGER.error("[HttpRequest.buildJSONResult]parse response exception",e);
            }
        }else {
            if(response == null) {
                System.out.println("[HttpRequest.buildJSONResult]response is null or fail");
//                WORKER_LOGGER.error("[HttpRequest.buildJSONResult]response is null or fail");
            }else {
                System.out.println("[HttpRequest.buildJSONResult]response status is not 200,statusLine is " + response.getStatusLine());
//                WORKER_LOGGER.error("[HttpRequest.buildJSONResult]response status is not 200,statusLine is "+response.getStatusLine());
                if (response.getStatusLine() != null) {
                    System.out.println("[HttpRequest.buildJSONResult]response status is not 200,status is " + response.getStatusLine().getStatusCode());
//                    WORKER_LOGGER.error("[HttpRequest.buildJSONResult]response status is not 200,status is " + response.getStatusLine().getStatusCode());
                }
            }
        }
        return null ;
    }
}
