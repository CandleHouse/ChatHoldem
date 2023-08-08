package org.chat.texasholdem.chat;

import com.alibaba.fastjson.JSONObject;
import org.chat.HttpUtil.HttpRequest;
import org.chat.texasholdem.chat.prompt.HoldemPrompt;
import org.chat.texasholdem.chat.prompt.HoldemPromptZeroShotCoT;
import org.chat.texasholdem.judge.entity.Constants;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatHoldem {

    private final static String CHATGPT_PROXY_URL = "https://pre-morrox.alibaba-inc.com/api/proxy/aigc/question?type=chatgpt";
    private final static String CHATGLM_PROXY_URL = "https://pre-morrox.alibaba-inc.com/api/proxy/aigc/question?type=chatglm";
    private final static String Cookie = "";

    /**
     * 一次询问获得结果
     */
    public JSONObject chatHoldemZeroShot(HoldemPrompt holdemPrompt, boolean allPromptsPrint) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Cookie", Cookie);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("text", holdemPrompt.getCombinedPrompt());
        if (allPromptsPrint)
            System.out.println(holdemPrompt.getCombinedPrompt());

        JSONObject res = HttpRequest.jsonPost(CHATGPT_PROXY_URL, headers, jsonBody.toJSONString());

        int retryCount = 0;
        String chatHoldemAns = null;
        while (retryCount++ < Constants.MAX_RETRY_TIMES) {
            // 请求出错
            if (res.getInteger("httpCode") != 200) {
                System.out.println("chatHoldemAns is absent, retrying...");
                res = HttpRequest.jsonPost(CHATGPT_PROXY_URL, headers, jsonBody.toJSONString());
                continue;
            }
            // 请求成功，返回正忙
            chatHoldemAns = res.getJSONObject("httpResponseBody").getJSONObject("data").getString("chatgpt");
            if (chatHoldemAns == null) {
                System.out.println("chatHoldemAns is busy, retrying...");
                res = HttpRequest.jsonPost(CHATGPT_PROXY_URL, headers, jsonBody.toJSONString());
                continue;
            }
            // 请求成功，检查返回格式是否为JSON
            if (chatHoldemAns != null) {
                try {
                    JSONObject.parseObject(chatHoldemAnsFilter(chatHoldemAns)).get("action");
                    JSONObject.parseObject(chatHoldemAnsFilter(chatHoldemAns)).get("amount");
                    JSONObject.parseObject(chatHoldemAnsFilter(chatHoldemAns)).get("reason");
                    break;
                } catch (Exception e) {
                    // 返回格式不是JSON，重试
                    System.out.println("chatHoldemAns is in nonsense, retrying...");
                    res = HttpRequest.jsonPost(CHATGPT_PROXY_URL, headers, jsonBody.toJSONString());
                    continue;
                }
            }
        }
        // 超过最大重试次数，放弃思考
        if (retryCount >= Constants.MAX_RETRY_TIMES) {
            JSONObject context = new JSONObject();
            context.put("action", "fold");
            context.put("amount", 0);
            context.put("reason", "timeout");
            context.put("speak", "好吧，我放弃思考了。");
            chatHoldemAns = context.toJSONString();
        }

        return JSONObject.parseObject(chatHoldemAnsFilter(chatHoldemAns));
    }

    /**
     * 一次询问 + chain of thoughts 获得结果
     */
    public JSONObject chatHoldemZeroShotCoT(HoldemPrompt holdemPrompt, boolean allPromptsPrint) {
        HoldemPromptZeroShotCoT holdemPromptZeroShotCoT = new HoldemPromptZeroShotCoT(holdemPrompt);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Cookie", Cookie);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("text", holdemPromptZeroShotCoT.firstPrompt());

        JSONObject res = HttpRequest.jsonPost(CHATGPT_PROXY_URL, headers, jsonBody.toJSONString());

        int retryCount = 0;
        String chatHoldemAns = null; String secondPrompt = null;
        while (retryCount++ < Constants.MAX_RETRY_TIMES) {
            // 请求出错
            if (res.getInteger("httpCode") != 200) {
                System.out.println("chatHoldemAns is absent, retrying...");
                res = HttpRequest.jsonPost(CHATGPT_PROXY_URL, headers, jsonBody.toJSONString());
                continue;
            }
            // 请求成功，返回正忙
            chatHoldemAns = res.getJSONObject("httpResponseBody").getJSONObject("data").getString("chatgpt");
            if (chatHoldemAns == null || (chatHoldemAns != null && chatHoldemAns.length() < 20)) {
                System.out.println("chatHoldemAns is busy, retrying...");
                res = HttpRequest.jsonPost(CHATGPT_PROXY_URL, headers, jsonBody.toJSONString());
                continue;
            }
            // 请求成功，用结果合成第二次prompt
            if (chatHoldemAns != null && chatHoldemAns.length() >= 20) {
                holdemPrompt.setCombinedPrompt(holdemPromptZeroShotCoT.secondPrompt(chatHoldemAns));
                break;
            }
        }

        return chatHoldemZeroShot(holdemPrompt, allPromptsPrint);
    }

    /**
     * 过滤非JSON字符串内容
     */
    public String chatHoldemAnsFilter(String chatHoldemAns) {
        Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
        Matcher matcher = pattern.matcher(chatHoldemAns);
        String filteredChatHoldemAns = "";
        while (matcher.find()) {
            String match = matcher.group();
            filteredChatHoldemAns += match;
        }

        return filteredChatHoldemAns;
    }

}