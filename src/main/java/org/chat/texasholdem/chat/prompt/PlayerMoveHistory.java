package org.chat.texasholdem.chat.prompt;

import lombok.Data;

@Data
public class PlayerMoveHistory {
    private String playerName;
    private String actionBettingRound;
    private String action;
    private int amount;

    public PlayerMoveHistory(String playerName, String actionBettingRound, String action, int amount) {
        this.playerName = playerName;
        this.actionBettingRound = actionBettingRound;
        this.action = action;
        this.amount = amount;
    }
}
