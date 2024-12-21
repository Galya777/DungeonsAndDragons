package Characters;
import Inventory.Spell;
import Inventory.Weapon;

import java.awt.*;

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

        int giveExperiencePointsAfterBattle();

        int attack();

        Stats getStats();

        String getFormattedName();


}
