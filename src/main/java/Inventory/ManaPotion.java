package Inventory;


import Characters.Hero;
import Characters.Position;

public class ManaPotion extends Potion {

    ManaPotion(String name, int points, int experience, Position position) {
        super(name, points, experience, position);
    }

    @Override
    public void setPosition(Position position) {
    this.position = position;
    }

    @Override
    public String use(Hero hero) {
        if (hero.takeMana(points)) {
            return points + " mana points added to your hero!";
        }

        return null;
    }



    @Override
    public String getTreasureStats() {
        return "Mana potion <" + name + "> Mana points <" + points + ">";
    }

    public static ManaPotion createPotion(String name, int level, int experience, Position position) {
        if (name == null || level <= 0 || experience <= 0) {
            throw new IllegalArgumentException(INVALID_ARGUMENTS);
        }

        return new ManaPotion(name, level, experience, position);
    }
}
