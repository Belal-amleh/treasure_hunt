package treasurehunt.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import treasurehunt.model.EmptyTile;
import treasurehunt.model.EnemyTile;
import treasurehunt.model.Grid;
import treasurehunt.model.Tile;
import treasurehunt.model.TrapTile;
import treasurehunt.model.TreasureTile;

/**
 * Builds a seeded world. It reserves a hazard-free L-shaped route from
 * the bottom-left start to the treasure before placing hazards elsewhere.
 */
public final class GridGenerator {
    public Grid generate(GameConfig config, long seed) {
        config.validate();
        Random random = new Random(seed);
        int rows = config.getRows();
        int columns = config.getColumns();
        int startRow = rows - 1;
        int startColumn = 0;

        Tile[][] tiles = new Tile[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                tiles[r][c] = new EmptyTile();
            }
        }

        List<Cell> eligibleTreasureCells = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (!(r == startRow && c == startColumn)
                        && manhattan(startRow, startColumn, r, c) <= config.getInitialMoves()) {
                    eligibleTreasureCells.add(new Cell(r, c));
                }
            }
        }
        if (eligibleTreasureCells.isEmpty()) {
            throw new IllegalArgumentException("No treasure location is reachable within the move budget.");
        }

        Cell treasure = eligibleTreasureCells.get(random.nextInt(eligibleTreasureCells.size()));
        List<Cell> safePath = createSafePath(startRow, startColumn, treasure.row, treasure.column);
        boolean[][] reserved = new boolean[rows][columns];
        for (Cell cell : safePath) {
            reserved[cell.row][cell.column] = true;
        }
        tiles[treasure.row][treasure.column] = new TreasureTile();

        List<Cell> available = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (!reserved[r][c] && !(r == startRow && c == startColumn)) {
                    available.add(new Cell(r, c));
                }
            }
        }
        Collections.shuffle(available, random);

        int needed = config.getTrapCount() + config.getEnemyCount();
        if (needed > available.size()) {
            throw new IllegalArgumentException(
                    "Hazard counts are too large after reserving the safe path. Reduce trap/enemy counts.");
        }

        int index = 0;
        for (int i = 0; i < config.getTrapCount(); i++) {
            Cell cell = available.get(index++);
            tiles[cell.row][cell.column] = new TrapTile(config.getTrapPenalty());
        }
        for (int i = 0; i < config.getEnemyCount(); i++) {
            Cell cell = available.get(index++);
            tiles[cell.row][cell.column] = new EnemyTile();
        }

        Grid grid = new Grid(rows, columns, tiles);
        if (!isSafePathValid(grid, safePath)) {
            throw new IllegalStateException("Generated safe path is invalid.");
        }
        return grid;
    }

    private List<Cell> createSafePath(int startRow, int startColumn, int targetRow, int targetColumn) {
        List<Cell> path = new ArrayList<>();
        int r = startRow;
        int c = startColumn;
        path.add(new Cell(r, c));

        while (c != targetColumn) {
            c += Integer.compare(targetColumn, c);
            path.add(new Cell(r, c));
        }
        while (r != targetRow) {
            r += Integer.compare(targetRow, r);
            path.add(new Cell(r, c));
        }
        return path;
    }

    private boolean isSafePathValid(Grid grid, List<Cell> path) {
        for (Cell cell : path) {
            char symbol = grid.getTile(cell.row, cell.column).getSymbol();
            if (symbol == 'X' || symbol == 'E') {
                return false;
            }
        }
        return path.size() - 1 <= 20;
    }

    private int manhattan(int r1, int c1, int r2, int c2) {
        return Math.abs(r1 - r2) + Math.abs(c1 - c2);
    }

    private static final class Cell {
        private final int row;
        private final int column;

        private Cell(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }
}
