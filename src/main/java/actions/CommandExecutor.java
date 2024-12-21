package actions;

import Characters.Hero;
import graphicScenes.MapGenerator;


import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collection;

public class CommandExecutor {

    private static final int ZERO_WORD = 0;
    private static final int SECOND_WORD = 2;
    private static final int PLAY_COMMAND_LENGTH = 3;
    private static final String YOU_ARE_NOT_REGISTERED_MESSAGE = "You are not registered yet.";
    private static final String COMMAND_DELIMITER = " ";

    private PlayerRepository playerRepository;
    private MapGenerator MapGenerator;

    public CommandExecutor(MapGenerator MapGenerator) {
        this.playerRepository = new PlayerRepository();
        this.MapGenerator = MapGenerator;
    }

    private Command determineCommand(String[] splitCommand, CommandType firstCommandAsEnum, SocketChannel socketChannel) {
        Hero hero = playerRepository.getHeroByGivenSocketChannel(socketChannel);
        switch (firstCommandAsEnum) {
            case BACKPACK:
                return new BackpackCommand(hero, splitCommand, MapGenerator);
            case GIVE:
                return new GiveCommand(hero, splitCommand, playerRepository.getHeroBySocketChannel());
            case COLLECT:
                return new CollectCommand(hero, splitCommand, MapGenerator);
            case BATTLE:
                return new BattleCommand(hero, splitCommand, playerRepository, MapGenerator);
            default:
                return null;
        }
    }

    public String checkCommandForRepositoryUpdate(String[] splitCommand, CommandType firstCommandAsEnum,
                                                  SocketChannel socketChannel) {
        if (firstCommandAsEnum.equals(CommandType.PLAY) && splitCommand.length == PLAY_COMMAND_LENGTH) {
            return playerRepository.registerUser(socketChannel, splitCommand[SECOND_WORD], MapGenerator);
        }

        if (!playerRepository.isUserRegistered(socketChannel)) {
            return YOU_ARE_NOT_REGISTERED_MESSAGE;
        }

        if (firstCommandAsEnum.equals(CommandType.QUIT)) {
            return playerRepository.removeUser(socketChannel, MapGenerator);
        }

        return null;
    }

    public String checkCommandResultForRepositoryUpdate(String commandResult, SocketChannel socketChannel,
                                                        UserRecipient userRecipient) {
        if (commandResult.equals(BattleCommand.HERO_LOST_MESSAGE)) {
            return commandResult + playerRepository.removeUser(socketChannel, MapGenerator);
        }

        if (userRecipient.getMessage() != null
                && userRecipient.getMessage().endsWith(BattleCommand.KILLED_YOU_MESSAGE)) {
            userRecipient.updateMessage(playerRepository.removeUser(userRecipient.getSocketChannel(), MapGenerator));
        }

        return commandResult;
    }

    public String executeCommand(String command, SocketChannel socketChannel, UserRecipient userRecipient) {
        String[] splitCommand = command.trim().split(COMMAND_DELIMITER);
        CommandType firstCommandAsEnum = CommandType.getCommandAsEnum(splitCommand[ZERO_WORD]);
        if (firstCommandAsEnum == null) {
            return Command.UNSUPPORTED_OPERATION_MESSAGE;
        }

        String updateResult = checkCommandForRepositoryUpdate(splitCommand, firstCommandAsEnum, socketChannel);
        if (updateResult != null) {
            return updateResult;
        }

        Command specificCommand = determineCommand(splitCommand, firstCommandAsEnum, socketChannel);
        String commandResult = (specificCommand != null) ? specificCommand.execute(userRecipient)
                : Command.UNSUPPORTED_OPERATION_MESSAGE;
        return checkCommandResultForRepositoryUpdate(commandResult, socketChannel, userRecipient);
    }

    public Collection<SocketChannel> getSocketChannelsFromRepository() {
        return playerRepository.getSocketChannels();
    }

    public String getDungeonMapFromRepository() {
        return Arrays.deepToString(MapGenerator.getMap());
    }
}
