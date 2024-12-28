package Characters;
import Inventory.Spell;
import Inventory.Weapon;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The Character interface defines the blueprint for a character in the game.
 * It encapsulates the essential behaviors and attributes that every character must have,
 * including rendering, combat functionality, and state management.
 */
public interface Character {

        void draw(Graphics g); // Method to render the object

        String getName();

        String getId();

        boolean isAlive();

        Weapon getWeapon();

        Spell getSpell();

        Position getPosition();

        void setPosition(int row, int col);

        boolean takeDamage(int damagePoints);

        boolean attemptDodge();
        
        int calculateDamage();

        int giveExperiencePointsAfterBattle();

        int attack();

        Stats getStats();

        String getFormattedName();

        void writeHero(DataOutputStream out) throws IOException;

}
