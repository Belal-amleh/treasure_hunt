package treasurehunt.engine;

import treasurehunt.model.Direction;
import treasurehunt.model.Grid;
import treasurehunt.model.Player;
import treasurehunt.model.Tile;
import treasurehunt.model.TileResult;

/** Owns game flow and is the single source of truth for game rules. */
public final class Game {
    private final GameConfig config;
    private final GridGenerator generator;
    private final long initialSeed;
    private long generationIndex;
    private Player player;
    private Grid grid;
    private GameState state;

    public Game(GameConfig config, long seed) {
        this.config = config;
        this.config.validate();
        this.generator = new GridGenerator();
        this.initialSeed = seed;
        this.generationIndex = 0;
        startNewGame();
    }

    public void startNewGame() {
        long seed = initialSeed + generationIndex;
        generationIndex++;
        this.grid = generator.generate(config, seed);
        this.player = new Player(grid.getRows() - 1, 0,
                config.getInitialMoves(), config.getInitialLives());
        this.state = GameState.PLAYING;
    }

    public MoveResult movePlayer(Direction direction) {
        if (state != GameState.PLAYING || direction == null) {
            return MoveResult.INVALID_MOVE;
        }

        int nextRow = player.getRow() + direction.rowDelta();
        int nextColumn = player.getColumn() + direction.columnDelta();
        if (!grid.isInside(nextRow, nextColumn)) {
            return MoveResult.INVALID_MOVE;
        }

        updatePosition(direction);
        Tile destination = grid.getTile(nextRow, nextColumn);
        TileResult tileResult = destination.onEnter(player);

        // Winning takes precedence over a resource reaching zero on this move.
        if (checkWinCondition(tileResult)) {
            finishGame(GameState.WON);
            return MoveResult.WON;
        }

        if (checkLoseCondition()) {
            finishGame(GameState.LOST);
            return MoveResult.LOST;
        }

        switch (tileResult) {
            case TRAP_TRIGGERED:
                return MoveResult.TRAP_TRIGGERED;
            case ENEMY_ENCOUNTERED:
                return MoveResult.ENEMY_ENCOUNTERED;
            default:
                return MoveResult.MOVED;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Grid getGrid() {
        return grid;
    }

    public GameState getGameState() {
        return state;
    }

    public long getInitialSeed() {
        return initialSeed;
    }

    private void updatePosition(Direction direction) {
        int nextRow = player.getRow() + direction.rowDelta();
        int nextColumn = player.getColumn() + direction.columnDelta();
        player.moveTo(nextRow, nextColumn);
        player.consumeMove();
    }

    private boolean checkWinCondition(TileResult result) {
        return result == TileResult.TREASURE_FOUND;
    }

    private boolean checkLoseCondition() {
        return player.getMovesLeft() <= 0 || player.getLives() <= 0;
    }

    private void finishGame(GameState result) {
        if (result == GameState.WON || result == GameState.LOST) {
            state = result;
        }
    }
}
