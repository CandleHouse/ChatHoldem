package org.chat.texasholdem.gameFlow;

import com.alibaba.fastjson.JSONObject;
import org.chat.texasholdem.chat.ChatHoldem;
import org.chat.texasholdem.chat.prompt.HoldemPrompt;
import org.chat.texasholdem.chat.prompt.PlayerMoveHistory;
import org.chat.texasholdem.chat.prompt.PlayerStatus;
import org.chat.texasholdem.judge.entity.Card;
import org.chat.texasholdem.judge.entity.Constants;
import org.chat.texasholdem.judge.entity.Dealer;
import org.chat.texasholdem.judge.entity.Player;

import java.util.*;

public class MainFlow {
    private int playerNum;
    private int initCounter;
    private int bigBlind;
    private int smallBlind;
    private boolean goldFinger;

    private Map<String, Integer> playerStacks;  // 时刻更新的玩家筹码
    private Map<String, Integer> playerStacksUpdatedOnSession;  // 每局session更新的玩家筹码
    private List<PlayerMoveHistory> playerMoveHistoryListUpdatedOnSession;  // 每局session的玩家行动历史
    private List<Player> allPlayerListUpdatedOnSession;  // 每局session参与的玩家
    private List<String> foldPlayerNameList;  // 每轮round弃牌玩家名称列表
    private Dealer dealer;
    private CommonUtils commonUtils = new CommonUtils();

    public MainFlow(int playerNum, boolean goldFinger) {
        this.playerNum = playerNum;
        this.initCounter = 2000;
        this.bigBlind = 20;
        this.smallBlind = 10;
        this.goldFinger = goldFinger;

        playerStacks = new HashMap<>();
        playerMoveHistoryListUpdatedOnSession = new ArrayList<>();
        foldPlayerNameList = new ArrayList<>();

        this.init();
    }

    public void init() {
        this.dealer = new Dealer();

        Player me = new Player("Me");
        this.dealer.join(me);

        for (int i = 0; i < playerNum; i++) {
            Player p = new Player("Player" + i);
            this.dealer.join(p);
        }

        this.dealer.start();  // 荷官发牌完毕
        this.dealer.board.setPotSize(0);  // 初始化底池

        for (Player player: this.dealer.getPlayerList()) {  // 所有人分配筹码
            String playerName = player.getPlayerName();
            playerStacks.put(playerName, this.initCounter);
        }

        playerStacksUpdatedOnSession = new HashMap<>(this.playerStacks);
        allPlayerListUpdatedOnSession = new ArrayList<>(this.dealer.getPlayerList());
    }

    public void stackUpdatedByPosition(int smallBlindIndex, int bigBlindIndex) {
        for (int i = 0; i < this.dealer.getPlayerList().size(); i++) {
            Player player = this.dealer.getPlayerList().get(i);
            int playerIndex = this.getPlayerIndexOnSession(player);
            String playerName = player.getPlayerName();

            String yourPosition = commonUtils.getYourPosition(playerIndex, smallBlindIndex, bigBlindIndex);
            if (yourPosition == null)
                ;
            else if (yourPosition.equals("small blind"))
                this.playerStacks.put(playerName, this.playerStacks.get(playerName) - this.smallBlind);
            else if (yourPosition.equals("big blind"))
                this.playerStacks.put(playerName, this.playerStacks.get(playerName) - this.bigBlind);
        }

        this.dealer.board.setPotSize(this.smallBlind + this.bigBlind);
    }

    public List<Card> getBoardCards(int nowRound) {
        if (nowRound == 0)  // pre-flop
            return null;
        if (nowRound == 1)  // flop
            return this.dealer.board.flop();
        if (nowRound == 2)  // turn
            return this.dealer.board.turn();
        if (nowRound == 3)  // river
            return this.dealer.board.river();

        return null;
    }

