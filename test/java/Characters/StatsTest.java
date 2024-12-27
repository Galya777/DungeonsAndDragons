package Characters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatsTest {

    @Test
    void decreaseHealth_damageGreaterThanDefenseAndHealthReduced() {
        Stats stats = Stats.getStatsInstance();
        int initialHealth = stats.getHealth();
        int damagePoints = 70; // damagePoints - defense = 20, less than health

        boolean result = stats.decreaseHealth(damagePoints);

        assertTrue(result);
        assertEquals(initialHealth - (damagePoints - stats.getDeffense()), stats.getHealth());
    }

    @Test
    void decreaseHealth_damageGreaterThanDefenseAndHealthSetToZero() {
        Stats stats = Stats.getStatsInstance();
        int damagePoints = 200; // extreme damage, should set health to 0

        boolean result = stats.decreaseHealth(damagePoints);

        assertTrue(result);
        assertEquals(0, stats.getHealth());
    }

    @Test
    void decreaseHealth_damageLessThanOrEqualToDefenseNoHealthChange() {
        Stats stats = Stats.getStatsInstance();
        int initialHealth = stats.getHealth();
        int damagePoints = stats.getDeffense(); // damage is equal to defense

        boolean result = stats.decreaseHealth(damagePoints);

        assertTrue(result);
        assertEquals(initialHealth, stats.getHealth());
    }

    @Test
    void decreaseHealth_negativeDamageNoHealthChange() {
        Stats stats = Stats.getStatsInstance();
        int initialHealth = stats.getHealth();
        int damagePoints = -10; // invalid negative damage

        boolean result = stats.decreaseHealth(damagePoints);

        assertTrue(result);
        assertEquals(initialHealth, stats.getHealth());
    }

    @Test
    void decreaseHealth_zeroDamageNoHealthChange() {
        Stats stats = Stats.getStatsInstance();
        int initialHealth = stats.getHealth();
        int damagePoints = 0; // no damage

        boolean result = stats.decreaseHealth(damagePoints);

        assertTrue(result);
        assertEquals(initialHealth, stats.getHealth());
    }
}