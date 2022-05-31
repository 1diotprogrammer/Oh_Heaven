package oh_heaven.game.player;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.enumation.Suit;
import oh_heaven.game.util.RandomUtil;

import java.awt.*;
import java.util.List;

/**
 * draw a player, can be random, also can be selected as normal player or legal player or smart player
 */
public abstract class Player {
    public static final Font bigFont = new Font("Serif", Font.BOLD, 36);

    protected int score = 0;
    protected int trick = 0;
    protected int bid = 0;

    protected Hand hand;
    protected Location handLocation;

    protected Actor actor;
    protected Location scoreLocation;

    public Player(Hand hand, Location handLocation, Location scoreLocation) {
        this.hand = hand;
        this.handLocation = handLocation;
        this.scoreLocation = scoreLocation;
    }

    /**
     * clear preview round datas
     */
    public void reInit() {
        trick = 0;
        bid = 0;
        hand.removeAll(true);
    }

    /**
     * player one card
     *
     * @return
     */
    public Card playCard(Oh_Heaven cardGame, String message, int thinkingTime, List<Card> turnPlayedCards, Suit trumps) {
        cardGame.setStatusText(message);
        cardGame.delay(thinkingTime);
        return RandomUtil.randomCard(hand);
    }

    /**
     * Show the player score
     *
     * @param cardGame
     */
    public void showScore(CardGame cardGame) {
        if (actor != null) {
            cardGame.removeActor(actor);
        }
        String text = "[" + score + "]" + trick + "/" + bid;
        actor = new TextActor(text, Color.WHITE, cardGame.bgColor, bigFont);
        cardGame.addActor(actor, scoreLocation);
    }

    /**
     * Show cards
     *
     * @param cardGame
     * @param layout
     * @param targetArea
     */
    public void showCards(CardGame cardGame, RowLayout layout, TargetArea targetArea) {
        hand.setView(cardGame, layout);
        hand.setTargetArea(targetArea);
        hand.draw();
    }

    /**
     * insert one card into the player hand
     *
     * @param card
     */
    public void addCard(Card card) {
        card.removeFromHand(false);
        hand.insert(card, false);
    }

    /**
     * Sort the cards in hand
     */
    public void sortCard() {
        hand.sort(Hand.SortType.SUITPRIORITY, true);
    }

    public void incrTrick() {
        this.trick++;
    }

    public void incrScore(int incr) {
        this.score += incr;
    }

    public Hand getHand() {
        return hand;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getScore() {
        return score;
    }

    public int getTrick() {
        return trick;
    }

    public Location getHandLocation() {
        return handLocation;
    }
}
