package oh_heaven.game.player;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Location;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.enumation.Suit;

import java.util.List;

/**
 * draw a player, can be random, also can be selected as normal player or legal player or smart player
 */
public class HumanPlayer extends Player{

    public HumanPlayer(Hand hand, Location handLocation, Location scoreLocation) {
        super(hand, handLocation, scoreLocation);
    }

    /**
     * player one card
     *
     * @return
     */
    public Card playCard(Oh_Heaven cardGame, String message, int thinkingTime, List<Card> turnPlayedCards, Suit trumps) {
        getHand().setTouchEnabled(true);
        cardGame.setStatusText(message);
        while (null == cardGame.selected) {
            cardGame.delay(100);
        }

        return cardGame.selected;
    }

}
