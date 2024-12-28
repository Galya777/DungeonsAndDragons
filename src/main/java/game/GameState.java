package game;

import Characters.Hero;
import Characters.Position;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private static GameState instance;
    private final ConcurrentHashMap<String, Hero> players;
    private final AtomicBoolean gameRunning;
    private final List<GameStateListener> listeners;

    private GameState() {
        this.players = new ConcurrentHashMap<>();
        this.gameRunning = new AtomicBoolean(false);
        this.listeners = new ArrayList<>();
    }

    public static synchronized GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void addPlayer(String playerId, Hero hero) {
        players.put(playerId, hero);
        notifyListeners(GameEvent.PLAYER_JOINED, playerId);
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
        notifyListeners(GameEvent.PLAYER_LEFT, playerId);
    }

    public Hero getPlayer(String playerId) {
        return players.get(playerId);
    }

    public void updatePlayerPosition(String playerId, Position newPosition) {
        Hero hero = players.get(playerId);
        if (hero != null) {
            hero.setPosition(newPosition);
            notifyListeners(GameEvent.PLAYER_MOVED, playerId);
        }
    }

    public void addListener(GameStateListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(GameEvent event, String playerId) {
        for (GameStateListener listener : listeners) {
            listener.onGameStateChanged(event, playerId);
        }
    }

    public void startGame() {
        gameRunning.set(true);
        notifyListeners(GameEvent.GAME_STARTED, null);
    }

    public void stopGame() {
        gameRunning.set(false);
        notifyListeners(GameEvent.GAME_STOPPED, null);
    }

    public boolean isGameRunning() {
        return gameRunning.get();
    }

    public enum GameEvent {
        PLAYER_JOINED,
        PLAYER_LEFT,
        PLAYER_MOVED,
        GAME_STARTED,
        GAME_STOPPED
    }
}
