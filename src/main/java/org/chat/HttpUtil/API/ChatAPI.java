package org.chat.HttpUtil.API;

import com.alibaba.fastjson.JSONObject;
import org.chat.texasholdem.chat.entity.ChatCore;

public abstract class ChatAPI {
    public abstract JSONObject chatAns(String combinedPrompt);

    public abstract String getContent(JSONObject res);
}
