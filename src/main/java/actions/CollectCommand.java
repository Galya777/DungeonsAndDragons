package actions;

import Characters.Hero;
import Inventory.Treasure;
import graphicScenes.MapGenerator;

public class CollectCommand extends CommandImpl {

    private static final String NO_TREASURE_AT_POSITION_MESSAGE = "There is no treasure at this position.";
    private static final String TREASURE_COLLECTED_MESSAGE = "You collected a treasure: ";
    private MapGenerator gameRepository;

    public CollectCommand(Hero hero, String[] splitCommand, MapGenerator gameRepository) {
        super(hero, splitCommand);
        this.gameRepository = gameRepository;
    }

    @Override
    public String execute(UserRecipient userRecipient) {
        try {
            Treasure treasure = gameRepository.getTreasureAtPosition(hero.getPosition());
            if (treasure == null) {
                return NO_TREASURE_AT_POSITION_MESSAGE;
            }
            
            String collectionResult = hero.collectTreasure(treasure);
            gameRepository.updateTakenTreasureAtPosition(hero.getPosition(), " " + hero.getId() + " ");
            gameRepository.repaint(); // Ensure the map is updated visually
            
            return TREASURE_COLLECTED_MESSAGE + collectionResult;
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error in CollectCommand: " + e.getMessage());
            return "An error occurred while collecting the treasure.";
        }
    }
}