package treasurehunt;

import java.awt.GraphicsEnvironment;

import treasurehunt.engine.Game;
import treasurehunt.engine.GameConfig;
import treasurehunt.ui.ConsoleUI;
import treasurehunt.ui.SwingUI;

public final class Main {
    private Main() {}

    public static void main(String[] args) {
        boolean console = GraphicsEnvironment.isHeadless();
        long seed = System.currentTimeMillis();

        for (int i = 0; i < args.length; i++) {
            if ("--console".equalsIgnoreCase(args[i])) {
                console = true;
            } else if ("--seed".equalsIgnoreCase(args[i]) && i + 1 < args.length) {
                try {
                    seed = Long.parseLong(args[++i]);
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid seed. Please provide a whole number.");
                    return;
                }
            } else {
                System.out.println("Unknown argument: " + args[i]);
                System.out.println("Usage: java treasurehunt.Main [--console] [--seed NUMBER]");
                return;
            }
        }

        Game game = new Game(GameConfig.defaults(), seed);
        if (console) {
            new ConsoleUI(game).start();
        } else {
            new SwingUI(game).start();
        }
    }
}
