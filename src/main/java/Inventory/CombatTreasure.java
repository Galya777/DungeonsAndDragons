package Inventory;

import Characters.Hero;
import Characters.Position;

import java.awt.*;

/**
 * Represents a combat treasure in the game that provides both treasure functionality and combat benefits.
 * CombatTreasure is a type of Treasure that can be collected, used, and interacted with by a hero in the game.
 * It has additional attributes relevant to combat, such as attack points, level requirements, and experience points.
 * This class serves as a base class for specific combat-related treasures (e.g., weapons, spells).
 */
public class CombatTreasure implements Treasure {

    protected Position position;
    protected String name;
    protected int level;
    protected int attack;
    protected int experience;
    private Image sprite; // Image for the player

    protected CombatTreasure(String name, int level, int attack, int experience, Position position) {
        this.name = name;
        this.level = level;
        this.attack = attack;
        this.experience = experience;
        this.position = position;
    }


    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
    this.position = position;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String use(Hero hero) {
        return "";
    }

    @Override
    public String getTreasureStats() {
        return "";
    }

    public int getAttack() {
        return attack;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public int giveExperienceWhenCollected() {
        return experience;
    }

}
