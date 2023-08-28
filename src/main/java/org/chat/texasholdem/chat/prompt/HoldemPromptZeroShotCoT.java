package org.chat.texasholdem.chat.prompt;

import lombok.Getter;
import lombok.Setter;
import org.chat.texasholdem.chat.entity.PlayerStatus;

@Getter
@Setter
public class HoldemPromptZeroShotCoT {


    private int playerNum;
    private int yourStack;
    private int bigBlind;
    private int smallBlind;
    private PlayerStatus playerStatus;
    private String combinedPrompt;

    public HoldemPromptZeroShotCoT(int playerNum, int yourStack, int bigBlind, int smallBlind, PlayerStatus playerStatus) {
        this.playerNum = playerNum;
        this.yourStack = yourStack;
        this.bigBlind = bigBlind;
        this.smallBlind = smallBlind;
        this.playerStatus = playerStatus;
    }

    public HoldemPromptZeroShotCoT(HoldemPrompt holdemPrompt) {
        this.playerNum = holdemPrompt.getPlayerNum();
        this.yourStack = holdemPrompt.getYourStack();
        this.bigBlind = holdemPrompt.getBigBlind();
        this.smallBlind = holdemPrompt.getSmallBlind();
        this.playerStatus = holdemPrompt.getPlayerStatus();
    }

    public String firstPrompt() {
        String gameTarget = "我希望你扮演一名德州扑克玩家。德州扑克是一种与德克萨斯扑克规则相同的游戏。" +
                            "在这个游戏中，你将会和其他" + this.playerNum + "名玩家一起玩牌。" +
                            "你的初始筹码数量为" + this.yourStack + "。\n";

        String customInput = "在游戏的每个阶段，你将收到一个包含当前上下文的JSON对象：" +
                            "你已经得到的牌，JSON数组中每个元素代表一张牌(yourHand)；" +
                            "公共牌(boardCards, 若无该属性表示当前为pre-flop)；" +
                            "当前卡池下注的总共筹码数(potSize)；" +
                            "其他玩家的筹码数(playerStacks)；" +
                            "当前轮次(bettingRound)；" +
                            "你的位置(yourPosition)；" +
                            "以及所有玩家行动历史记录(playerMoveHistoryList)。\n";

        String zeroShotCoT = "Let's think step by step，" +
                            "首先，你需要根据你的手牌(yourHand)和公共牌(boardCards)来判断你当前的最大牌型," +
                            "如果处于pre-flop没有公共牌时，最好选择跟注call，不判断最大牌型；" +
                            "然后，根据所有玩家行动历史记录(playerMoveHistoryList)，推测其他玩家的牌型，是否在咋唬，" +
                            "不考虑历史记录中大小盲行动，因为大小盲是强制下注；" +
                            "接着，根据当前卡池下注的总共筹码数(potSize)，你的位置(yourPosition)，当前轮次(bettingRound)，" +
                            "等其他信息作出综合分析，判断你的胜率；" +
                            "最后，你需要根据你当前的胜率来判断你当前的行动，是否加注、跟牌或弃牌。\n";
//        String zeroShotCoT = "Let's think step by step，给出逐步的分析，并给出最终的行动。\n";

        String nowStatus = "你是玩家" + this.playerStatus.getPlayerName() + "，你当前的数据如下：\n" +
                this.playerStatus.toJSONObject().toJSONString();

        return gameTarget + customInput + zeroShotCoT + nowStatus;
    }

    public String secondPrompt(String chatHoldemAns) {
        String thinking = "\n你做出了如下思考：\n";

        String gameRules = "\n在每一轮中，你必须选择跟注(call)、加注(raise)、弃牌(fold)或者全下(all in)之一，不许check。" +
                            "如果能组成 one pair, two pairs, three of a kind, straight, flush, full house, four of a kind, " +
                            "这些牌型的胜率较大，你可以逐级选择跟注(call)或者加注(raise)，不要轻易放弃。" +
                            "如果你作为" + this.playerStatus.getPlayerName() +
                            "在之前的行动下注金额较大，不要轻易放弃。\n";

        String customOutput = "根据上面的分析，请作出行动：\n" +
                            "返回 valid JSON format：" +
                            "action属性描述下一步动作，必须是call, raise, fold, all in 之一；" +
                            "amount属性为下注金额，为整数，" +
                            "必须大于等于" + this.playerStatus.getMaxAmount() + "，" +
                            "必须小于等于" + this.yourStack + "，" +
                            "弃牌时请将该属性赋值整数0；" +
                            "reason属性，总结思考过程，解释行动原因，使用中文；" +
                            "speak发表迷惑对手的语言，风趣幽默，千万千万不要透露自己的牌面信息，使用中文。\n";

         return this.firstPrompt() + thinking + chatHoldemAns + gameRules + customOutput;
    }

}
