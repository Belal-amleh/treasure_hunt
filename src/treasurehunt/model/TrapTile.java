package treasurehunt.model;

public final class TrapTile extends Tile {
    private final int movePenalty;

    public TrapTile(int movePenalty) {
        if (movePenalty < 0) {
            throw new IllegalArgumentException("Trap penalty cannot be negative.");
        }
        this.movePenalty = movePenalty;
    }

    public int getMovePenalty() {
        return movePenalty;
    }

    @Override
    public TileResult onEnter(Player player) {
        player.consumeMoves(movePenalty);
        return TileResult.TRAP_TRIGGERED;
    }

    @Override
    public char getSymbol() {
        return 'X';
    }
}
