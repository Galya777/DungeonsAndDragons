package actions;


import Characters.Hero;
import Characters.Position;
import graphicScenes.MapGenerator;

import java.util.Objects;

public class BackpackCommand extends CommandImpl {

    private static final String THROW_AT_FULL_POSITION_MESSAGE = "You can't throw treasure here.";
    private static final int BACKPACK_CHECK_COMMAND_LENGTH = 2;
    private static final int BACKPACK_COMMAND_LENGTH = 3;

    private MapGenerator gameRepository;

    public BackpackCommand(Hero hero, String[] splitCommand, MapGenerator gameRepository) {
        super(hero, splitCommand);
        this.gameRepository = gameRepository;
    }

    private String checkBackpackContents() {
        return hero.getBackpackContents();
    }

    private String useTreasureFromBackpack() {
        if (splitCommand.length != BACKPACK_COMMAND_LENGTH) {
            return UNSUPPORTED_OPERATION_MESSAGE;
        }

        return hero.useTreasure(splitCommand[SECOND_WORD].trim());
    }

    private String throwTreasureFromBackpack() {
        if (splitCommand.length != BACKPACK_COMMAND_LENGTH) {
            return UNSUPPORTED_OPERATION_MESSAGE;
        }

        if (!(Objects.equals(gameRepository.getAtPosition(hero.getPosition().getRow(), hero.getPosition().getCol()), "."))) {
            return THROW_AT_FULL_POSITION_MESSAGE;
        }

        String thrownTreasureMessage = hero.throwTreasure(splitCommand[SECOND_WORD].trim());
        gameRepository.updateThrownTreasure(new Position(hero.getPosition()), hero.getId(),
                hero.getTreasure(splitCommand[SECOND_WORD].trim()));
        return thrownTreasureMessage;
    }
    @Override
    public String execute(UserRecipient action) {
        return switch (action.getMessage().toUpperCase()) {
            case "CHECK" -> checkBackpackContents();
            case "USE" -> useTreasureFromBackpack();
            case "THROW" -> throwTreasureFromBackpack();
            default -> UNSUPPORTED_OPERATION_MESSAGE;
        };
    }



}

