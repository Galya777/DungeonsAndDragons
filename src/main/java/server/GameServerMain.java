package server;

import graphicScenes.MapGenerator;

/**
 * The GameServerMain class serves as the entry point for the game server application.
 * It initializes necessary components and starts the game server.
 *
 * Responsibilities of this class include:
 * - Creating an instance of the MapGenerator, which acts as the repository for game data.
 * - Using the MapGenerator to create a GameServer instance.
 * - Starting the GameServer to begin processing game logic and handling server tasks.
 *
 * This class is designed to be the main executable for the server-side component of the game.
 */
public class GameServerMain {
    public static void main(String[] args) {
        MapGenerator gameRepository=new MapGenerator();
        GameServer gameServer = GameServer.createGameServer(gameRepository);
        gameServer.startGameServer();
    }
}
