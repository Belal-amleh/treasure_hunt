package treasurehunt.model;

/** Base type for every tile. Subclasses provide their own entry behavior. */
public abstract class Tile {
    public abstract TileResult onEnter(Player player);

    public abstract char getSymbol();

    /** All current tile types can be entered. */
    public boolean isWalkable() {
        return true;
    }
}
