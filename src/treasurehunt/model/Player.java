package treasurehunt.model;

/** Stores player resources and position. Coordinates are zero-based. */
public final class Player {
    private int row;
    private int column;
    private int movesLeft;
    private int lives;

    public Player(int row, int column, int moves, int lives) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Player coordinates cannot be negative.");
        }
        if (moves < 0 || lives < 0) {
            throw new IllegalArgumentException("Moves and lives cannot be negative.");
        }
        this.row = row;
        this.column = column;
        this.movesLeft = moves;
        this.lives = lives;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getMovesLeft() {
        return movesLeft;
    }

    public int getLives() {
        return lives;
    }

    /** Consumes one move for a successful step. */
    public void consumeMove() {
        consumeMoves(1);
    }

    /** Consumes additional moves, clamping the result at zero. */
    public void consumeMoves(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Move cost cannot be negative.");
        }
        movesLeft = Math.max(0, movesLeft - amount);
    }

    public void loseLife() {
        lives = Math.max(0, lives - 1);
    }

    /*
     * Package-private so only classes in the model package can change position.
     * Game uses the public move operation and does not set coordinates directly.
     */
    void setPosition(int row, int column) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Player coordinates cannot be negative.");
        }
        this.row = row;
        this.column = column;
    }

    /**
     * Controlled movement operation used by Game. The caller must validate
     * the destination before calling this method.
     */
    public void moveTo(int row, int column) {
        setPosition(row, column);
    }
}
