package actions;


import Characters.Hero;
import Inventory.Treasure;
import graphicScenes.MapGenerator;

public class CollectCommand extends CommandImpl {

    private static final String NO_TREASURE_AT_POSITION_MESSAGE = "There is no treasure at this position.";
    private MapGenerator gameRepository;

    CollectCommand(Hero hero, String[] splitCommand, MapGenerator gameRepository) {
        super(hero, splitCommand);
        this.gameRepository = gameRepository;
    }

    @Override
    public String execute(UserRecipient userRecipient) {
        Treasure treasure = gameRepository.getTreasureAtPosition(hero.getPosition());
        if (treasure == null) {
            return NO_TREASURE_AT_POSITION_MESSAGE;
        }
        gameRepository.updateTakenTreasureAtPosition(hero.getPosition(), " " + hero.getId() + " ");
        return hero.collectTreasure(treasure);
    }

}
