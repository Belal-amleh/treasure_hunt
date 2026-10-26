package treasurehunt.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import treasurehunt.engine.Game;
import treasurehunt.engine.GameState;
import treasurehunt.engine.MoveResult;
import treasurehunt.model.Direction;
import treasurehunt.model.Grid;
import treasurehunt.model.Player;

public final class SwingUI implements GameUI {
    private final Game game;
    private JFrame frame;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JLabel messageLabel;
    private final Map<Direction, JButton> directionButtons = new EnumMap<>(Direction.class);

    public SwingUI(Game game) {
        this.game = game;
    }

    @Override
    public void start() {
        SwingUtilities.invokeLater(this::createWindow);
    }

    private void createWindow() {
        frame = new JFrame("Treasure Hunt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));

        statusLabel = new JLabel();
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        frame.add(statusLabel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(game.getGrid().getRows(), game.getGrid().getColumns(), 2, 2));
        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(4, 4));
        JPanel controls = new JPanel(new GridLayout(2, 3, 4, 4));
        controls.add(new JLabel());
        addDirectionButton(controls, Direction.NORTH, "↑ North");
        controls.add(new JLabel());
        addDirectionButton(controls, Direction.WEST, "← West");
        addDirectionButton(controls, Direction.SOUTH, "↓ South");
        addDirectionButton(controls, Direction.EAST, "→ East");
        bottomPanel.add(controls, BorderLayout.CENTER);

        messageLabel = new JLabel("Find the treasure (T). Avoid traps (X) and enemies (E).");
        bottomPanel.add(messageLabel, BorderLayout.SOUTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        render();
        frame.setSize(760, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.getRootPane().setDefaultButton(directionButtons.get(Direction.NORTH));

        frame.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.VK_W -> handleMove(Direction.NORTH);
                    case java.awt.event.KeyEvent.VK_DOWN, java.awt.event.KeyEvent.VK_S -> handleMove(Direction.SOUTH);
                    case java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.VK_A -> handleMove(Direction.WEST);
                    case java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.KeyEvent.VK_D -> handleMove(Direction.EAST);
                    default -> {
                    }
                }
            }
        });
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    private void addDirectionButton(JPanel panel, Direction direction, String label) {
        JButton button = new JButton(label);
        button.addActionListener((ActionEvent e) -> handleMove(direction));
        directionButtons.put(direction, button);
        panel.add(button);
    }

    private void handleMove(Direction direction) {
        if (game.getGameState() != GameState.PLAYING) {
            return;
        }
        MoveResult result = game.movePlayer(direction);
        showMessage(messageFor(result));
        render();
        if (game.getGameState() == GameState.WON || game.getGameState() == GameState.LOST) {
            askReplayOrExit();
        }
        frame.requestFocusInWindow();
    }

    private String messageFor(MoveResult result) {
        return switch (result) {
            case INVALID_MOVE -> "Can't move outside the grid.";
            case TRAP_TRIGGERED -> "Trap! You lost 3 additional moves.";
            case ENEMY_ENCOUNTERED -> "Enemy! You lost one life.";
            case WON -> "You found the treasure. You win!";
            case LOST -> "You ran out of moves or lives. You lose.";
            default -> "You moved.";
        };
    }

    @Override
    public void render() {
        if (boardPanel == null) {
            return;
        }
        boardPanel.removeAll();
        Grid grid = game.getGrid();
        Player player = game.getPlayer();

        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getColumns(); c++) {
                char symbol = (player.getRow() == r && player.getColumn() == c)
                        ? 'P' : grid.getTile(r, c).getSymbol();
                JButton cell = new JButton(String.valueOf(symbol));
                cell.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
                cell.setFocusable(false);
                final int targetRow = r;
                final int targetColumn = c;
                cell.addActionListener(e -> moveToward(targetRow, targetColumn));
                boardPanel.add(cell);
            }
        }

        statusLabel.setText(String.format("Moves: %d    Lives: %d    State: %s",
                player.getMovesLeft(), player.getLives(), game.getGameState()));
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void moveToward(int targetRow, int targetColumn) {
        Player player = game.getPlayer();
        int rowDelta = targetRow - player.getRow();
        int columnDelta = targetColumn - player.getColumn();

        // Only adjacent tiles are valid. Prefer the row direction if a diagonal is clicked.
        if (Math.abs(rowDelta) + Math.abs(columnDelta) != 1) {
            showMessage("Choose an adjacent tile.");
            return;
        }
        if (rowDelta < 0) handleMove(Direction.NORTH);
        else if (rowDelta > 0) handleMove(Direction.SOUTH);
        else if (columnDelta < 0) handleMove(Direction.WEST);
        else handleMove(Direction.EAST);
    }

    @Override
    public void showMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
        }
    }

    @Override
    public void askReplayOrExit() {
        Object[] options = {"New Game", "Exit"};
        int choice = JOptionPane.showOptionDialog(frame,
                "Game over: " + game.getGameState() + "\nWould you like to play again?",
                "Treasure Hunt", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
        if (choice == 0) {
            game.startNewGame();
            render();
            showMessage("New game started. Find the treasure!");
            frame.requestFocusInWindow();
        } else {
            frame.dispose();
        }
    }
}
