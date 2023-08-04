package org.chat.texasholdem;

import org.chat.texasholdem.gameFlow.MainFlow;

public class StartGame {


    public static void main(String[] args) {

        int playerNum = 6;
        boolean goldFinger = true;

        MainFlow mainFlow = new MainFlow(playerNum,goldFinger);
        mainFlow.play();
    }
}
