package org.chat.texasholdem.chat.entity;

import com.alibaba.fastjson.JSONObject;
import org.chat.texasholdem.chat.entity.PlayerMoveHistory;
import org.chat.texasholdem.judge.entity.Card;

import java.util.ArrayList;
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
    private List<String> yourHand;
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
        this.yourHand = this.specifyYourHand(yourHand);
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

    public List<String> specifyYourHand(List<Card> yourHand) {
        List<String> yourHandStr = new ArrayList<>();
        for (Card card : yourHand) {
            String cardStr = "一张";

            switch (card.getSuit().getName()) {
                case "S":
                    cardStr += "黑桃";
                    break;
                case "H":
                    cardStr += "红心";
                    break;
                case "C":
                    cardStr += "梅花";
                    break;
                case "D":
                    cardStr += "方块";
                    break;
            }
            switch (card.getRank()) {
                case CARD_ACE:
                    cardStr += "A";
                    break;
                case CARD_KING:
                    cardStr += "K";
                    break;
                case CARD_QUEUE:
                    cardStr += "Q";
                    break;
                case CARD_JACK:
                    cardStr += "J";
                    break;
                default:
                    cardStr += card.getRank().getNumber();
                    break;
            }
            yourHandStr.add(cardStr);
        }

        return yourHandStr;
    }
}
