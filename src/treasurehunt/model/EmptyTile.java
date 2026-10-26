package treasurehunt.model;

public final class EmptyTile extends Tile {
    @Override
    public TileResult onEnter(Player player) {
        return TileResult.NORMAL;
    }

    @Override
    public char getSymbol() {
        return '.';
    }
}
