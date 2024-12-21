package Characters;

import Inventory.Spell;
import Inventory.Treasure;
import Inventory.Weapon;

public class Hero extends CharImpl{
    private Level level;
    private Backpack backpack;
    private final static String NULL_ARGUMENT_MESSAGE = "Name cannot be null.";
    private final static String OWNERSHIP_TREASURE_FALSE_MESSAGE = "Cannot use treasures that are not yours.";

    public Hero(String name, String id, Position position, String imagePath) {
        super(name, id, position, imagePath);
        level = Level.getLevelInstance();
        backpack = Backpack.getBackpackInstance();
    }

    public boolean takeHealing(int healthPoints) {
        if (!isAlive() || healthPoints < 0) {
            return false;
        }

        return stats.increaseHealth(healthPoints);
    }

    public boolean takeMana(int manaPoints) {
        if (!isAlive() || manaPoints < 0) {
            return false;
        }

        return stats.increaseMana(manaPoints);
    }

    public void gainExperience(int points) {
        if (!isAlive() || points < 0) {
            return;
        }

        if (level.gainExperience(points)) {
            stats.increaseForReachingNewLevel();
        }
    }

    public String collectTreasure(Treasure treasure) {
        String collectedMessage = backpack.addItem(treasure);
        if (collectedMessage.endsWith(Backpack.ADDED_TO_BACKPACK_MESSAGE)) {
            gainExperience(treasure.giveExperienceWhenCollected());
        }
        return collectedMessage;
    }

    public String throwTreasure(String item) {
        return backpack.throwItem(item);
    }

    public boolean equip(Weapon weapon) {
        if (weapon == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
        }

        if (weapon.getLevel() > level.getLevel()) {
            return false;
        }

        if (this.currentWeapon != null) {
            backpack.addItem(currentWeapon);
        }

        currentWeapon = weapon;
        return true;
    }

    public boolean learn(Spell spell) {
        if (spell == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
        }

        if (spell.getLevel() > level.getLevel()) {
            return false;
        }

        if (this.currentSpell != null) {
            backpack.addItem(currentSpell);
        }

        currentSpell = spell;
        return true;
    }

    public String useTreasure(String name) {
        Treasure treasure = getTreasure(name);
        return (treasure != null) ? treasure.use(this) : OWNERSHIP_TREASURE_FALSE_MESSAGE;
    }

    public void move(int dx, int dy) {
        position.setRow(position.getRow() + dx);
        position.setCol(position.getCol()+ dy);
    }

    public Treasure getTreasure(String name) {
        return backpack.getItem(name);
    }

    public Treasure throwTreasureWhenDead() {
        return backpack.throwRandomTreasure();
    }

    public String getBackpackContents() {
        return backpack.listContents();
    }

    @Override
    public int giveExperiencePointsAfterBattle() {
        return level.getExperience();
    }
}
