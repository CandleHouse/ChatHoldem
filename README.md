# ChatHoldem: LLM enabled texasholdem

快来和 LLM 玩德州扑克吧，赢了它你就赢了人类智慧结晶！

### How to play

1. 通过访问[智谱AI](https://open.bigmodel.cn/usercenter/apikeys)，获取自己的{API key}.{API secret}，添加到如下文件中：
```
src/main/java/org/chat/HttpUtil/API/ChatGLMAPI.java
```
2. run StartGame.java

```
src/main/java/org/chat/texasholdem/StartGame.java
```


### What can be tuned
- 以 ChatGLM 为核心的系列参数调整，包括更换模型，调整模型参数等，详见[官网](https://open.bigmodel.cn/dev/api#overview)
- 自行购买 ChatGPT API key，实现 `ChatGPTAPI.java` 方法
- 以代理形式更换其他模型，实现 `ChatProxyAPI.java` 方法

### Now support
1. 更换模型内核(目前仅支持 ChatGLM)
2. 四种操作 `call`, `raise`, `fold`, `all in`
2. 金手指，看所有人的牌
3. 结算时挑出最大手牌，自动比牌
4. 自定义玩家个数，你想打几个？
5. 游戏难度 `EASY`, `NORMAL`
6. LLM prompt 打印开关 `allPromptsPrint`


### Notice
- ChatHoldem prompt系列工程针对 ChatGPT 优化，可能在 ChatGLM 表现欠佳
- 感谢 [KingDomPan/texasholdem](https://github.com/KingDomPan/texasholdem) 提供的摊牌比较算法