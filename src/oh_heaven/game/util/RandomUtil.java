package oh_heaven.game.util;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.Random;

/**
 * Tools, provide random util
 */
public class RandomUtil {

    private static int seed;
    private static Random random;
    static {
        seed = PropertyUtil.getIntParameter("seed");
        random = new Random(seed);;
    }

    public static void init(int seed) {
        RandomUtil.seed = seed;
        RandomUtil.random =  new Random(seed);;
    }

    public static Random getRandom() {
        return random;
    }

    // return random Enum value
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // return random Card from Hand
    public static Card randomCard(Hand hand) {
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

}
