package oh_heaven.game.player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jgamegrid.Location;
import oh_heaven.game.Oh_Heaven;
import oh_heaven.game.enumation.Suit;
import oh_heaven.game.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Smart player
 */
public class SmartPlayer extends Player {
    public SmartPlayer(Hand hand, Location handLocation, Location scoreLocation) {
        super(hand, handLocation, scoreLocation);
    }

    @Override
    public Card playCard(Oh_Heaven cardGame, String message, int thinkingTime, List<Card> turnPlayedCards, Suit trumps) {
        cardGame.setStatusText(message);
        cardGame.delay(thinkingTime);

        if (trick == bid) {
            // if the point is same as expected points, try to get zero point
            // (if get expected points finally, will get extra 10 points)
            return noWinCard(turnPlayedCards, trumps);
        }

        Card playCard = null;
        if (turnPlayedCards.size() == 0) {
            // play the leader card, the biggest card with same color of trump
            playCard = maxSuitCard(hand.getCardList(), trumps);
            if (playCard == null) {
                // has no card with same color of trump，play the biggest card
                for (Card card : hand.getCardList()) {
                    if (playCard == null || playCard.getRankId() > card.getRankId()) {
                        playCard = card;
                    }
                }
            }
        } else {
            // normally play
            Card lead = turnPlayedCards.get(0);
            // if somebody else has play the biggest card with same color as trump
            Card maxSameTrumpSuitCard = maxSuitCard(turnPlayedCards, trumps);
            // try to play the card with the same color, but bigger
            for (Card card : hand.getCardList()) {
                if (card.getSuit().equals(trumps)) {
                    if (maxSameTrumpSuitCard == null || maxSameTrumpSuitCard.getRankId() > card.getRankId()) {
                        if (playCard == null || playCard.getRankId() > card.getRankId()) {
                            playCard = card;
                        }
                    }
                }
            }
            if (playCard != null) {
                return playCard;
            }

            if (maxSameTrumpSuitCard == null) {
                // all of the card on table is different of trump
                // but others play the biggest card with the same color as leader
                Card maxSameLeadSuitCard = maxSuitCard(turnPlayedCards, (Suit) lead.getSuit());
                // try to play the card with the same color, but bigger
                for (Card card : hand.getCardList()) {
                    if (card.getSuit().equals(lead.getSuit())) {
                        if (maxSameLeadSuitCard == null || maxSameLeadSuitCard.getRankId() > card.getRankId()) {
                            if (playCard == null || playCard.getRankId() > card.getRankId()) {
                                playCard = card;
                            }
                        }
                    }
                }
            }
        }

        if (playCard != null) {
            return playCard;
        }

        // none situation above happens, randomly play one
        return RandomUtil.randomCard(hand);

    }

    /**
     * has get expected points, play the card will not win
     *
     * @param turnPlayedCards
     * @param trumps
     * @return
     */
    private Card noWinCard(List<Card> turnPlayedCards, Suit trumps) {

        Card maxSameTrumpSuitCard = maxSuitCard(turnPlayedCards, trumps);
        if (maxSameTrumpSuitCard != null) {
            // somebody has played a card with the color same as trump, play a smaller card
            Card lessCard = null;
            for (Card card : hand.getCardList()) {
                if (card.getSuit().equals(trumps) && card.getRankId() > maxSameTrumpSuitCard.getRankId()) {
                    // same color but less than maxSameTrumpSuitCard
                    if (lessCard == null || lessCard.getRankId() > card.getRankId()) {
                        // play the biggest card in the set<-(cards less than maxSameTrumpSuitCard)
                        lessCard = card;
                    }
                }
            }
            if (lessCard != null) {
                return lessCard;
            }
        }

        Card lead = null;
        if (turnPlayedCards.size() > 0) {
            lead = turnPlayedCards.get(0);
        }

        Card playCard = null;
        if (lead == null) {
            // play leader card, play one card with different color of trump
            for (Card card : hand.getCardList()) {
                if (card.getSuit().equals(trumps)) {
                    continue;
                }
                if (playCard == null || playCard.getRankId() < card.getRankId()) {
                    playCard = card;
                }
            }

        } else {
            // play the biggest card in hand with different color of trump
            // or same color of leader but less then leader
            for (Card card : hand.getCardList()) {
                if (card.getSuit().equals(trumps)) {
                    // same color as trump
                    continue;
                }

                if (card.getSuit().equals(lead.getSuit()) && card.getRankId() > lead.getRankId()) {
                    // same color as leader, but less than leader
                    if (playCard == null || playCard.getRankId() > card.getRankId()) {
                        // as big as it can
                        playCard = card;
                    }
                } else if (!card.getSuit().equals(lead.getSuit())) {
                    // biggest card with different color
                    if (playCard == null || playCard.getRankId() > card.getRankId()) {
                        playCard = card;
                    }
                }
            }

        }

        if (playCard != null) {
            return playCard;
        }
        // none situation above, randomly play
        return RandomUtil.randomCard(hand);
    }

    /**
     * biggest card with the same color as suit
     *
     * @param suit
     * @return
     */
    private Card maxSuitCard(List<Card> cards, Suit suit) {
        Card card = null;
        for (Card cardTmp : cards) {
            if (cardTmp.getSuit().equals(suit)) {
                if (card == null || card.getRankId() > cardTmp.getRankId()) {
                    card = cardTmp;
                }

            }
        }
        return card;
    }


}
