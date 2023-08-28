package org.chat.HttpUtil.API;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.chat.HttpUtil.HttpRequest;

import java.util.Calendar;
import java.util.HashMap;

/*
    用于调用ChatGLM模型的API: https://open.bigmodel.cn/dev/api#overview
 */
public class ChatGLMAPI extends ChatAPI{

    private final static String API_KEY = "";

    private String createJwt(String apiKey) {
        String[] arrStr = apiKey.split("\\.");
        String subApiKey = arrStr[0];
        String subApiSecret = arrStr[1];

        Algorithm alg;
        try {
            alg = Algorithm.HMAC256(subApiSecret.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("api_key", subApiKey);
        payload.put("exp", System.currentTimeMillis() + 30 * 60 * 1000);
        payload.put("timestamp", Calendar.getInstance().getTimeInMillis());
        HashMap<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", "HS256");
        headerClaims.put("sign_type", "SIGN");
        String token = JWT.create().withPayload(payload).withHeader(headerClaims).sign(alg);

        return token;
    }

    @Override
    public JSONObject chatAns(String combinedPrompt) {

        String model = "chatglm_pro";  // chatglm_std, chatglm_lite, text_embedding
        String API_URL = "https://open.bigmodel.cn/api/paas/v3/model-api/" + model + "/invoke";

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", createJwt(API_KEY));

        JSONObject chatPrompt = new JSONObject();
        chatPrompt.put("role", "user");
        chatPrompt.put("content", combinedPrompt);
        JSONArray prompt = new JSONArray();
        prompt.add(chatPrompt);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("prompt", prompt);
//        jsonBody.put("temperature", 0.5);
//        jsonBody.put("top_p", 0.5);
        JSONObject res = HttpRequest.jsonPost(API_URL, headers, jsonBody.toJSONString());

        return res;
    }

    @Override
    public String getContent(JSONObject res) {
        String content = res.getJSONObject("httpResponseBody").getJSONObject("data").
                getJSONArray("choices").getJSONObject(0).getString("content");
        return content;
    }
}
