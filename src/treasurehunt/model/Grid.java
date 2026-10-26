package treasurehunt.model;

/**
 * A grid containing a complete tile layout. The supplied array is copied
 * so callers cannot mutate the grid by retaining the original array.
 */
public final class Grid {
    private final int rows;
    private final int columns;
    private final Tile[][] tiles;

    public Grid(int rows, int columns, Tile[][] initialTiles) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive.");
        }
        if (initialTiles == null || initialTiles.length != rows) {
            throw new IllegalArgumentException("Tile array does not match grid rows.");
        }

        this.rows = rows;
        this.columns = columns;
        this.tiles = new Tile[rows][columns];

        for (int r = 0; r < rows; r++) {
            if (initialTiles[r] == null || initialTiles[r].length != columns) {
                throw new IllegalArgumentException("Tile array does not match grid columns.");
            }
            for (int c = 0; c < columns; c++) {
                if (initialTiles[r][c] == null) {
                    throw new IllegalArgumentException("Every grid cell must contain a tile.");
                }
                this.tiles[r][c] = initialTiles[r][c];
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Tile getTile(int row, int column) {
        if (!isInside(row, column)) {
            throw new IndexOutOfBoundsException("Coordinates outside the grid: (" + row + ", " + column + ")");
        }
        return tiles[row][column];
    }

    public boolean isInside(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }
}
