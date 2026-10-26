package treasurehunt.engine;

/** Overall result of attempting one movement. */
public enum MoveResult {
    INVALID_MOVE,
    MOVED,
    TREASURE_FOUND,
    TRAP_TRIGGERED,
    ENEMY_ENCOUNTERED,
    WON,
    LOST
}
