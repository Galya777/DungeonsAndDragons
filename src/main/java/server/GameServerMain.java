package server;

import graphicScenes.MapGenerator;

public class GameServerMain {
    public static void main(String[] args) {
        MapGenerator gameRepository=new MapGenerator();
        gameRepository.startEnemyMovement();
        GameServer gameServer = GameServer.createGameServer(gameRepository);
        gameServer.startGameServer();
    }
}
