package org.chat.texasholdem.chat.prompt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.chat.texasholdem.chat.entity.PlayerStatus;

@Getter
@Setter
public class HoldemPrompt {


    private int playerNum;
    private int yourStack;
    private int bigBlind;
    private int smallBlind;
    private PlayerStatus playerStatus;
    private String combinedPrompt;

    public HoldemPrompt(int playerNum, int yourStack, int bigBlind, int smallBlind, PlayerStatus playerStatus) {
        this.playerNum = playerNum;
        this.yourStack = yourStack;
        this.bigBlind = bigBlind;
        this.smallBlind = smallBlind;
        this.playerStatus = playerStatus;
        this.combinedPrompt = this.init();
    }
    private String init() {
        String gameTarget = "我希望你扮演一名德州扑克玩家。德州扑克是一种与德克萨斯扑克规则相同的游戏。" +
                            "在这个游戏中，你将会和其他" + this.playerNum + "名玩家一起玩牌。" +
                            "你的初始筹码数量为" + this.yourStack + "。\n" +
                            "大盲注" + this.bigBlind + "，小盲注" + this.smallBlind + "。\n" +
                            "每个玩家会被发两张牌，然后会有三张公共牌，然后是一张公共牌，最后是一张公共牌。" +
                            "你的目标是使用你的两张牌和公共牌中的五张牌来组成最好的牌。\n";
        String gameRules = "在每一轮中，你可以选择跟注、加注、弃牌或者全下。" +
                            "call: 跟注是指你和其他玩家下注相同的筹码。" +
                            "raise: 加注是指你下注更多的筹码。" +
                            "fold: 弃牌是指你放弃这一轮的牌，你将不会再参与这一轮的游戏。" +
                            "all in: 全下是指你将所有的筹码都下注。\n";

        String customInput = "在游戏的每个阶段，你将收到一个包含当前上下文的JSON对象：" +
                            "你已经得到的两张牌(yourHand)，" +
                            "公共牌(boardCards, 若无该属性表示当前为pre-flop)，" +
                            "当前卡池下注的总共筹码数(potSize)，" +
                            "其他玩家的筹码数(playerStacks)，" +
                            "当前轮次(bettingRound)，" +
                            "你的位置(yourPosition)，" +
                            "以及所有玩家行动历史记录(playerMoveHistoryList)。\n";

        String customOutput = "Let's think step by step，根据你的手牌、公共牌、筹码以及其他玩家行动一步步分析，" +
                            "决定你的下一步操作并返回 valid JSON format。" +
                            "只能返回有如下四个属性的JSON数据，不能嵌套：" +
                            "action属性描述下一步动作，只能是call, raise, fold, all in 之一；" +
                            "amount属性为下注金额，为整数，" +
                            "必须大于等于" + this.playerStatus.getMaxAmount() + "，" +
                            "必须小于等于" + this.yourStack + "，" +
                            "弃牌时请将该属性赋值整数0；" +
                            "reason属性解释行动原因，使用中文；" +
                            "speak发表迷惑对手的语言，使用中文。\n";

        String nowStatus = "你是玩家" + this.playerStatus.getPlayerName() + "，你当前的数据如下：\n" +
                            this.playerStatus.toJSONObject().toJSONString();

        return gameTarget + gameRules + customInput + customOutput + nowStatus;
    }

    private JSONObject jsonContext() {
        JSONObject context = new JSONObject();

        JSONObject handCard1 = new JSONObject();
        handCard1.put("suit", "hearts"); handCard1.put("rank", 5);
        JSONObject handCard2 = new JSONObject();
        handCard2.put("suit", "diamonds"); handCard2.put("rank", 5);
        JSONArray handCards = new JSONArray();
        handCards.add(handCard1); handCards.add(handCard2);
        context.put("your hand", handCards);

        JSONObject playersStacks = new JSONObject();
        playersStacks.put("you", 90);
        playersStacks.put("player1", 80);
        context.put("players stacks", playersStacks);

        context.put("pot size", 30);
        context.put("betting round", "pre flop");
        context.put("your position", "small blind");
        context.put("player1's last move", null);

        return context;
    }

}
