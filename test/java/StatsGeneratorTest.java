import org.junit.jupiter.api.Test;

import javax.swing.JLabel;

import static org.junit.jupiter.api.Assertions.*;

class StatsGeneratorTest {

    @Test
    void testUpdateStatsUpdatesHealthLabel() {
        StatsGenerator statsGenerator = new StatsGenerator();

        statsGenerator.updateStats(120, 15, 10);

        JLabel healthLabel = (JLabel) statsGenerator.getComponent(0);
        assertEquals("Health: 120", healthLabel.getText());
    }

    @Test
    void testUpdateStatsUpdatesAttackLabel() {
        StatsGenerator statsGenerator = new StatsGenerator();

        statsGenerator.updateStats(100, 25, 10);

        JLabel attackLabel = (JLabel) statsGenerator.getComponent(1);
        assertEquals("Attack: 25", attackLabel.getText());
    }

    @Test
    void testUpdateStatsUpdatesDefenseLabel() {
        StatsGenerator statsGenerator = new StatsGenerator();

        statsGenerator.updateStats(100, 15, 20);

        JLabel defenseLabel = (JLabel) statsGenerator.getComponent(2);
        assertEquals("Defense: 20", defenseLabel.getText());
    }
}