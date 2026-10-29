package treasurehunt.model;

public final class TreasureTile extends Tile {
    @Override
    public TileResult onEnter(Player player) {
        return TileResult.TREASURE_FOUND;
    }

    @Override
    public char getSymbol() {
        return 'G';
    }
}
