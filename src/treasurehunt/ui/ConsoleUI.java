package treasurehunt.ui;

import java.util.Scanner;

import treasurehunt.engine.Game;
import treasurehunt.engine.GameState;
import treasurehunt.engine.MoveResult;
import treasurehunt.model.Direction;
import treasurehunt.model.Grid;
import treasurehunt.model.Player;
import treasurehunt.model.Tile;

public final class ConsoleUI implements GameUI {
    private final Game game;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(Game game) {
        this.game = game;
    }

    @Override
    public void start() {
        System.out.println("=== Treasure Hunt ===");
        render();
        while (game.getGameState() == GameState.PLAYING) {
            System.out.print("Move (W/A/S/D), or Q to quit: ");
            String command = scanner.nextLine().trim().toUpperCase();
            if (command.equals("Q")) {
                showMessage("You exited the game.");
                return;
            }

            Direction direction = parseDirection(command);
            if (direction == null) {
                showMessage("Invalid command. Use W, A, S, or D.");
                continue;
            }

            MoveResult result = game.movePlayer(direction);
            showMessage(messageFor(result));
            render();
        }
        askReplayOrExit();
    }

    private Direction parseDirection(String command) {
        switch (command) {
            case "W": return Direction.NORTH;
            case "S": return Direction.SOUTH;
            case "D": return Direction.EAST;
            case "A": return Direction.WEST;
            default: return null;
        }
    }

    private String messageFor(MoveResult result) {
        switch (result) {
            case INVALID_MOVE: return "Invalid move: the destination is outside the grid or the game has ended.";
            case TRAP_TRIGGERED: return "Trap! You lost 3 additional moves.";
            case ENEMY_ENCOUNTERED: return "Enemy! You lost one life.";
            case WON: return "You found the treasure. You win!";
            case LOST: return "You ran out of moves or lives. You lose.";
            case TREASURE_FOUND: return "Treasure found!";
            default: return "You moved.";
        }
    }

    @Override
    public void render() {
        Grid grid = game.getGrid();
        Player player = game.getPlayer();

        System.out.println();
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getColumns(); c++) {
                if (player.getRow() == r && player.getColumn() == c) {
                    System.out.print("P ");
                } else {
                    Tile tile = grid.getTile(r, c);
                    System.out.print(tile.getSymbol() + " ");
                }
            }
            System.out.println();
        }
        System.out.printf("Moves: %d | Lives: %d | State: %s%n",
                player.getMovesLeft(), player.getLives(), game.getGameState());
        System.out.println();
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void askReplayOrExit() {
        while (true) {
            System.out.print("New game (N) or exit (Q)? ");
            String choice = scanner.nextLine().trim().toUpperCase();
            if (choice.equals("N")) {
                game.startNewGame();
                start();
                return;
            }
            if (choice.equals("Q")) {
                System.out.println("Goodbye!");
                return;
            }
            System.out.println("Please enter N or Q.");
        }
    }
}
