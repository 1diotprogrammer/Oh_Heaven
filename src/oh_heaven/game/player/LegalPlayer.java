package oh_heaven.game.player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jgamegrid.Location;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.enumation.Suit;
import oh_heaven.game.util.RandomUtil;

import java.util.List;

/**
 * Legal player
 */
public class LegalPlayer extends Player {
    public LegalPlayer(Hand hand, Location handLocation, Location scoreLocation) {
        super(hand, handLocation, scoreLocation);
    }

    @Override
    public Card playCard(Oh_Heaven cardGame, String message, int thinkingTime, List<Card> turnPlayedCards, Suit trumps) {
        cardGame.setStatusText(message);
        cardGame.delay(thinkingTime);

        Card playCard = null;
        if (turnPlayedCards.size() == 0) {
            // play the leader card, if player has the same color as trumps, play the biggest card with that color
            // if not, randomly play one card
            for (Card card : hand.getCardList()) {
                if (card.getSuit().equals(trumps)) {
                    if (playCard == null || playCard.getRankId() > card.getRankId()) {
                        playCard = card;
                    }
                }
            }
        } else {
            // normally play
            Card lead = turnPlayedCards.get(0);

            // if player has the same color as trumps, play the biggest card with that color
            for (Card card : hand.getCardList()) {
                if (card.getSuit().equals(trumps)) {
                    if (playCard == null || playCard.getRankId() > card.getRankId()) {
                        playCard = card;
                    }
                }
            }

            if (playCard == null) {
                // if has no card with same color as trumps, play the card equal or bigger than the lead card
                for (Card card : hand.getCardList()) {
                    if (card.getSuit().equals(lead.getSuit())) {
                        if (playCard == null || playCard.getRankId() > card.getRankId()) {
                            playCard = card;
                        }
                    }
                }
            }
        }

        if (playCard == null) {
            // randomly play one
            playCard = RandomUtil.randomCard(hand);
        }
        return playCard;
    }
}
