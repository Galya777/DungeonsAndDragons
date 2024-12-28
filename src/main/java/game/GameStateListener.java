package game;

public interface GameStateListener {
    void onGameStateChanged(GameState.GameEvent event, String playerId);
}
