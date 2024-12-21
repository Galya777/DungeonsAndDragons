package Inventory;

import Characters.Hero;
import Characters.Position;

public class Spell extends CombatTreasure {

    private int manaCost;

    Spell(String name, int level, int damage, int experience, int manaCost, Position position) {
        super(name, level, damage, experience, position);
        this.manaCost = manaCost;
    }

    public int getManaCost() {
        return manaCost;
    }

    @Override
    public String use(Hero hero) {
        if (hero.learn(this)) {
            return "Learned spell " + name + "! Attack points: " + attack + ", Mana cost: " + manaCost;
        }

        return "You need to be level " + level + " to use this spell.";
    }

    @Override
    public String getTreasureStats() {
        return "Spell <" + name + "> Attack points <" + attack + "> Mana cost <" + manaCost + ">";
    }

    public static Spell createSpell(String name, int level, int damage, int attack, int experience, Position position) {
        if (name == null || level <= 0 || attack <= 0 || damage <= 0 || experience <= 0) {
            throw new IllegalArgumentException(INVALID_ARGUMENTS);
        }

        return new Spell(name, level, damage, attack, experience, position);
    }

}
