package actions;


import Characters.Backpack;
import Characters.Hero;
import Inventory.Treasure;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Optional;

public class GiveCommand extends CommandImpl {

    private static final String PLAYER_NOT_FOUND_MESSAGE = "The player is not online.";
    private static final String PLAYER_NOT_HERE_MESSAGE = "The player is not in range.";
    private static final String WANTS_TO_SEND_TREASURE_MESSAGE = " wants to send you a treasure.";
    private static final String PLAYER_HAS_NO_SPACE_MESSAGE = "The player's backpack is full.";
    private static final String GAVE_TREASURE_MESSAGE = " gave you a treasure. ";
    private static final String GIVEN_TREASURE_MESSAGE = "You have given a treasure to ";
    private static final String TREASURE_NOT_FOUND_MESSAGE = "Treasure not found.";

    private static final int GIVE_COMMAND_LENGTH = 3;

    public Map<SocketChannel, Hero> heroBySocketChannel;

    public GiveCommand(Hero hero, String[] splitCommand, Map<SocketChannel, Hero> heroBySocketChannel) {
        super(hero, splitCommand);
        this.heroBySocketChannel = heroBySocketChannel;
    }

    private String executeGiveIfPlayerIsFound(SocketChannel socketChannelRecipient, UserRecipient userRecipient,
                                              Treasure treasure) {
        Hero heroRecipient = heroBySocketChannel.get(socketChannelRecipient);
        if (!heroRecipient.getPosition().equals(hero.getPosition())) {
            return PLAYER_NOT_HERE_MESSAGE;
        }
        String recipientCollectTreasure = heroRecipient.collectTreasure(treasure);
        String messageToRecipient;
        String messageToHero;
        if (recipientCollectTreasure.equals(Backpack.FULL_BACKPACK_MESSAGE)) {
            messageToRecipient = hero.getFormattedName() + WANTS_TO_SEND_TREASURE_MESSAGE + recipientCollectTreasure;
            messageToHero = PLAYER_HAS_NO_SPACE_MESSAGE;
        } else {
            messageToRecipient = hero.getFormattedName() + GAVE_TREASURE_MESSAGE + recipientCollectTreasure;
            messageToHero = GIVEN_TREASURE_MESSAGE + heroRecipient.getFormattedName();
        }

        userRecipient.setMessage(messageToRecipient);
        userRecipient.setSocketChannel(socketChannelRecipient);
        return messageToHero;
    }

    @Override
    public String execute(UserRecipient userRecipient) {
        if (splitCommand.length != GIVE_COMMAND_LENGTH) {
            return UNSUPPORTED_OPERATION_MESSAGE;
        }

        String item = splitCommand[FIRST_WORD].trim();
        Treasure treasure = hero.getTreasure(item);
        if (treasure == null) {
            return TREASURE_NOT_FOUND_MESSAGE;
        }

        String receiver = splitCommand[SECOND_WORD].trim();
        Optional<Map.Entry<SocketChannel, Hero>> entryToReceive = heroBySocketChannel.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(receiver)).findFirst();

        if (entryToReceive.isPresent()) {
            SocketChannel socketChannelRecipient = entryToReceive.get().getKey();
            return executeGiveIfPlayerIsFound(socketChannelRecipient, userRecipient, treasure);
        }

        return PLAYER_NOT_FOUND_MESSAGE;
    }

}
