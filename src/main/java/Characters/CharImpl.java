package Characters;

import Inventory.Spell;
import Inventory.Weapon;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;

public class CharImpl implements Character {
    private static final int NO_ATTACK_POINTS = 0;
    private String name;
    protected String id;
    protected Position position;
    protected Weapon currentWeapon;
    protected Spell currentSpell;
    protected Stats stats;
    private Image sprite; // Image for the player

    protected CharImpl(String name, String id, Position position, String imagePath) {
        this.name = name;
        this.id = id;
        this.position = position;
        stats = Stats.getStatsInstance();
        this.sprite = new ImageIcon(imagePath).getImage(); // Load image
    }
    @Override
    public void draw(Graphics g) {
        g.drawImage(sprite, position.getRow(), position.getCol(), null); // Draw the image at the player's position
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isAlive() {
        return stats.getHealth() > 0;
    }

    @Override
    public Stats getStats() {
        return stats;
    }

    @Override
    public Weapon getWeapon() {
        return currentWeapon;
    }

    @Override
    public Spell getSpell() {
        return currentSpell;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(int row, int col) {
        this.position.setRow(row);
        this.position.setCol(col);
    }

    @Override
    public boolean takeDamage(int damagePoints) {
        if (!isAlive() || damagePoints < 0) {
            return false;
        }

        return stats.decreaseHealth(damagePoints);
    }

    @Override
    public int giveExperiencePointsAfterBattle() {
        return stats.getAttack();
    }

    @Override
    public int attack() {
        int attack = stats.getAttack();
        if (currentSpell == null) {
            return attack + ((currentWeapon == null) ? NO_ATTACK_POINTS : currentWeapon.getAttack());
        }

        if (currentWeapon == null || currentWeapon.getAttack() < currentSpell.getAttack()) {
            if (stats.useMana(currentSpell.getManaCost())) {
                return attack + currentSpell.getAttack();
            } else {
                return attack + ((currentWeapon == null) ? NO_ATTACK_POINTS : currentWeapon.getAttack());
            }
        }

        return attack;
    }
    public void writeHero(DataOutputStream out) throws IOException {
        // Write key fields to the output stream
        out.writeUTF(this.getName());                     // Write name
        out.writeInt(this.getStats().getHealth());        // Write health
        out.writeInt(this.getStats().getMana());          // Write mana
        out.writeInt(this.getPosition().getRow());        // Write position row
        out.writeInt(this.getPosition().getCol());        // Write position column
    }
    @Override
    public String getFormattedName() {
        return name + " <" + id + ">";
    }

}