    /**
     * @param playerName
     * @param nowRound
     * @param playerAction
     * @param playerAmount 玩家每round总下注的金额
     * @return
     */
    public void playerActionDealer(String playerName, int nowRound, String playerAction, int playerAmount) {
        // 寻找历史最大金额
        int maxAmount = 0;
        for (PlayerMoveHistory playerMoveHistory : this.playerMoveHistoryListUpdatedOnSession) {
            if (playerMoveHistory.getAmount() >= maxAmount)
                maxAmount = playerMoveHistory.getAmount();
        }

        // 根据playAction重新修改金额，增强鲁棒性
        switch (playerAction) {
            case "call":
                playerAmount = maxAmount;
                break;
            case "raise":
                if (playerAmount < maxAmount) playerAmount = maxAmount;
                else if (playerAmount > this.playerStacksUpdatedOnSession.get(playerName)) {
                    playerAmount = this.playerStacksUpdatedOnSession.get(playerName);
                    playerAction = "all in";
                }
                break;
            case "fold":
                playerAmount = this.playerStacksUpdatedOnSession.get(playerName) - this.playerStacks.get(playerName);
                this.foldPlayerNameList.add(playerName);
                break;
            case "all in":
                playerAmount = this.playerStacksUpdatedOnSession.get(playerName);
                break;
        }

        // 更新底池、玩家筹码、玩家行动历史
        this.dealer.board.setPotSize(
                this.dealer.board.getPotSize() + playerAmount -
                        (this.playerStacksUpdatedOnSession.get(playerName) - this.playerStacks.get(playerName))
        );
        this.playerStacks.put(playerName, this.playerStacksUpdatedOnSession.get(playerName) - playerAmount);
        this.playerMoveHistoryListUpdatedOnSession.add(
                new PlayerMoveHistory(playerName, commonUtils.getBettingRound(nowRound), playerAction, playerAmount)
        );

        System.out.print("[玩家" + playerName + "行动]：" + playerAction + " " + playerAmount);
        System.out.println(" (potSize: " + this.dealer.board.getPotSize() + ")\n");
    }

    public Player getSessionWinner(List<Player> sessionRemainingPlayers) {
        if (sessionRemainingPlayers.size() == 0) {
            String winnerName = this.playerMoveHistoryListUpdatedOnSession.get(
                    this.playerMoveHistoryListUpdatedOnSession.size() - 1
            ).getPlayerName();
            return commonUtils.searchPlayerByName(winnerName, this.allPlayerListUpdatedOnSession);
        } else {
            return sessionRemainingPlayers.get(0);
        }
    }

    public void layYourCardsOnTheTable(int nowRound, int nowSession) {
        // 比较玩家手牌
        this.dealer.showHand(nowRound);
        List<Player> sessionRemainingPlayers = this.dealer.getRankingPlayers();
        // 更新赢家筹码
        String winnerName = getSessionWinner(sessionRemainingPlayers).getPlayerName();
        this.playerStacks.put(winnerName, this.playerStacks.get(winnerName) + this.dealer.board.getPotSize());

        // 输出
        System.out.println("=======【 Session " + nowSession + " Finish 】=======");
        System.out.println(this.dealer.board);
        System.out.println("=======【      排名   ↓     】=======");
        for (Player player : sessionRemainingPlayers) {
            System.out.println(player.toString());
        }
        System.out.println("=======【      剩余筹码      】=======");
        for (Player player : this.allPlayerListUpdatedOnSession) {
            System.out.println(player.getPlayerName() + ": " + this.playerStacks.get(player.getPlayerName()));
        }
        System.out.println();

        // 清空与更新session内数据，开启下一轮
        this.playerMoveHistoryListUpdatedOnSession.clear();  // 清空session玩家行动历史
        this.playerStacksUpdatedOnSession = new HashMap<>(this.playerStacks);  // 更新session玩家筹码

        Iterator<Player> iterator = this.allPlayerListUpdatedOnSession.iterator();  // 检查玩家筹码
        while (iterator.hasNext()) {
            Player player = iterator.next(); player.dropCards();  // 清空玩家手牌
            if (this.playerStacks.get(player.getPlayerName()) == 0) {
                iterator.remove(); this.playerNum--;
                System.out.println("玩家" + player.getPlayerName() + "已淘汰");
            }
        }

        this.dealer = new Dealer(this.allPlayerListUpdatedOnSession);  // 更新session玩家列表
        this.dealer.start();  // 荷官发牌完毕
        this.dealer.board.setPotSize(0);  // 初始化底池
    }

