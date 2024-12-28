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

    PlayerRepository playerRepository;
    private MapGenerator MapGenerator;

    public CommandExecutor(MapGenerator MapGenerator) {
        this.playerRepository = new PlayerRepository();
        this.MapGenerator = MapGenerator;
    }

    Command determineCommand(String[] splitCommand, CommandType firstCommandAsEnum, SocketChannel socketChannel) {

        // Check if user is registered
        if (!playerRepository.isUserRegistered(socketChannel)) {
            throw new IllegalStateException(YOU_ARE_NOT_REGISTERED_MESSAGE); // Fail fast if the user isn't registered
        }

        Hero hero = playerRepository.getHeroByGivenSocketChannel(socketChannel);

        // Ensure Hero is not null
        if (hero == null) {
            throw new IllegalStateException("Hero is null for SocketChannel: " + socketChannel);
        }

        return switch (firstCommandAsEnum) {
            case BACKPACK -> new BackpackCommand(hero, splitCommand, MapGenerator);
            case GIVE -> new GiveCommand(hero, splitCommand, playerRepository.getHeroBySocketChannel());
            case COLLECT -> new CollectCommand(hero, splitCommand, MapGenerator);
            case BATTLE -> new BattleCommand(hero, splitCommand, MapGenerator);
            default -> null;
        };
    }

    public String checkCommandForRepositoryUpdate(String[] splitCommand, CommandType firstCommandAsEnum,
                                                  SocketChannel socketChannel) {
        if (firstCommandAsEnum.equals(CommandType.PLAY) && splitCommand.length == PLAY_COMMAND_LENGTH) {
            String registrationResult = playerRepository.registerUser(socketChannel, splitCommand[SECOND_WORD], MapGenerator);
            if (registrationResult != null) {
                return registrationResult;
            }
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
        synchronized (playerRepository) { // Prevent race conditions
            String[] splitCommand = command.split(COMMAND_DELIMITER);

            userRecipient.setSocketChannel(socketChannel);
            userRecipient.setMessage(null);

            CommandType firstCommandAsEnum;
            try {
                firstCommandAsEnum = CommandType.valueOf(splitCommand[ZERO_WORD].toUpperCase());
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                return "Error: Invalid or unknown command.";
            }

            if (firstCommandAsEnum.equals(CommandType.PLAY)) {
                String repositoryUpdateResult = checkCommandForRepositoryUpdate(splitCommand, firstCommandAsEnum, socketChannel);
                return repositoryUpdateResult; // Ensure PLAY command response is sent immediately
            }
            String repositoryUpdateResult = checkCommandForRepositoryUpdate(splitCommand, firstCommandAsEnum, socketChannel);
            if (repositoryUpdateResult != null) {
                return repositoryUpdateResult;
            }

            Hero hero = playerRepository.getHeroByGivenSocketChannel(socketChannel);
            if (hero == null) {
                return "Error: No hero linked to your session. Please register or try again.";
            }

            Command determinedCommand = determineCommand(splitCommand, firstCommandAsEnum, socketChannel);
            return checkCommandResultForRepositoryUpdate(determinedCommand.execute(userRecipient), socketChannel, userRecipient);
        }
    }

    public Collection<SocketChannel> getSocketChannelsFromRepository() {
        return playerRepository.getSocketChannels();
    }

    public String getDungeonMapFromRepository() {
        return Arrays.deepToString(MapGenerator.getMap());
    }
}

