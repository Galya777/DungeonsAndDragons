package Characters;

import Inventory.Spell;
import Inventory.Treasure;
import Inventory.Weapon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * The Hero class represents a powerful character in a game, capable of performing various actions
 * such as collecting treasures, equipping weapons, learning spells, and gaining experience. It
 * extends the CharImpl class, inheriting core character properties and behaviors.
 *
 * The Hero class manages its interactions with a backpack for inventory, a level system for
 * progression, and a stats for health and mana management. The Hero can also engage in battles
 * and drop loot upon defeat.
 */
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
    public int getHealth() {
        return stats.getHealth(); // Assuming stats manages the health
    }
    public boolean isAlive() {
        return stats.isAlive(); // Assuming stats maintains whether the hero is alive
    }
    public void decreaseHealth(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage value cannot be negative.");
        }

        stats.decreaseHealth(damage);
        System.out.println(this.getName() + " took " + damage + " damage. Current health: " + stats.getHealth());

        // Check if the hero is still alive
        if (!stats.isAlive()) {
            System.out.println(this.getName() + " has been defeated.");
            dropLootWhenDead(); // Call the treasure-dropping method
        }
        if (getHealth() <= 0) {
            System.out.println("Game Over!"); // Debug message
            gameOver(); // Call the game-over logic
        } else {
            System.out.println("Hero's updated health: " +getHealth()); // Debug message
        }
    }
    // This should trigger when the game ends
    private void gameOver() {
        System.out.println("Game Over! Final score: XYZ");
    }
    private void dropLootWhenDead() {
        Treasure treasure = throwTreasureWhenDead();
        if (treasure != null) {
            System.out.println(this.getName() + " has dropped treasure: " + treasure.getName());
            treasure.setPosition(this.getPosition());
            // This is where you can add the treasure to the game map
        }
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


    public void setPosition(Position position) {
        this.position = position;
    }
}