    public void cleanFoldPlayers() {
        for (String playerName: this.foldPlayerNameList) {
            this.dealer.playerFold(playerName);
        }
        this.foldPlayerNameList.clear();
    }

    public void printRoundPlayers(String playerName) {
        String playerList = "[" + (this.playerNum+1) + " Players]: ";
        for (Player player : this.dealer.getPlayerList()) {
            if (player.getPlayerName().equals(playerName)) {
                String playerNameHighlight = commonUtils.getColoredString(playerName, 33, 1);
                playerList += "{" + playerNameHighlight + "} ";
            }
            else
                playerList += player.getPlayerName() + " ";
        }
        System.out.println(playerList);
    }

    public int getPlayerIndexOnSession(Player player) {
        for (int i = 0; i < this.allPlayerListUpdatedOnSession.size(); i++) {
            if (this.allPlayerListUpdatedOnSession.get(i).getPlayerName().equals(player.getPlayerName()))
                return i;
        }

        return -1;
    }

    public void myTurn(int nowRound, int playerIndex, Player player,
                       int smallBlindIndex, int bigBlindIndex, boolean roundCall) {
        System.out.print("Your turn: ");
        commonUtils.printYourPosition(playerIndex, smallBlindIndex, bigBlindIndex);
        System.out.println("桌子上的牌：" + this.getBoardCards(nowRound));
        System.out.print("你的手牌：" + player.getCards().toString());
        String playerStackHighlight = commonUtils.getColoredString(
                String.valueOf(this.playerStacks.get(player.getPlayerName())), 34, 3
        );
        System.out.println("  剩余筹码：" + playerStackHighlight);

        String playerAction = ""; int playerAmount = -1;
        if (!roundCall) {
            System.out.println("=> 键入操作：[call] [raise] [fold] [all in]");
            Scanner scanner = new Scanner(System.in);
            playerAction = scanner.nextLine();
            playerAmount = playerAction.equals("raise")? scanner.nextInt(): -1;
        } else {
            System.out.println("=> 键入操作：[call] [fold]");
            Scanner scanner = new Scanner(System.in);
            playerAction = scanner.nextLine();
            // 交给playerActionDealer修正playerAmount
        }
        this.playerActionDealer(player.getPlayerName(), nowRound, playerAction, playerAmount);
    }

    public void playerTurn(int nowRound, int playerIndex, Player player,
                           int smallBlindIndex, int bigBlindIndex, boolean roundCall) {
        System.out.print(player.getPlayerName() + "'s turn: ");
        commonUtils.printYourPosition(playerIndex, smallBlindIndex, bigBlindIndex);

        if (this.goldFinger) {
            System.out.println("[GoldFinger]: 桌子上的牌：" + this.getBoardCards(nowRound));
            System.out.print("[GoldFinger]: " + player.getPlayerName() + "的手牌：" + player.getCards().toString());
            String playerStackHighlight = commonUtils.getColoredString(
                    String.valueOf(this.playerStacks.get(player.getPlayerName())), 34, 3
            );
            System.out.println("  剩余筹码：" + playerStackHighlight);
        }

        String playerAction = ""; int playerAmount = -1;
        if (!roundCall) {
            // chatHoldem AI
            PlayerStatus playerStatus = new PlayerStatus(
                    player.getPlayerName(), player.getCards(), this.getBoardCards(nowRound),
                    this.dealer.board.getPotSize(), this.playerStacks, commonUtils.getBettingRound(nowRound),
                    commonUtils.getYourPosition(playerIndex, smallBlindIndex, bigBlindIndex),
                    playerMoveHistoryListUpdatedOnSession
            );

            HoldemPrompt prompt = new HoldemPrompt(
                    this.playerNum, this.playerStacksUpdatedOnSession.get(player.getPlayerName()),
                    this.bigBlind, this.smallBlind, playerStatus
            );
            JSONObject chatHoldemAns = new ChatHoldem().chatHoldemAction(prompt.getCombinedPrompt());

            if (this.goldFinger) {
                String reason = (String) chatHoldemAns.get("reason");
                System.out.println("[GoldFinger]: [思考考]: " + reason);
            }
            String speak = (String) chatHoldemAns.get("speak");
            if (speak != null) System.out.println("[speak]: " + speak);
            else System.out.println("[speak]: " + "我就看着你们表演吧！");

            playerAction = chatHoldemAns.getString("action");
            playerAmount = chatHoldemAns.getIntValue("amount");
        } else {
            playerAction = "call";  // 强制跟牌
            // 交给playerActionDealer修正playerAmount
        }
        this.playerActionDealer(player.getPlayerName(), nowRound, playerAction, playerAmount);
    }

