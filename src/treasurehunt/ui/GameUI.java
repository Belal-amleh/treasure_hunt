package treasurehunt.ui;

public interface GameUI {
    void start();
    void render();
    void showMessage(String message);
    void askReplayOrExit();
}
