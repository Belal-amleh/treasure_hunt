package treasurehunt.model;

public final class EnemyTile extends Tile {
    @Override
    public TileResult onEnter(Player player) {
        player.loseLife();
        return TileResult.ENEMY_ENCOUNTERED;
    }

    @Override
    public char getSymbol() {
        return 'E';
    }
}
