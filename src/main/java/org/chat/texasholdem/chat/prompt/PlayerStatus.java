package org.chat.texasholdem.chat.prompt;

import com.alibaba.fastjson.JSONObject;
import org.chat.texasholdem.judge.entity.Card;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Class {@code HoldemStatus} 每个玩家回合的当前局状态
 */
@Getter
@Setter
public class PlayerStatus {
    private String playerName;
    private List<Card> yourHand;
    private List<Card> boardCards;
    private int potSize;
    private Map<String, Integer> playerStacks;

    private String bettingRound;
    private String yourPosition;
    private List<PlayerMoveHistory> playerMoveHistoryList;

    public PlayerStatus(String playerName, List<Card> yourHand, List<Card> boardCards,
                        int potSize, Map<String, Integer> playerStacks, String bettingRound,
                        String yourPosition, List<PlayerMoveHistory> playerMoveHistoryList) {
        this.playerName = playerName;
        this.yourHand = yourHand;
        this.boardCards = boardCards;
        this.potSize = potSize;
        this.playerStacks = playerStacks;
        this.bettingRound = bettingRound;
        this.yourPosition = yourPosition;
        this.playerMoveHistoryList = playerMoveHistoryList;
    }

    public JSONObject toJSONObject() {
        return (JSONObject) JSONObject.toJSON(this);
    }

    public int getMaxAmount() {
        int maxAmount = 0;
        for (PlayerMoveHistory playerMoveHistory : this.playerMoveHistoryList) {
            if (playerMoveHistory.getAmount() >= maxAmount)
                maxAmount = playerMoveHistory.getAmount();
        }
        return maxAmount;
    }
}
