package Inventory;

import Characters.Position;

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