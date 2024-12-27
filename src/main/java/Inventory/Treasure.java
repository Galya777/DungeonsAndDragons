package Inventory;

import Characters.Hero;
import Characters.Position;

/**
 * Represents a generic treasure object in the game.
 * This interface defines the common functionality for all types of treasures
 * that can be found, used, or delivered by a hero in the game.
 * It also includes attributes and behaviors required for interaction with
 * the hero and the game world.
 */
public interface Treasure {

    Position getPosition();
    void setPosition(Position position);

    String INVALID_ARGUMENTS = "Invalid arguments.";

    String getName();

    String use(Hero hero);

    String getTreasureStats();

    int giveExperienceWhenCollected();
}
