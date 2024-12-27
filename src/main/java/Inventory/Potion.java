package Inventory;

import Characters.Position;

/**
 * Represents an abstract Potion that acts as a type of Treasure in the game.
 * Potions provide specific benefits like healing points or other effects
 * when used by a Hero in the game. This class serves as a base for specific
 * potion types like HealthPotion and ManaPotion.
 *
 * Implements the Treasure interface for integration with inventory and treasure-related
 * mechanics in the game. Each Potion has a name, healing points, experience points
 * gained when collected, and a position on the game map.
 */
public abstract class Potion implements Treasure {

    protected String name;
    protected int points;
    protected int experience;
    protected Position position;

    protected Potion(String name, int points, int experience, Position position) {
        this.name = name;
        this.points = points;
        this.experience = experience;
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public String getName() {
        return name;
    }

    public int heal() {
        return points;
    }

    @Override
    public int giveExperienceWhenCollected() {
        return experience;
    }

}