    public void play() {
        int session = 0;
        boolean continueGame = true;

        while (continueGame) {

            int button = session++;
            int smallBlindIndex = (button + 1) % (this.playerNum+1);
            int bigBlindIndex = (button + 2) % (this.playerNum+1);
            this.stackUpdatedByPosition(smallBlindIndex, bigBlindIndex);
            int round = 0;

            for (; round < Constants.TOTAL_ROUND; round++) {
                if (this.dealer.getPlayerList().size() <= 1)  // 只剩一个玩家提前进行结算
                    break;

                System.out.println("=======【 Round " + (round + 1) + " 】=======");
                for (int i = 0; i < this.dealer.getPlayerList().size(); i++) {
                    Player player = this.dealer.getPlayerList().get(i);
                    printRoundPlayers(player.getPlayerName());
                    int playerIndex = this.getPlayerIndexOnSession(player);

                    if (player.getPlayerName().equals("Me"))
                        myTurn(round, playerIndex, player, smallBlindIndex, bigBlindIndex, false);
                    else
                        playerTurn(round, playerIndex, player, smallBlindIndex, bigBlindIndex, false);
                }
                this.cleanFoldPlayers();  // Dealer清除round内所有弃牌玩家

                // round结束前，剩余玩家筹码拉齐
                Player alignPlayer = commonUtils.getMaxAmountFirstPlayer(
                        this.dealer.getPlayerList(), this.playerStacks,
                        this.playerStacksUpdatedOnSession,
                        this.playerMoveHistoryListUpdatedOnSession
                );
                if (this.dealer.getPlayerList().get(0).getPlayerName().equals(alignPlayer.getPlayerName()))
                    continue;  // 所有人都拉齐了，不需要再拉齐

                System.out.println("=======【 Round " + (round + 1) + "筹码拉齐 】=======");
                for (int i = 0; i < this.dealer.getPlayerList().size(); i++) {
                    Player player = this.dealer.getPlayerList().get(i);
                    printRoundPlayers(player.getPlayerName());
                    int playerIndex = this.getPlayerIndexOnSession(player);

                    if (player.getPlayerName().equals(alignPlayer.getPlayerName())) break;

                    if (player.getPlayerName().equals("Me"))
                        myTurn(round, playerIndex, player, smallBlindIndex, bigBlindIndex, true);
                    else
                        playerTurn(round, playerIndex, player, smallBlindIndex, bigBlindIndex, true);
                }
                this.cleanFoldPlayers();  // Dealer清除筹码拉齐阶段内所有弃牌玩家
            }

            // 只剩一个玩家提前进行结算 or 多个玩家进入最终round进入结算
            if (round == Constants.TOTAL_ROUND) round -= 1;
            this.layYourCardsOnTheTable(round, session);

            if (this.playerStacks.get("Me") == 0) {
                System.out.println("=======【 Game Over 】=======");
                return;
            } else {
                System.out.println("还要继续吗？[y/n]");
                continueGame = new Scanner(System.in).nextLine().equals("y");
            }
        }
    }
}
