package oh_heaven.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import oh_heaven.game.enumation.Rank;
import oh_heaven.game.enumation.Suit;
import oh_heaven.game.player.*;
import oh_heaven.game.util.PropertyUtil;
import oh_heaven.game.util.RandomUtil;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class Oh_Heaven extends CardGame {
    public static final int madeBidBonus = 10;
    private static final int handWidth = 400;
    private static final int trickWidth = 40;
    private static final String[] trumpImage = {"bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif"};


    private final String version = "1.0";
    public final int nbPlayers = 4;
    public int nbStartCards = 13;
    public int nbRounds = 3;

    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private final int thinkingTime = 2000;
    private Location hideLocation = new Location(-500, -500);
    private Location trumpsActorLocation = new Location(50, 50);
    private boolean enforceRules = false;

    private Player[] players = new Player[nbPlayers];

    /**
     * constructor
     */
    public Oh_Heaven() {
        super(700, 700, 30);
        this.nbStartCards = PropertyUtil.getIntParameter("nbStartCards");
        this.nbRounds = PropertyUtil.getIntParameter("rounds");

        setTitle("Oh_Heaven (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");

        initPlayers();
    }

    /**
     * Play game
     */
    public void playGame() {
        for (int i = 0; i < nbRounds; i++) {
            initRound();
            playRound();
            updateScores();
        }
    }

    /**
     * Game result
     */
    public void showGameResult() {
        int maxScore = 0;
        for (int i = 0; i < nbPlayers; i++) {
            players[i].showScore(this);
            Player player = players[i];
            maxScore = Math.max(maxScore, player.getScore());
        }

        Set<Integer> winners = new HashSet<>();
        for (int i = 0; i < nbPlayers; i++) {
            Player player = players[i];
            if (player.getScore() == maxScore) {
                winners.add(i);
            }
        }

        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " + winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toSet()));
        }

        addActor(new Actor("sprites/gameover.gif"), textLocation);

        setStatusText(winText);

        refresh();
    }


    private void dealingOut(Player[] players, int nbPlayers, int nbCardsPerPlayer) {
        Hand pack = deck.toHand(false);
        // pack.setView(Oh_Heaven.this, new RowLayout(hideLocation, 0));
        for (int i = 0; i < nbCardsPerPlayer; i++) {
            for (int j = 0; j < nbPlayers; j++) {
                if (pack.isEmpty()) return;
                Card dealt = RandomUtil.randomCard(pack);
                players[j].addCard(dealt);
            }
        }
    }

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId();
        // Warning: Reverse rank order of cards (see comment on enum)
    }

    private void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            Player player = players[i];
            player.incrScore(player.getTrick());
            if (player.getTrick() == player.getBid()) {
                player.incrScore(madeBidBonus);
            }
        }
    }

    private void initBids(Suit trumps, int nextPlayer) {
        int total = 0;
        for (int i = nextPlayer; i < nextPlayer + nbPlayers; i++) {
            int iP = i % nbPlayers;
            Player player = players[iP];
            player.setBid(nbStartCards / 4 + RandomUtil.getRandom().nextInt(2));
            total += player.getBid();
        }
        // Force last bid so not every bid possible
        if (total == nbStartCards) {
            int iP = (nextPlayer + nbPlayers) % nbPlayers;
            Player player = players[iP];
            int bid = player.getBid();
            if (bid == 0) {
                player.setBid(1);
            } else {
                player.setBid(bid + (RandomUtil.getRandom().nextBoolean() ? -1 : 1));
            }
        }
    }

    public Card selected;

    private void initRound() {
        // clear hands of players, trick and bid
        for (int i = 0; i < nbPlayers; i++) {
            players[i].reInit();
        }

        // deal cards
        dealingOut(players, nbPlayers, nbStartCards);
        for (int i = 0; i < nbPlayers; i++) {
            // order of cards in hand
            players[i].sortCard();
        }

        // listen the clicking
        Player humanPlayer = getHumanPlayer();
        if (humanPlayer != null) {
            // Set up human player for interaction
            CardListener cardListener = new CardAdapter() {
                // Human Player plays card
                public void leftDoubleClicked(Card card) {
                    selected = card;
                    humanPlayer.getHand().setTouchEnabled(false);
                }
            };
            humanPlayer.getHand().addCardListener(cardListener);
        }

        // graphics, show card in hand
        for (int i = 0; i < nbPlayers; i++) {
            Player player = players[i];
            RowLayout layout = new RowLayout(player.getHandLocation(), handWidth);
            layout.setRotationAngle(90 * i);
            player.showCards(this, layout, new TargetArea(trickLocation));
        }

    }

    private void playRound() {
        // Select and display trump suit
        final Suit trumps = RandomUtil.randomEnum(Suit.class);
        final Actor trumpsActor = new Actor("sprites/" + trumpImage[trumps.ordinal()]);
        addActor(trumpsActor, trumpsActorLocation);

        // End trump suit
        Hand trick;
        int winner;
        Card winningCard;
        Suit lead;
        List<Card> roundPlayedCards = new ArrayList<>();
        List<Card> turnPlayedCards = new ArrayList<>();

        // randomly select player to lead for this round
        int nextPlayer = RandomUtil.getRandom().nextInt(nbPlayers);

        // init players bid score
        initBids(trumps, nextPlayer);

        // show score
        for (int i = 0; i < nbPlayers; i++) {
            players[i].showScore(this);
        }

        for (int i = 0; i < nbStartCards; i++) {
            trick = new Hand(deck);
            selected = null;
            turnPlayedCards.clear();

            // select lead card
            selectPlayCard(nextPlayer, 0, roundPlayedCards, turnPlayedCards, trumps);

            // Lead with selected card
            trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
            trick.draw();
            selected.setVerso(false);
            // No restrictions on the card being lead
            lead = (Suit) selected.getSuit();
            selected.transfer(trick, true); // transfer to trick (includes graphic effect)
            winner = nextPlayer;
            winningCard = selected;
            // End Lead

            for (int j = 1; j < nbPlayers; j++) {
                if (++nextPlayer >= nbPlayers) nextPlayer = 0;  // From last back to first
                selected = null;

                // select play card
                selectPlayCard(nextPlayer, 1, roundPlayedCards, turnPlayedCards, trumps);

                Player player = players[nextPlayer];
                // Follow with selected card
                trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards() + 2) * trickWidth));
                trick.draw();
                selected.setVerso(false);  // In case it is upside down
                // Check: Following card must follow suit if possible
                if (selected.getSuit() != lead && player.getHand().getNumberOfCardsWithSuit(lead) > 0) {
                    // Rule violation
                    String violation = "Follow rule broken by player " + nextPlayer + " attempting to play " + selected;
                    System.out.println(violation);
                    if (enforceRules)
                        try {
                            throw (new BrokeRuleException(violation));
                        } catch (BrokeRuleException e) {
                            e.printStackTrace();
                            System.out.println("A cheating player spoiled the game!");
                            System.exit(0);
                        }
                }
                // End Check
                selected.transfer(trick, true); // transfer to trick (includes graphic effect)
                System.out.println("winning: " + winningCard);
                System.out.println(" played: " + selected);
                // System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + (13 - winningCard.getRankId()));
                // System.out.println(" played: suit = " +    selected.getSuit() + ", rank = " + (13 -    selected.getRankId()));
                if ( // beat current winner with higher card
                        (selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
                                // trumped when non-trump was winning
                                (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
                    System.out.println("NEW WINNER");
                    winner = nextPlayer;
                    winningCard = selected;
                }
                // End Follow
            }

            delay(600);
            trick.setView(this, new RowLayout(hideLocation, 0));
            trick.draw();
            nextPlayer = winner;
            setStatusText("Player " + nextPlayer + " wins trick.");

            Player winnerPlayer = players[winner];
            winnerPlayer.incrTrick();
            winnerPlayer.showScore(this);

        }
        removeActor(trumpsActor);
    }

    /**
     * get the humanplayer
     *
     * @return
     */
    private Player getHumanPlayer() {
        for (int i = 0; i < nbPlayers; i++) {
            if (players[i] instanceof HumanPlayer) {
                return players[i];
            }
        }
        return null;
    }

    /**
     * consider to play a card
     *
     * @param playerIndex
     * @param type             0:lead card, 1:common card
     * @param roundPlayedCards cards have been played current round
     * @param turnPlayedCards  cards have been played last round
     * @param trumps
     */
    private void selectPlayCard(int playerIndex, int type, List<Card> roundPlayedCards, List<Card> turnPlayedCards, Suit trumps) {
        Player player = players[playerIndex];
        if (player instanceof HumanPlayer) {
            String playType = type == 1 ? "play" : "lead";
            String message = "Player " + playerIndex + " double-click on card to " + playType + ".";
            selected = player.playCard(this, message, 0, turnPlayedCards, trumps);
        } else {
            String message = "Player " + playerIndex + " thinking...";
            selected = player.playCard(this, message, thinkingTime, turnPlayedCards, trumps);
        }
        roundPlayedCards.add(selected);
        turnPlayedCards.add(selected);
    }

    /**
     * initialise all players
     */
    private void initPlayers() {
        players[0] = initPlayer(0, new Hand(deck), new Location(350, 625), new Location(575, 675));
        players[1] = initPlayer(1, new Hand(deck), new Location(75, 350), new Location(25, 575));
        players[2] = initPlayer(2, new Hand(deck), new Location(350, 75), new Location(575, 25));
        players[3] = initPlayer(3, new Hand(deck), new Location(625, 350), new Location(575, 575));
        for (Player player : players) {
            player.showScore(this);
        }
    }

    /**
     * initialise all kinds of players by following the properties
     *
     * @param playerIndex
     * @param hand
     * @param handLocation
     * @param scoreLocation
     * @return
     */
    private Player initPlayer(int playerIndex, Hand hand, Location handLocation, Location scoreLocation) {
        String playerType = PropertyUtil.getStringParameter("players." + playerIndex);
        if (playerType.equalsIgnoreCase("human")) {
            return new HumanPlayer(hand, handLocation, scoreLocation);
        } else if (playerType.equalsIgnoreCase("legal")) {
            return new LegalPlayer(hand, handLocation, scoreLocation);
        } else if (playerType.equalsIgnoreCase("smart")) {
            return new SmartPlayer(hand, handLocation, scoreLocation);
        } else {
            return new RandomPlayer(hand, handLocation, scoreLocation);
        }
    }

}
