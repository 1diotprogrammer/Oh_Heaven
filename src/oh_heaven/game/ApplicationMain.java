package oh_heaven.game;

import oh_heaven.game.util.PropertyUtil;


/**
 * starter
 */
public class ApplicationMain {

    public static void main(String[] args) {
        // System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String file = null;
        if (args != null && args.length != 0) {
            file = args[0];
        }
        PropertyUtil.loadPropertiesFile(file);

        Oh_Heaven game = new Oh_Heaven();
        game.playGame();
        game.showGameResult();

    }
}
