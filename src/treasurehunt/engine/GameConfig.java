package treasurehunt.engine;

/** Immutable settings for one Treasure Hunt game. */
public final class GameConfig {
    private final int rows;
    private final int columns;
    private final int initialMoves;
    private final int initialLives;
    private final int trapPenalty;
    private final int trapCount;
    private final int enemyCount;

    public GameConfig(int rows, int columns, int initialMoves, int initialLives,
                      int trapPenalty, int trapCount, int enemyCount) {
        this.rows = rows;
        this.columns = columns;
        this.initialMoves = initialMoves;
        this.initialLives = initialLives;
        this.trapPenalty = trapPenalty;
        this.trapCount = trapCount;
        this.enemyCount = enemyCount;
        validate();
    }

    public static GameConfig defaults() {
        return new GameConfig(10, 10, 20, 3, 3, 5, 5);
    }

    public int getRows() { return rows; }
    public int getColumns() { return columns; }
    public int getInitialMoves() { return initialMoves; }
    public int getInitialLives() { return initialLives; }
    public int getTrapPenalty() { return trapPenalty; }
    public int getTrapCount() { return trapCount; }
    public int getEnemyCount() { return enemyCount; }

    public void validate() {
        if (rows < 2 || columns < 2) {
            throw new IllegalArgumentException("Grid must be at least 2 x 2.");
        }
        if (initialMoves <= 0 || initialLives <= 0) {
            throw new IllegalArgumentException("Initial moves and lives must be positive.");
        }
        if (trapPenalty < 0 || trapCount < 0 || enemyCount < 0) {
            throw new IllegalArgumentException("Trap penalty and hazard counts cannot be negative.");
        }
        int totalCells = rows * columns;
        // One start tile and one treasure tile are reserved. Hazards occupy distinct cells.
        if (trapCount + enemyCount > totalCells - 2) {
            throw new IllegalArgumentException("Too many hazards for the grid size.");
        }
        if (initialMoves < rows - 1 + columns - 1) {
            // Not always invalid, but the generator cannot promise all possible treasures
            // are reachable. It can still choose a nearby treasure.
            if (initialMoves < 1) {
                throw new IllegalArgumentException("Initial move budget is too small.");
            }
        }
    }
}
