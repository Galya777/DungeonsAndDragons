import javax.swing.*;
import java.awt.*;

public class StatsGenerator extends JPanel {
    private JLabel healthLabel;
    private JLabel attackLabel;
    private JLabel defenseLabel;

    public StatsGenerator() {
        setLayout(new GridLayout(3, 1));
        setBorder(BorderFactory.createTitledBorder("Character Stats"));

        healthLabel = new JLabel("Health: 100");
        attackLabel = new JLabel("Attack: 15");
        defenseLabel = new JLabel("Defense: 10");

        add(healthLabel);
        add(attackLabel);
        add(defenseLabel);
    }

    // Update stats
    public void updateStats(int health, int attack, int defense) {
        healthLabel.setText("Health: " + health);
        attackLabel.setText("Attack: " + attack);
        defenseLabel.setText("Defense: " + defense);
    }
}

