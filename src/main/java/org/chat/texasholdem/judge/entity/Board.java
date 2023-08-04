package org.chat.texasholdem.judge.entity;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {

    private int potSize; // 奖池大小
    private List<Card> cards; // 桌子上的牌

    public Board(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> flop() {
        return cards.subList(0, Constants.DESK_CARD_NUMERS-2);
    }

    public List<Card> turn() {
        return cards.subList(0, Constants.DESK_CARD_NUMERS-1);
    }

    public List<Card> river() {
        return cards.subList(0, Constants.DESK_CARD_NUMERS);
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    @Override
    public String toString() {
        return "Board{cards=" + cards + ", potSize=" + potSize + "}";
    }
}
