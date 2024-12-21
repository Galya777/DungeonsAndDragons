package Inventory;

import Characters.Hero;
import Characters.Position;

import java.awt.*;

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
